package com.psclistens.example.service.filter.request;

import java.util.Date;

import com.psclistens.example.domain.OrderStatus;
import com.psclistens.example.service.OrderService;

/**
 * Used to specify criteria for a filtered query of the orders in the database.
 * 
 * @author LYNCHNF
 * @see OrderService#filterOrders(OrderFilterRequest) OrderService.filterOrders()
 */
public class OrderFilterRequest extends BaseFilterRequest {
    private static final long serialVersionUID = 1L;
    public static final OrderOrderByField DEFAULT_ORDER_BY_FIELD = OrderOrderByField.ID;
    private Long whereId;
    private Long whereCustomerId;
    private String whereCustomerName;
    private Date whereEntryDate;
    private OrderStatus whereOrderStatus;
    private OrderOrderByField orderByField = DEFAULT_ORDER_BY_FIELD;

    public Long getWhereId() {
        return whereId;
    }

    public void setWhereId(Long whereId) {
        this.whereId = whereId;
    }

    public Long getWhereCustomerId() {
        return whereCustomerId;
    }

    public void setWhereCustomerId(Long whereCustomerId) {
        this.whereCustomerId = whereCustomerId;
    }

    public String getWhereCustomerName() {
        return whereCustomerName;
    }

    public void setWhereCustomerName(String whereCustomerName) {
        this.whereCustomerName = whereCustomerName;
    }

    public Date getWhereEntryDate() {
        return whereEntryDate;
    }

    public void setWhereEntryDate(Date whereEntryDate) {
        this.whereEntryDate = whereEntryDate;
    }

    public OrderStatus getWhereOrderStatus() {
        return whereOrderStatus;
    }

    public void setWhereOrderStatus(OrderStatus whereOrderStatus) {
        this.whereOrderStatus = whereOrderStatus;
    }

    public OrderOrderByField getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(OrderOrderByField orderByField) {
        this.orderByField = orderByField;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderFilterRequest [");
        if (whereId != null) {
            builder.append("whereId=");
            builder.append(whereId);
            builder.append(", ");
        }
        if (whereCustomerId != null) {
            builder.append("whereCustomerId=");
            builder.append(whereCustomerId);
            builder.append(", ");
        }
        if (whereCustomerName != null) {
            builder.append("whereCustomerName=");
            builder.append(whereCustomerName);
            builder.append(", ");
        }
        if (whereEntryDate != null) {
            builder.append("whereEntryDate=");
            builder.append(whereEntryDate);
            builder.append(", ");
        }
        if (whereOrderStatus != null) {
            builder.append("whereOrderStatus=");
            builder.append(whereOrderStatus);
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