package com.psclistens.example.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Simple item line entity bean. Property sku (stock-keeping unit) will always be unique, i.e. there will never be 2
 * different items with the same sku.
 * 
 * @author LYNCHNF
 */
@Entity
@Table(name = "ITEM")
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer version;
    private String sku;
    private String description;
    private BigDecimal unitPrice;

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

    @Column(name = "SKU", length = 20, nullable = false, unique = true)
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Column(name = "DESCRIPTION", length = 100)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "UNIT_PRICE", precision = 9, scale = 2)
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Item [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (sku != null) {
            builder.append("sku=");
            builder.append(sku);
            builder.append(", ");
        }
        if (description != null) {
            builder.append("description=");
            builder.append(description);
            builder.append(", ");
        }
        if (unitPrice != null) {
            builder.append("unitPrice=");
            builder.append(unitPrice);
        }
        builder.append("]");
        return builder.toString();
    }
}