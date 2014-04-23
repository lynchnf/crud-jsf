package com.psclistens.example.service.filter.request;

import java.math.BigDecimal;

import com.psclistens.example.service.CustomerService;

/**
 * Used to specify criteria for a filtered query of the customers in the database.
 * 
 * @author LYNCHNF
 * @see CustomerService#filterCustomers(CustomerFilterRequest) CustomerService.filterCustomers()
 */
public class CustomerFilterRequest extends BaseFilterRequest {
    private static final long serialVersionUID = 1L;
    public static final CustomerOrderByField DEFAULT_ORDER_BY_FIELD = CustomerOrderByField.ID;
    private Long whereId;
    private String whereName;
    private BigDecimal whereDiscount;
    private CustomerOrderByField orderByField = DEFAULT_ORDER_BY_FIELD;

    public Long getWhereId() {
        return whereId;
    }

    public void setWhereId(Long whereId) {
        this.whereId = whereId;
    }

    public String getWhereName() {
        return whereName;
    }

    public void setWhereName(String whereName) {
        this.whereName = whereName;
    }

    public BigDecimal getWhereDiscount() {
        return whereDiscount;
    }

    public void setWhereDiscount(BigDecimal whereDiscount) {
        this.whereDiscount = whereDiscount;
    }

    public CustomerOrderByField getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(CustomerOrderByField orderByField) {
        this.orderByField = orderByField;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CustomerFilterRequest [");
        if (whereId != null) {
            builder.append("whereId=");
            builder.append(whereId);
            builder.append(", ");
        }
        if (whereName != null) {
            builder.append("whereName=");
            builder.append(whereName);
            builder.append(", ");
        }
        if (whereDiscount != null) {
            builder.append("whereDiscount=");
            builder.append(whereDiscount);
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