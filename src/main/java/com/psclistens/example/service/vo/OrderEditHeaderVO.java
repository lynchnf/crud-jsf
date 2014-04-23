package com.psclistens.example.service.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.psclistens.example.domain.OrderHeader;
import com.psclistens.example.domain.OrderStatus;

/**
 * This value object is used to hold order information for the order edit page. It allows us to pass values from the
 * OrderHeader and related entities out of the service layer without throwing a LazyInitializationException. It is also
 * used to pass in OrderHeader information that we want to insert or update.
 * 
 * @author LYNCHNF
 * @see OrderHeader
 */
public class OrderEditHeaderVO {
    private Long id;
    private Integer version;
    private Long customerId;
    private String customerName;
    private BigDecimal customerDiscount;
    private Date entryDate;
    private OrderStatus orderStatus;
    private List<OrderEditLineVO> lines = new ArrayList<OrderEditLineVO>();
    private BigDecimal orderSubTotal = BigDecimal.ZERO;
    private BigDecimal orderDiscount = BigDecimal.ZERO;
    private BigDecimal orderFinalTotal = BigDecimal.ZERO;
    private String newLineSku;
    private Integer newLineQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public BigDecimal getCustomerDiscount() {
        return customerDiscount;
    }

    public void setCustomerDiscount(BigDecimal customerDiscount) {
        this.customerDiscount = customerDiscount;
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

    public List<OrderEditLineVO> getLines() {
        return lines;
    }

    public void setLines(List<OrderEditLineVO> lines) {
        this.lines = lines;
    }

    public BigDecimal getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(BigDecimal orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public BigDecimal getOrderDiscount() {
        return orderDiscount;
    }

    public void setOrderDiscount(BigDecimal orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    public BigDecimal getOrderFinalTotal() {
        return orderFinalTotal;
    }

    public void setOrderFinalTotal(BigDecimal orderFinalTotal) {
        this.orderFinalTotal = orderFinalTotal;
    }

    public String getNewLineSku() {
        return newLineSku;
    }

    public void setNewLineSku(String newLineSku) {
        this.newLineSku = newLineSku;
    }

    public Integer getNewLineQuantity() {
        return newLineQuantity;
    }

    public void setNewLineQuantity(Integer newLineQuantity) {
        this.newLineQuantity = newLineQuantity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderEditHeaderVO [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (version != null) {
            builder.append("version=");
            builder.append(version);
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
        if (customerDiscount != null) {
            builder.append("customerDiscount=");
            builder.append(customerDiscount);
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
            builder.append(", ");
        }
        if (orderSubTotal != null) {
            builder.append("orderSubTotal=");
            builder.append(orderSubTotal);
            builder.append(", ");
        }
        if (orderDiscount != null) {
            builder.append("orderDiscount=");
            builder.append(orderDiscount);
            builder.append(", ");
        }
        if (orderFinalTotal != null) {
            builder.append("orderFinalTotal=");
            builder.append(orderFinalTotal);
            builder.append(", ");
        }
        if (newLineSku != null) {
            builder.append("newLineSku=");
            builder.append(newLineSku);
            builder.append(", ");
        }
        if (newLineQuantity != null) {
            builder.append("newLineQuantity=");
            builder.append(newLineQuantity);
            builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }
}