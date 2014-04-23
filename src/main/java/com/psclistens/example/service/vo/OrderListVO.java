package com.psclistens.example.service.vo;

import java.util.Date;

import com.psclistens.example.domain.OrderHeader;
import com.psclistens.example.domain.OrderStatus;

/**
 * This value object is used to hold order information for the order list page. It allows us to pass values from the
 * OrderHeader and related entities out of the service layer without throwing a LazyInitializationException.
 * 
 * @author LYNCHNF
 * @see OrderHeader
 */
public class OrderListVO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Date entryDate;
    private OrderStatus orderStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderListVO [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (customerId != null) {
            builder.append("customerId=");
            builder.append(customerId);
            builder.append(", ");
        }
        if (customerName != null) {
            builder.append("customerName=");
            builder.append(customerName);
            builder.append(", ");
        }
        if (entryDate != null) {
            builder.append("entryDate=");
            builder.append(entryDate);
            builder.append(", ");
        }
        if (orderStatus != null) {
            builder.append("orderStatus=");
            builder.append(orderStatus);
        }
        builder.append("]");
        return builder.toString();
    }
}