package com.psclistens.example.service.filter.request;

import java.math.BigDecimal;

import com.psclistens.example.service.ItemService;

/**
 * Used to specify criteria for a filtered query of the items in the database.
 * 
 * @author LYNCHNF
 * @see ItemService#filterItems(ItemFilterRequest) ItemService.filterItems()
 */
public class ItemFilterRequest extends BaseFilterRequest {
    private static final long serialVersionUID = 1L;
    public static final ItemOrderByField DEFAULT_ORDER_BY_FIELD = ItemOrderByField.ID;
    private String whereSku;
    private String whereDescription;
    private BigDecimal whereUnitPrice;
    private ItemOrderByField orderByField = DEFAULT_ORDER_BY_FIELD;

    public String getWhereSku() {
        return whereSku;
    }

    public void setWhereSku(String whereSku) {
        this.whereSku = whereSku;
    }

    public String getWhereDescription() {
        return whereDescription;
    }

    public void setWhereDescription(String whereDescription) {
        this.whereDescription = whereDescription;
    }

    public BigDecimal getWhereUnitPrice() {
        return whereUnitPrice;
    }

    public void setWhereUnitPrice(BigDecimal whereUnitPrice) {
        this.whereUnitPrice = whereUnitPrice;
    }

    public ItemOrderByField getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(ItemOrderByField orderByField) {
        this.orderByField = orderByField;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemFilterRequest [");
        if (whereSku != null) {
            builder.append("whereSku=");
            builder.append(whereSku);
            builder.append(", ");
        }
        if (whereDescription != null) {
            builder.append("whereDescription=");
            builder.append(whereDescription);
            builder.append(", ");
        }
        if (whereUnitPrice != null) {
            builder.append("whereUnitPrice=");
            builder.append(whereUnitPrice);
            builder.append(", ");
        }
        if (orderByField != null) {
            builder.append("orderByField=");
            builder.append(orderByField);
            builder.append(", ");
        }
        if (orderByDirection != null) {
            builder.append("orderByDirection=");
            builder.append(orderByDirection);
            builder.append(", ");
        }
        builder.append("first=");
        builder.append(first);
        builder.append(", max=");
        builder.append(max);
        builder.append("]");
        return builder.toString();
    }
}