package com.psclistens.example.jsf.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.crud.CrudMode;
import com.psclistens.example.domain.Item;
import com.psclistens.example.jsf.FacesContextUtil;
import com.psclistens.example.service.ItemService;
import com.psclistens.example.service.filter.request.ItemFilterRequest;

/**
 * This is the backing bean for the item edit view.
 * 
 * @author LYNCHNF
 */
@ManagedBean
@ViewScoped
public class ItemEditBacking implements Serializable {
    private static final long serialVersionUID = 1L;
    private ItemFilterRequest filterRequest = new ItemFilterRequest();
    private CrudMode mode;
    private Long originalId;
    private Integer version;
    private String sku;
    private String description;
    private BigDecimal unitPrice;

    public ItemEditBacking() {
        filterRequest = (ItemFilterRequest) FacesContextUtil.getFromFlash("filterRequest");
        mode = (CrudMode) FacesContextUtil.getFromFlash("mode");
        originalId = (Long) FacesContextUtil.getFromFlash("originalId");

        version = null;
        sku = null;
        description = null;
        unitPrice = null;
        if (mode != CrudMode.ADD) {
            try {
                Item item = ItemService.readItem(Long.valueOf(originalId));
                if (item == null) {
                    FacesContextUtil.addErrorMessage("Item with id=" + originalId + " not found.");
                } else {
                    if (mode == CrudMode.UPDATE) version = item.getVersion();
                    sku = item.getSku();
                    description = item.getDescription();
                    unitPrice = item.getUnitPrice();
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String save() {
        Item item = new Item();
        if (mode == CrudMode.UPDATE) item.setId(originalId);
        item.setVersion(version);
        item.setSku(sku);
        item.setDescription(description);
        item.setUnitPrice(unitPrice);

        // Business validation.
        List<String> errors = ItemService.validateSaveItem(item);

        // Business processing.
        if (errors.isEmpty()) {
            try {
                Item item2 = ItemService.saveItem(item);
                FacesContextUtil.addInfoMessage("Item with id=" + item2.getId() + " saved successfully.");
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        } else {
            for (String error : errors) {
                FacesContextUtil.addErrorMessage(error);
            }
        }

        // If errors, redisplay this page.
        if (FacesContextUtil.hasErrors()) return null;

        // Otherwise, go back to list page.
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "ItemList";
    }

    public String reload() {
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", mode);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "ItemEdit";
    }

    public String cancel() {
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "ItemList";
    }
}