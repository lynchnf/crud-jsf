package com.psclistens.example.jsf.customer;

import java.io.Serializable;

import com.psclistens.example.domain.Customer;

/**
 * This class adds a boolean check box to the customer entity.
 * 
 * @author LYNCHNF
 */
public class CustomerRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean checked;
    private Customer customer;

    public CustomerRow(Customer customer) {
        this.customer = customer;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CustomerRow [checked=");
        builder.append(checked);
        builder.append(", ");
        if (customer != null) {
            builder.append("customer=");
            builder.append(customer);
        }
        builder.append("]");
        return builder.toString();
    }
}