package com.psclistens.example.jsf.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.crud.CrudMode;
import com.psclistens.example.domain.Customer;
import com.psclistens.example.jsf.FacesContextUtil;
import com.psclistens.example.service.CustomerService;
import com.psclistens.example.service.filter.request.CustomerFilterRequest;

/**
 * This is the backing bean for the customer edit view.
 * 
 * @author LYNCHNF
 */
@ManagedBean
@ViewScoped
public class CustomerEditBacking implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(CustomerEditBacking.class);
    private CustomerFilterRequest filterRequest = new CustomerFilterRequest();
    private CrudMode mode;
    private Long originalId;
    private Integer version;
    private String name;
    private BigDecimal discount;

    public CustomerEditBacking() {
        log.trace("constructed"); // DEBUG
        filterRequest = (CustomerFilterRequest) FacesContextUtil.getFromFlash("filterRequest");
        mode = (CrudMode) FacesContextUtil.getFromFlash("mode");
        originalId = (Long) FacesContextUtil.getFromFlash("originalId");

        version = null;
        name = null;
        discount = null;
        if (mode != CrudMode.ADD) {
            try {
                Customer customer = CustomerService.readCustomer(Long.valueOf(originalId));
                if (customer == null) {
                    FacesContextUtil.addErrorMessage("Customer with id=" + originalId + " not found.");
                } else {
                    if (mode == CrudMode.UPDATE) version = customer.getVersion();
                    name = customer.getName();
                    discount = customer.getDiscount();
                }
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        }
    }

    public CrudMode getMode() {
        log.trace("getMode returns \"" + mode + "\""); // DEBUG
        return mode;
    }

    public Long getOriginalId() {
        log.trace("getOriginalId returns \"" + originalId + "\""); // DEBUG
        return originalId;
    }

    public String getName() {
        log.trace("getName returns \"" + name + "\""); // DEBUG
        return name;
    }

    public void setName(String name) {
        log.trace("setName(" + name + ")"); // DEBUG
        this.name = name;
    }

    public BigDecimal getDiscount() {
        log.trace("getDiscount returns \"" + discount + "\""); // DEBUG
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        log.trace("setDiscount(" + discount + ")"); // DEBUG
        this.discount = discount;
    }

    public String save() {
        log.trace("save executing"); // DEBUG
        Customer customer = new Customer();
        if (mode == CrudMode.UPDATE) customer.setId(originalId);
        customer.setVersion(version);
        customer.setName(name);
        customer.setDiscount(discount);

        // Business validation.
        List<String> errors = CustomerService.validateSaveCustomer(customer);

        // Business processing.
        if (errors.isEmpty()) {
            try {
                Customer customer2 = CustomerService.saveCustomer(customer);
                FacesContextUtil.addInfoMessage("Customer with id=" + customer2.getId() + " saved successfully.");
            } catch (CrudException e) {
                FacesContextUtil.addErrorMessage(e.getMessage());
            }
        } else {
            for (String error : errors) {
                FacesContextUtil.addErrorMessage(error);
            }
        }

        // If errors, redisplay this page.
        if (FacesContextUtil.hasErrors()) return null;

        // Otherwise, go back to list page.
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "CustomerList";
    }

    public String reload() {
        log.trace("reload executing"); // DEBUG
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        FacesContextUtil.putInFlash("mode", mode);
        FacesContextUtil.putInFlash("originalId", originalId);
        return "CustomerEdit";
    }

    public String cancel() {
        log.trace("cancel executing"); // DEBUG
        FacesContextUtil.putInFlash("filterRequest", filterRequest);
        return "CustomerList";
    }
}