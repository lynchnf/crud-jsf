package com.psclistens.example.service.filter.response;

import java.util.ArrayList;
import java.util.List;

import com.psclistens.example.domain.Customer;
import com.psclistens.example.service.CustomerService;
import com.psclistens.example.service.filter.request.CustomerFilterRequest;

/**
 * Used to contain the results from a filtered query of the customers in the database.
 * 
 * @author LYNCHNF
 * @see CustomerService#filterCustomers(CustomerFilterRequest) CustomerService.filterCustomers()
 */
public class CustomerFilterResponse extends BaseFilterResponse {
    private List<Customer> resultList = new ArrayList<Customer>();

    public List<Customer> getResultList() {
        return resultList;
    }

    public void setResultList(List<Customer> resultList) {
        this.resultList = resultList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CustomerFilterResponse [");
        if (resultList != null) {
            builder.append("resultList.size=");
            builder.append(resultList.size());
            builder.append(", ");
        } else {
            builder.append("resultList is null, ");
        }
        if (count != null) {
            builder.append("count=");
            builder.append(count);
        }
        builder.append("]");
        return builder.toString();
    }
}