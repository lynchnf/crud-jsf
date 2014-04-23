package com.psclistens.example.service.vo;

import java.math.BigDecimal;

import com.psclistens.example.domain.OrderLine;

/**
 * This value object is used to hold order information for the order edit page. It allows us to pass values from the
 * OrderLine and related entities out of the service layer without throwing a LazyInitializationException. It is also
 * used to pass in OrderLine information that we want to insert or update.
 * 
 * @author LYNCHNF
 * @see OrderLine
 */
public class OrderEditLineVO {
    private Long lineId;
    private Integer lineVersion;
    private Long itemId;
    private String sku;
    private String itemDescription;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal linePrice;
    private boolean removed = false;

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Integer getLineVersion() {
        return lineVersion;
    }

    public void setLineVersion(Integer lineVersion) {
        this.lineVersion = lineVersion;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLinePrice() {
        return linePrice;
    }

    public void setLinePrice(BigDecimal linePrice) {
        this.linePrice = linePrice;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderEditLineVO [");
        if (lineId != null) {
            builder.append("lineId=");
            builder.append(lineId);
            builder.append(", ");
        }
        if (lineVersion != null) {
            builder.append("lineVersion=");
            builder.append(lineVersion);
            builder.append(", ");
        }
        if (itemId != null) {
            builder.append("itemId=");
            builder.append(itemId);
            builder.append(", ");
        }
        if (sku != null) {
            builder.append("sku=");
            builder.append(sku);
            builder.append(", ");
        }
        if (itemDescription != null) {
            builder.append("itemDescription=");
            builder.append(itemDescription);
            builder.append(", ");
        }
        if (unitPrice != null) {
            builder.append("unitPrice=");
            builder.append(unitPrice);
            builder.append(", ");
        }
        if (quantity != null) {
            builder.append("quantity=");
            builder.append(quantity);
            builder.append(", ");
        }
        if (linePrice != null) {
            builder.append("linePrice=");
            builder.append(linePrice);
            builder.append(", ");
        }
        builder.append("removed=");
        builder.append(removed);
        builder.append(", ");
        builder.append("]");
        return builder.toString();
    }
}