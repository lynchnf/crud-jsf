package com.psclistens.example.jsf.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.crud.CrudMode;
import com.psclistens.example.domain.Item;
import com.psclistens.example.jsf.FacesContextUtil;
import com.psclistens.example.service.ItemService;
import com.psclistens.example.service.filter.request.ItemFilterRequest;
import com.psclistens.example.service.filter.request.ItemOrderByField;
import com.psclistens.example.service.filter.request.OrderByDirection;
import com.psclistens.example.service.filter.response.ItemFilterResponse;

/**
 * This is the backing bean for the item list view.
 * 
 * @author LYNCHNF
 */
@ManagedBean
@ViewScoped
public class ItemListBacking implements Serializable {
    private static final long serialVersionUID = 1L;
    private ItemFilterRequest filterRequest;
    private ItemFilterResponse filterResponse;
    private String whereSku;
    private String whereDescription;
    private BigDecimal whereUnitPrice;
    private int pageSize;
    private DataModel<ItemRow> resultList;
    private boolean rebuildResultList = true;

    public ItemListBacking() {
        filterRequest = (ItemFilterRequest) FacesContextUtil.getFromFlash("filterRequest");
        if (filterRequest == null) filterRequest = new ItemFilterRequest();
    }

    public String getWhereSku() {
        return filterRequest.getWhereSku();
    }

    public void setWhereSku(String whereSku) {
        this.whereSku = whereSku;
    }

    public String getWhereDescription() {
        return filterRequest.getWhereDescription();
    }

    public void setWhereDescription(String whereDescription) {
        this.whereDescription = whereDescription;
    }

    public BigDecimal getWhereUnitPrice() {
        return filterRequest.getWhereUnitPrice();
    }

    public void setWhereUnitPrice(BigDecimal whereUnitPrice) {
        this.whereUnitPrice = whereUnitPrice;
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
        return ItemFilterRequest.PAGE_SIZE_OPTIONS;
    }

    public DataModel<ItemRow> getResultList() {
        if (rebuildResultList) {
            List<Item> items = getFilterResponse().getResultList();
            Collection<Long> checkedIds = getCheckedIds();
            List<ItemRow> rows = new ArrayList<ItemRow>();
            for (Item item : items) {
                boolean checked = checkedIds.contains(item.getId());
                ItemRow row = new ItemRow(item);
                row.setChecked(checked);
                rows.add(row);
            }
            resultList = new ListDataModel<ItemRow>(rows);
            rebuildResultList = false;
        }
        return resultList;
    }

    public String reload() {
        return "ItemList";
    }

    public String changeWhere() {
        filterRequest.setWhereSku(whereSku);
        filterRequest.setWhereDescription(whereDescription);
        filterRequest.setWhereUnitPrice(whereUnitPrice);
        filterRequest.setOrderByField(ItemFilterRequest.DEFAULT_ORDER_BY_FIELD);
        filterRequest.setOrderByDirection(ItemFilterRequest.DEFAULT_ORDER_BY_DIRECTION);
        filterRequest.setFirst(0);
        filterResponse = null;
        rebuildResultList = true;
        return null;
    }

    public String changeOrderBy() {
        String orderByFieldStr = FacesContextUtil.getRequestParameter("orderByField");
        ItemOrderByField orderByField = ItemOrderByField.valueOf(orderByFieldStr);
        OrderByDirection orderByDirection = ItemFilterRequest.DEFAULT_ORDER_BY_DIRECTION;
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

    public String delete() {
        Collection<Long> checkedIds = getCheckedIds();

        // If the delete button was pressed, we should have at least one checked row.
        if (checkedIds.isEmpty()) {
            FacesContextUtil.addErrorMessage("Please check at least one row before pressing the Delete button.");
        } else {

            // Do business validation and processing.
            List<String> errors = ItemService.validateDeleteItems(checkedIds);
            if (errors.isEmpty()) {
                try {
                    ItemService.deleteItems(checkedIds);
                    FacesContextUtil.addInfoMessage("Items deleted successfully.");
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
        return "ItemList";
    }

    public String add() {
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.ADD);
        FacesContextUtil.putInFlash("originalId", null);
        return "ItemEdit";
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
        return "ItemEdit";
    }

    public String edit() {
        Long originalId = resultList.getRowData().getItem().getId();
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", CrudMode.UPDATE);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "ItemEdit";
    }

    private ItemFilterResponse getFilterResponse() {
        if (filterResponse == null) {
            try {
                filterResponse = ItemService.filterItems(filterRequest);
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        }
        return filterResponse;
    }

    private Collection<Long> getCheckedIds() {
        Collection<Long> checkedIds = new HashSet<Long>();
        if (resultList != null) {
            for (ItemRow row : resultList) {
                if (row.isChecked()) {
                    checkedIds.add(row.getItem().getId());
                }
            }
        }
        return checkedIds;
    }
}