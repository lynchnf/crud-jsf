package com.psclistens.example.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Customer order entity bean. An order MUST have a customer and may have multiple order lines.
 * 
 * @author LYNCHNF
 */
@Entity
@Table(name = "ORDER_HEADER")
public class OrderHeader implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer version;
    private Customer customer;
    private Date entryDate;
    private OrderStatus orderStatus;
    private List<OrderLine> orderLines = new ArrayList<OrderLine>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name = "ENTRY_DATE")
    @Temporal(TemporalType.DATE)
    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    @Column(name = "ORDER_STATUS")
    @Enumerated(EnumType.STRING)
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "orderHeader")
    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderHeader [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (customer != null && customer.getId() != null) {
            builder.append("customerId=");
            builder.append(customer.getId());
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