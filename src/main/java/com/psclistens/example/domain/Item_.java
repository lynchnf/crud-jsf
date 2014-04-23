package com.psclistens.example.domain;

import java.math.BigDecimal;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author LYNCHNF
 */
@StaticMetamodel(Item.class)
public abstract class Item_ {
    public static volatile SingularAttribute<Item, Long> id;
    public static volatile SingularAttribute<Item, Integer> version;
    public static volatile SingularAttribute<Item, String> sku;
    public static volatile SingularAttribute<Item, String> description;
    public static volatile SingularAttribute<Item, BigDecimal> unitPrice;
}