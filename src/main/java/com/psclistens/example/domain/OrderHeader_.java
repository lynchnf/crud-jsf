package com.psclistens.example.domain;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author LYNCHNF
 */
@StaticMetamodel(OrderHeader.class)
public abstract class OrderHeader_ {
    public static volatile SingularAttribute<OrderHeader, Long> id;
    public static volatile SingularAttribute<OrderHeader, Integer> version;
    public static volatile SingularAttribute<OrderHeader, Customer> customer;
    public static volatile SingularAttribute<OrderHeader, Date> entryDate;
    public static volatile SingularAttribute<OrderHeader, OrderStatus> orderStatus;
    public static volatile ListAttribute<OrderHeader, OrderLine> orderLines;
}