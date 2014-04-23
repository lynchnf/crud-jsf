package com.psclistens.example.domain;

import java.math.BigDecimal;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author LYNCHNF
 */
@StaticMetamodel(Customer.class)
public abstract class Customer_ {
    public static volatile SingularAttribute<Customer, Long> id;
    public static volatile SingularAttribute<Customer, Integer> version;
    public static volatile SingularAttribute<Customer, String> name;
    public static volatile SingularAttribute<Customer, BigDecimal> discount;
}