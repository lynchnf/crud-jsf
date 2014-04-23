package com.psclistens.example.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author LYNCHNF
 */
@StaticMetamodel(OrderLine.class)
public abstract class OrderLine_ {
    public static volatile SingularAttribute<OrderLine, Long> id;
    public static volatile SingularAttribute<OrderLine, Integer> version;
    public static volatile SingularAttribute<OrderLine, OrderHeader> orderHeader;
    public static volatile SingularAttribute<OrderLine, Item> item;
    public static volatile SingularAttribute<OrderLine, Integer> quantity;
}