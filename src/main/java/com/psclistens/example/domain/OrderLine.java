package com.psclistens.example.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Order line entity bean. A line will always be part of an order (i.e. it will always have an order header) and it MUST
 * have an item.
 * 
 * @author LYNCHNF
 */
@Entity
@Table(name = "ORDER_LINE")
public class OrderLine implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer version;
    private OrderHeader orderHeader;
    private Item item;
    private Integer quantity;

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
    @JoinColumn(name = "ORDER_ID", nullable = false)
    public OrderHeader getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(OrderHeader orderHeader) {
        this.orderHeader = orderHeader;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Column(name = "QUANTITY")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderLine [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (orderHeader != null && orderHeader.getId() != null) {
            builder.append("orderHeaderId=");
            builder.append(orderHeader.getId());
            builder.append(", ");
        }
        if (item != null && item.getId() != null) {
            builder.append("itemId=");
            builder.append(item.getId());
            builder.append(", ");
        }
        if (quantity != null) {
            builder.append("quantity=");
            builder.append(quantity);
        }
        builder.append("]");
        return builder.toString();
    }
}