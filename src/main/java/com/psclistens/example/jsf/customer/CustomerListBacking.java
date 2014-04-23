package com.psclistens.example.jsf.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.crud.CrudMode;
import com.psclistens.example.domain.Customer;
import com.psclistens.example.jsf.FacesContextUtil;
import com.psclistens.example.service.CustomerService;
import com.psclistens.example.service.filter.request.CustomerFilterRequest;
import com.psclistens.example.service.filter.request.CustomerOrderByField;
import com.psclistens.example.service.filter.request.OrderByDirection;
import com.psclistens.example.service.filter.response.CustomerFilterResponse;

/**
 * This is the backing bean for the customer list view.
 * 
 * @author LYNCHNF
 */
@ManagedBean
@ViewScoped
public class CustomerListBacking implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(CustomerListBacking.class);
    private CustomerFilterRequest filterRequest;
    private CustomerFilterResponse filterResponse;
    private Long whereId;
    private String whereName;
    private BigDecimal whereDiscount;
    private int pageSize;
    private DataModel<CustomerRow> resultList;
    private boolean rebuildResultList = true;

    public CustomerListBacking() {
        log.trace("constructed"); // DEBUG
        filterRequest = (CustomerFilterRequest) FacesContextUtil.getFromFlash("filterRequest");
        if (filterRequest == null) filterRequest = new CustomerFilterRequest();
    }

    public Long getWhereId() {
        log.trace("getWhereId returns \"" + filterRequest.getWhereId() + "\""); // DEBUG
        return filterRequest.getWhereId();
    }

    public void setWhereId(Long whereId) {
        log.trace("setWhereId(" + whereId + ")"); // DEBUG
        this.whereId = whereId;
    }

    public String getWhereName() {
        log.trace("getWhereName returns \"" + filterRequest.getWhereName() + "\""); // DEBUG
        return filterRequest.getWhereName();
    }

    public void setWhereName(String whereName) {
        log.trace("setWhereName(" + whereName + ")"); // DEBUG
        this.whereName = whereName;
    }

    public BigDecimal getWhereDiscount() {
        log.trace("getWhereDiscount returns \"" + filterRequest.getWhereDiscount() + "\""); // DEBUG
        return filterRequest.getWhereDiscount();
    }

    public void setWhereDiscount(BigDecimal whereDiscount) {
        log.trace("setWhereDiscount(" + whereDiscount + ")"); // DEBUG
        this.whereDiscount = whereDiscount;
    }

    public int getPageNumber() {
        log.trace("getPageNumber returns \"" + (filterRequest.getFirst() / filterRequest.getMax() + 1) + "\""); // DEBUG
        return filterRequest.getFirst() / filterRequest.getMax() + 1;
    }

    public int getTotalPages() {
        log.trace("getTotalPages starting"); // DEBUG
        int totalPages = (getFilterResponse().getCount().intValue() - 1) / filterRequest.getMax() + 1;
        log.trace("getTotalPages returns \"" + totalPages + "\""); // DEBUG
        return totalPages;
    }

    public int getPageSize() {
        log.trace("getPageSize returns \"" + filterRequest.getMax() + "\""); // DEBUG
        return filterRequest.getMax();
    }

    public void setPageSize(int pageSize) {
        log.trace("setPageSize(" + pageSize + ")"); // DEBUG
        this.pageSize = pageSize;
    }

    public int[] getPageSizeOptions() {
        log.trace("getPageSizeOptions returns \"" + Arrays.toString(CustomerFilterRequest.PAGE_SIZE_OPTIONS) + "\""); // DEBUG
        return CustomerFilterRequest.PAGE_SIZE_OPTIONS;
    }

    public DataModel<CustomerRow> getResultList() {
        log.trace("getResultList starting"); // DEBUG
        if (rebuildResultList) {
            List<Customer> customers = getFilterResponse().getResultList();
            Collection<Long> checkedIds = getCheckedIds();
            List<CustomerRow> rows = new ArrayList<CustomerRow>();
            for (Customer customer : customers) {
                boolean checked = checkedIds.contains(customer.getId());
                CustomerRow row = new CustomerRow(customer);
                row.setChecked(checked);
                rows.add(row);
            }
            resultList = new ListDataModel<CustomerRow>(rows);
            rebuildResultList = false;
        }
        log.trace("getResultList returns \"" + resultList.getRowCount() + "\" rows"); // DEBUG
        return resultList;
    }

    public String reload() {
        log.trace("reload executing"); // DEBUG
        return "CustomerList";
    }

    public String changeWhere() {
        log.trace("changeWhere executing"); // DEBUG
        filterRequest.setWhereId(whereId);
        filterRequest.setWhereName(whereName);
        filterRequest.setWhereDiscount(whereDiscount);
        filterRequest.setOrderByField(CustomerFilterRequest.DEFAULT_ORDER_BY_FIELD);
        filterRequest.setOrderByDirection(CustomerFilterRequest.DEFAULT_ORDER_BY_DIRECTION);
        filterRequest.setFirst(0);
        filterResponse = null;
        rebuildResultList = true;
        return null;
    }

    public String changeOrderBy() {
        log.trace("changeOrderBy executing"); // DEBUG
        String orderByFieldStr = FacesContextUtil.getRequestParameter("orderByField");
        CustomerOrderByField orderByField = CustomerOrderByField.valueOf(orderByFieldStr);
        OrderByDirection orderByDirection = CustomerFilterRequest.DEFAULT_ORDER_BY_DIRECTION;
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
        log.trace("changePageNumber executing"); // DEBUG
        String pageNumberStr = FacesContextUtil.getRequestParameter("pageNumber");
        int pageNumber = Integer.parseInt(pageNumberStr);

        filterRequest.setFirst((pageNumber - 1) * pageSize);
        filterResponse = null;
        rebuildResultList = true;
        return null;
    }

    public void changePageSize(ValueChangeEvent event) {
        log.trace("changePageSize executing"); // DEBUG
        Integer newValue = (Integer) event.getNewValue();
        pageSize = newValue.intValue();

        filterRequest.setFirst(0);
        filterRequest.setMax(pageSize);
        filterResponse = null;
        rebuildResultList = true;
    }

    public String delete() {
        log.trace("delete executing"); // DEBUG
        Collection<Long> checkedIds = getCheckedIds();

        // If the delete button was pressed, we should have at least one checked row.
        if (checkedIds.isEmpty()) {
            FacesContextUtil.addErrorMessage("Please check at least one row before pressing the Delete button.");
        } else {

            // Do business validation and processing.
            List<String> errors = CustomerService.validateDeleteCustomers(checkedIds);
            if (errors.isEmpty()) {
                try {
                    CustomerService.deleteCustomers(checkedIds);
                    FacesContextUtil.addInfoMessage("Customers deleted successfully.");
                } catch (CrudException e) {
                    FacesContextUtil.addErrorMessage(e.getMessage());
                }
            } else {
                for (String error : errors) {
                    FacesContextUtil.addErrorMessage(error);
                }
            }
        }

        // If errors, redisplay this page.
        if (FacesContextUtil.hasErrors()) return null;

        // Otherwise, reload the list page.
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "CustomerList";
    }

    public String add() {
        log.trace("add executing"); // DEBUG
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.ADD);
        FacesContextUtil.putInFlash("originalId", null);
        return "CustomerEdit";
    }

    public String copy() {
        log.trace("copy executing"); // DEBUG
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
        return "CustomerEdit";
    }

    public String edit() {
        log.trace("edit executing"); // DEBUG
        Long originalId = resultList.getRowData().getCustomer().getId();
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.UPDATE);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "CustomerEdit";
    }

    private CustomerFilterResponse getFilterResponse() {
        if (filterResponse == null) {
            log.trace("filterRequest=\"" + filterRequest + "\""); // DEBUG
            try {
                filterResponse = CustomerService.filterCustomers(filterRequest);
                log.trace("filterResponse=\"" + filterResponse + "\""); // DEBUG
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        }
        return filterResponse;
    }

    private Collection<Long> getCheckedIds() {
        Collection<Long> checkedIds = new HashSet<Long>();
        if (resultList != null) {
            for (CustomerRow row : resultList) {
                if (row.isChecked()) {
                    checkedIds.add(row.getCustomer().getId());
                }
            }
        }
        return checkedIds;
    }
}