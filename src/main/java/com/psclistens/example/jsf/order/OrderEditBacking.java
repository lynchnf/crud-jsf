package com.psclistens.example.jsf.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.crud.CrudMode;
import com.psclistens.example.domain.OrderStatus;
import com.psclistens.example.jsf.FacesContextUtil;
import com.psclistens.example.service.OrderService;
import com.psclistens.example.service.filter.request.OrderFilterRequest;
import com.psclistens.example.service.vo.OrderEditHeaderVO;
import com.psclistens.example.service.vo.OrderEditLineVO;

/**
 * This is the backing bean for the order edit view.
 * 
 * @author LYNCHNF
 */
@ManagedBean
@ViewScoped
public class OrderEditBacking implements Serializable {
    private static final long serialVersionUID = 1L;
    private OrderFilterRequest filterRequest;
    private CrudMode mode;
    private Long originalId;

    private OrderEditHeaderVO order;
    private Long customerId;
    private Date entryDate;
    private OrderStatus orderStatus;
    private DataModel<OrderEditRow> lines;
    private boolean rebuildLines = true;

    public OrderEditBacking() {
        filterRequest = (OrderFilterRequest) FacesContextUtil.getFromFlash("filterRequest");
        mode = (CrudMode) FacesContextUtil.getFromFlash("mode");
        originalId = (Long) FacesContextUtil.getFromFlash("originalId");

        if (mode == CrudMode.ADD) {
            order = new OrderEditHeaderVO();
            customerId = null;
            entryDate = new Date();
            orderStatus = OrderStatus.ENTERED;
        } else {
            try {
                order = OrderService.getOrderForEdit(Long.valueOf(originalId));
                if (order == null) {
                    FacesContextUtil.addErrorMessage("Order with id=" + originalId + " not found.");
                } else {
                    if (mode == CrudMode.COPY) {
                        order.setId(null);
                        order.setVersion(null);
                    }
                    customerId = order.getCustomerId();
                    entryDate = order.getEntryDate();
                    orderStatus = order.getOrderStatus();
                }
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        }
    }

    public CrudMode getMode() {
        return mode;
    }

    public Long getOriginalId() {
        return originalId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return order.getCustomerName();
    }

    public BigDecimal getCustomerDiscount() {
        return order.getCustomerDiscount();
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getOrderSubTotal() {
        return order.getOrderSubTotal();
    }

    public BigDecimal getOrderDiscount() {
        return order.getOrderDiscount();
    }

    public BigDecimal getOrderFinalTotal() {
        return order.getOrderFinalTotal();
    }

    public DataModel<OrderEditRow> getLines() {
        if (rebuildLines) {
            List<OrderEditRow> rows = new ArrayList<OrderEditRow>();
            for (OrderEditLineVO orderEditLineVO : order.getLines()) {
                OrderEditRow row = new OrderEditRow(orderEditLineVO);
                row.setNewLine(false);
                rows.add(row);
            }
            if (orderStatus == OrderStatus.ENTERED) {
                OrderEditRow newRow = new OrderEditRow(new OrderEditLineVO());
                newRow.getOrderEditLineVO().setSku(order.getNewLineSku());
                newRow.getOrderEditLineVO().setQuantity(order.getNewLineQuantity());
                newRow.setNewLine(true);
                rows.add(newRow);
            }
            lines = new ListDataModel<OrderEditRow>(rows);
            rebuildLines = false;
        }
        return lines;
    }

    public String cancelOrder() {
        try {
            Long orderId2 = OrderService.cancelOrder(originalId);
            FacesContextUtil.addInfoMessage("Order with id=" + orderId2 + " has been canceled.");
        } catch (CrudException e) {
            FacesContextUtil.addErrorMessage(e.getMessage());
        }

        // If errors, redisplay this page.
        if (FacesContextUtil.hasErrors()) {
            order.setCustomerId(customerId);
            order.setEntryDate(entryDate);
            order.setOrderStatus(orderStatus);
            try {
                order = OrderService.refreshOrderForEdit(order, true);
                customerId = order.getCustomerId();
                entryDate = order.getEntryDate();
                orderStatus = order.getOrderStatus();
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
            return null;
        }

        // Otherwise, go back to list page.
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "OrderList";
    }

    public String save() {
        copyUItoVO();

        // If no UI errors, do business validation.
        if (!FacesContextUtil.hasErrors()) {
            List<String> errors = OrderService.validateSaveOrder(order);
            for (String error : errors) {
                FacesContextUtil.addErrorMessage(error);
            }
        }

        // If no UI or business errors, do business processing.
        if (!FacesContextUtil.hasErrors()) {
            try {
                Long orderId2 = OrderService.saveOrder(order);
                FacesContextUtil.addInfoMessage("Order with id=" + orderId2 + " saved successfully.");
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        }

        // If errors, redisplay this page.
        if (FacesContextUtil.hasErrors()) {
            refreshUIfromVO();
            return null;
        }

        // Otherwise, go back to list page.
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "OrderList";
    }

    public String recalculate() {
        copyUItoVO();
        
        // If no UI errors, do business validation.
        if (!FacesContextUtil.hasErrors()) {
            List<String> errors = OrderService.validateSaveOrder(order);
            for (String error : errors) {
                FacesContextUtil.addErrorMessage(error);
            }
        }
        
        refreshUIfromVO();
        return null;
    }

    public String reload() {
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", mode);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "OrderEdit";
    }

    public String cancel() {
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "OrderList";
    }

    private void copyUItoVO() {
        order.setCustomerId(customerId);
        order.setEntryDate(entryDate);
        order.setOrderStatus(orderStatus);
        order.getLines().clear();
        int i = 1;
        for (OrderEditRow line : lines) {
            if (line.isNewLine()) {
                order.setNewLineSku(line.getOrderEditLineVO().getSku());
                order.setNewLineQuantity(line.getOrderEditLineVO().getQuantity());

                // If either sku or quantity is entered, both must be entered.
                if (line.getOrderEditLineVO().getSku() != null || line.getOrderEditLineVO().getQuantity() != null) {
                    if (line.getOrderEditLineVO().getSku() == null) {
                        FacesContextUtil.addErrorMessage("If Quantity is not blank, SKU is required for line " + i + ".");
                    }
                    if (line.getOrderEditLineVO().getQuantity() == null) {
                        FacesContextUtil.addErrorMessage("If SKU is not blank, Quantity is required for line " + i + ".");
                    }
                }
            } else {
                order.getLines().add(line.getOrderEditLineVO());

                // If line is not deleted, validate that quantity is not blank.
                if (!line.getOrderEditLineVO().isRemoved() && line.getOrderEditLineVO().getQuantity() == null) {
                    FacesContextUtil.addErrorMessage("Quantity is required for line " + i + ".");
                }
            }
            i++;
        }
    }

    private void refreshUIfromVO() {
        try {
            boolean addNewLine = !FacesContextUtil.hasErrors();
            order = OrderService.refreshOrderForEdit(order, addNewLine);
            customerId = order.getCustomerId();
            entryDate = order.getEntryDate();
            orderStatus = order.getOrderStatus();
            rebuildLines = true;
        } catch (CrudException e) {
            FacesContextUtil.addErrorMessage(e.getMessage());
        }
    }
}