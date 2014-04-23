package com.psclistens.example.jsf.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.crud.CrudMode;
import com.psclistens.example.domain.OrderStatus;
import com.psclistens.example.jsf.FacesContextUtil;
import com.psclistens.example.service.OrderService;
import com.psclistens.example.service.filter.request.OrderByDirection;
import com.psclistens.example.service.filter.request.OrderFilterRequest;
import com.psclistens.example.service.filter.request.OrderOrderByField;
import com.psclistens.example.service.filter.response.OrderFilterResponse;
import com.psclistens.example.service.vo.OrderListVO;

/**
 * This is the backing bean for the order list view.
 * 
 * @author LYNCHNF
 */
@ManagedBean
@ViewScoped
public class OrderListBacking implements Serializable {
    private static final long serialVersionUID = 1L;
    private OrderFilterRequest filterRequest;
    private OrderFilterResponse filterResponse;
    private Long whereId;
    private Long whereCustomerId;
    private String whereCustomerName;
    private Date whereEntryDate;
    private OrderStatus whereOrderStatus;

    private int pageSize;
    private DataModel<OrderListRow> resultList;
    private boolean rebuildResultList = true;

    public OrderListBacking() {
        filterRequest = (OrderFilterRequest) FacesContextUtil.getFromFlash("filterRequest");
        if (filterRequest == null) filterRequest = new OrderFilterRequest();
    }

    public Long getWhereId() {
        return filterRequest.getWhereId();
    }

    public void setWhereId(Long whereId) {
        this.whereId = whereId;
    }

    public Long getWhereCustomerId() {
        return filterRequest.getWhereCustomerId();
    }

    public void setWhereCustomerId(Long whereCustomerId) {
        this.whereCustomerId = whereCustomerId;
    }

    public String getWhereCustomerName() {
        return filterRequest.getWhereCustomerName();
    }

    public void setWhereCustomerName(String whereCustomerName) {
        this.whereCustomerName = whereCustomerName;
    }

    public Date getWhereEntryDate() {
        return filterRequest.getWhereEntryDate();
    }

    public void setWhereEntryDate(Date whereEntryDate) {
        this.whereEntryDate = whereEntryDate;
    }

    public OrderStatus getWhereOrderStatus() {
        return filterRequest.getWhereOrderStatus();
    }

    public void setWhereOrderStatus(OrderStatus whereOrderStatus) {
        this.whereOrderStatus = whereOrderStatus;
    }

    public OrderStatus[] getOrderStatusOptions() {
        return OrderStatus.values();
    }

    public int getPageNumber() {
        return filterRequest.getFirst() / filterRequest.getMax() + 1;
    }

    public int getTotalPages() {
        return (getFilterResponse().getCount().intValue() - 1) / filterRequest.getMax() + 1;
    }

    public int getPageSize() {
        return filterRequest.getMax();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int[] getPageSizeOptions() {
        return OrderFilterRequest.PAGE_SIZE_OPTIONS;
    }

    public DataModel<OrderListRow> getResultList() {
        if (rebuildResultList) {
            List<OrderListVO> orders = getFilterResponse().getResultList();
            Collection<Long> checkedIds = getCheckedIds();
            List<OrderListRow> rows = new ArrayList<OrderListRow>();
            for (OrderListVO order : orders) {
                boolean checked = checkedIds.contains(order.getId());
                OrderListRow row = new OrderListRow(order);
                row.setChecked(checked);
                rows.add(row);
            }
            resultList = new ListDataModel<OrderListRow>(rows);
            rebuildResultList = false;
        }
        return resultList;
    }

    public String reload() {
        return "OrderList";
    }

    public String changeWhere() {
        filterRequest.setWhereId(whereId);
        filterRequest.setWhereCustomerId(whereCustomerId);
        filterRequest.setWhereCustomerName(whereCustomerName);
        filterRequest.setWhereEntryDate(whereEntryDate);
        filterRequest.setWhereOrderStatus(whereOrderStatus);
        filterRequest.setOrderByField(OrderFilterRequest.DEFAULT_ORDER_BY_FIELD);
        filterRequest.setOrderByDirection(OrderFilterRequest.DEFAULT_ORDER_BY_DIRECTION);
        filterRequest.setFirst(0);
        filterResponse = null;
        rebuildResultList = true;
        return null;
    }

    public String changeOrderBy() {
        String orderByFieldStr = FacesContextUtil.getRequestParameter("orderByField");
        OrderOrderByField orderByField = OrderOrderByField.valueOf(orderByFieldStr);
        OrderByDirection orderByDirection = OrderFilterRequest.DEFAULT_ORDER_BY_DIRECTION;
        if (orderByField == filterRequest.getOrderByField()) {
            if (filterRequest.getOrderByDirection() == OrderByDirection.ASC) {
                orderByDirection = OrderByDirection.DESC;
            } else {
                orderByDirection = OrderByDirection.ASC;
            }
        }

        filterRequest.setOrderByField(orderByField);
        filterRequest.setOrderByDirection(orderByDirection);
        filterRequest.setFirst(0);
        filterResponse = null;
        rebuildResultList = true;
        return null;
    }

    public String changePageNumber() {
        String pageNumberStr = FacesContextUtil.getRequestParameter("pageNumber");
        int pageNumber = Integer.parseInt(pageNumberStr);

        filterRequest.setFirst((pageNumber - 1) * pageSize);
        filterResponse = null;
        rebuildResultList = true;
        return null;
    }

    public void changePageSize(ValueChangeEvent event) {
        Integer newValue = (Integer) event.getNewValue();
        pageSize = newValue.intValue();

        filterRequest.setFirst(0);
        filterRequest.setMax(pageSize);
        filterResponse = null;
        rebuildResultList = true;
    }

    public String add() {
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.ADD);
        FacesContextUtil.putInFlash("originalId", null);
        return "OrderEdit";
    }

    public String copy() {
        Collection<Long> checkedIds = getCheckedIds();

        // If the copy button was pressed, we should have exactly one checked row.
        if (checkedIds.isEmpty()) {
            FacesContextUtil.addErrorMessage("Please check a row before pressing the Copy button.");
        } else if (checkedIds.size() > 1) {
            FacesContextUtil.addErrorMessage("Please check only one row before pressing the Copy button.");
        }

        // If errors, redisplay this page.
        if (FacesContextUtil.hasErrors()) return null;

        // Otherwise, go to the edit page.
        Long originalId = checkedIds.iterator().next();
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.COPY);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "OrderEdit";
    }

    public String edit() {
        Long originalId = resultList.getRowData().getOrderListVO().getId();
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.UPDATE);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "OrderEdit";
    }

    private OrderFilterResponse getFilterResponse() {
        if (filterResponse == null) {
            try {
                filterResponse = OrderService.filterOrders(filterRequest);
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        }
        return filterResponse;
    }

    private Collection<Long> getCheckedIds() {
        Collection<Long> checkedIds = new HashSet<Long>();
        if (resultList != null) {
            for (OrderListRow row : resultList) {
                if (row.isChecked()) {
                    checkedIds.add(row.getOrderListVO().getId());
                }
            }
        }
        return checkedIds;
    }
}