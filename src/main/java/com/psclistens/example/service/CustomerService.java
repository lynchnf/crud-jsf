package com.psclistens.example.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.domain.Customer;
import com.psclistens.example.domain.Customer_;
import com.psclistens.example.domain.OrderHeader;
import com.psclistens.example.service.filter.request.CustomerFilterRequest;
import com.psclistens.example.service.filter.request.CustomerOrderByField;
import com.psclistens.example.service.filter.request.OrderByDirection;
import com.psclistens.example.service.filter.response.CustomerFilterResponse;

/**
 * This class does all database operations and business validation for customer processing.
 * 
 * @author LYNCHNF
 */
public class CustomerService extends BaseService {
    private static Log log = LogFactory.getLog(CustomerService.class);

    public static Customer readCustomer(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<Customer> function = new DaoFunction<Customer>() {
            public Customer doFunction(EntityManager entityManager) throws Exception {
                return entityManager.find(Customer.class, id);
            }

            public String getErrorMessage() {
                return "Error reading customer id=\"" + id + "\".";
            }
        };
        Customer customer = doWithEntityManager(function);
        log.trace("customer=\"" + customer + "\"");
        return customer;
    }

    public static CustomerFilterResponse filterCustomers(final CustomerFilterRequest request) throws CrudException {
        log.trace("request=\"" + request + "\"");
        DaoFunction<CustomerFilterResponse> function = new DaoFunction<CustomerFilterResponse>() {
            public CustomerFilterResponse doFunction(EntityManager entityManager) throws Exception {
                // Select ...
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
                Root<Customer> customer = cq.from(Customer.class);
                cq.select(customer);
                CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
                Root<Customer> customer2 = cq2.from(Customer.class);
                cq2.select(cb.count(customer2));

                // Where ...
                Collection<Predicate> whereCollection = new HashSet<Predicate>();
                if (request.getWhereId() != null) {
                    Predicate idEquals = cb.equal(customer.get(Customer_.id), request.getWhereId());
                    whereCollection.add(idEquals);
                }
                if (request.getWhereName() != null) {
                    Expression<String> lowerName = cb.lower(customer.get(Customer_.name));
                    String pattern = "%" + request.getWhereName().toLowerCase() + "%";
                    Predicate nameLike = cb.like(lowerName, pattern);
                    whereCollection.add(nameLike);
                }
                if (request.getWhereDiscount() != null) {
                    Predicate discountGreaterOrEqual = cb.ge(customer.get(Customer_.discount), request.getWhereDiscount());
                    whereCollection.add(discountGreaterOrEqual);
                }
                if (!whereCollection.isEmpty()) {
                    Predicate[] whereArray = new Predicate[whereCollection.size()];
                    whereArray = whereCollection.toArray(whereArray);
                    cq.where(whereArray);
                    cq2.where(whereArray);
                }

                // Order by ...
                List<Path<?>> orderByPathList = new ArrayList<Path<?>>();
                if (request.getOrderByField() == CustomerOrderByField.NAME) {
                    orderByPathList.add(customer.get(Customer_.name));
                } else if (request.getOrderByField() == CustomerOrderByField.DISCOUNT) {
                    orderByPathList.add(customer.get(Customer_.discount));
                }
                // Always order by id.
                orderByPathList.add(customer.get(Customer_.id));
                // Ascending or Descending?
                List<Order> orderByList = new ArrayList<Order>();
                for (Path<?> orderByPath : orderByPathList) {
                    Order orderBy = null;
                    if (request.getOrderByDirection() == OrderByDirection.DESC) {
                        orderBy = cb.desc(orderByPath);
                    } else {
                        orderBy = cb.asc(orderByPath);
                    }
                    orderByList.add(orderBy);
                }
                cq.orderBy(orderByList);

                CustomerFilterResponse response = new CustomerFilterResponse();

                // Get a page of records.
                TypedQuery<Customer> query = entityManager.createQuery(cq);
                if (request.getFirst() > 0) query.setFirstResult(request.getFirst());
                if (request.getMax() >= 0) query.setMaxResults(request.getMax());
                response.setResultList(query.getResultList());

                // Get total record count.
                TypedQuery<Long> query2 = entityManager.createQuery(cq2);
                Long count = query2.getSingleResult();
                response.setCount(count);
                return response;
            }

            public String getErrorMessage() {
                return "Error reading customers with filter request=\"" + request + "\".";
            }
        };
        CustomerFilterResponse response = doWithEntityManager(function);
        log.trace("response.result.size=\"" + response.getResultList().size() + "\", response.count=\"" + response.getCount() + "\"");
        return response;
    }

    public static List<String> validateSaveCustomer(Customer customer) {
        log.trace("customer=\"" + customer + "\"");
        List<String> errors = new ArrayList<String>();

        // Business says discount must be zero or greater and no more than 50%.
        if (customer.getDiscount() != null) {
            BigDecimal lo = BigDecimal.ZERO;
            BigDecimal hi = BigDecimal.valueOf(5, 1);
            if (customer.getDiscount().compareTo(lo) < 0 || customer.getDiscount().compareTo(hi) > 0) {
                String msg = "If this customer has a discount, it must be between 0 and 50 percent.";
                errors.add(msg);
            }
        }
        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static Customer saveCustomer(final Customer customer) throws CrudException {
        log.trace("customer=\"" + customer + "\"");
        DaoFunction<Customer> function = new DaoFunction<Customer>() {
            public Customer doFunction(EntityManager entityManager) throws Exception {
                return entityManager.merge(customer);
            }

            public String getErrorMessage() {
                return "Error saving customer=\"" + customer + "\".";
            }
        };
        Customer customer2 = doInTransaction(function);
        log.trace("customer2=\"" + customer2 + "\"");
        return customer2;
    }

    public static List<String> validateDeleteCustomer(Long id) {
        log.trace("id=\"" + id + "\"");
        List<String> errors = new ArrayList<String>();

        // Verify this customer does not have any orders.
        try {
            List<OrderHeader> orderHeaders = OrderService.readOrderHeadersByCustomer(id);
            if (!orderHeaders.isEmpty()) {
                String msg = "Customer with id=\"" + id + "\" cannot be deleted. Orders exist for this customer.";
                errors.add(msg);
            }
        } catch (CrudException e) {
            errors.add(e.getMessage());
        }
        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static void deleteCustomer(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<Void> function = new DaoFunction<Void>() {
            public Void doFunction(EntityManager entityManager) throws Exception {
                Customer customer = readCustomer(id);
                if (customer != null) entityManager.remove(customer);
                return null;
            }

            public String getErrorMessage() {
                return "Error deleting customer id=\"" + id + "\".";
            }
        };
        doInTransaction(function);
        log.trace("delete completed successfully");
    }

    public static List<String> validateDeleteCustomers(final Collection<Long> ids) {
        log.trace("ids=\"" + ids + "\"");
        List<String> errors = new ArrayList<String>();
        for (Long id : ids) {
            errors.addAll(validateDeleteCustomer(id));
        }
        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static void deleteCustomers(final Collection<Long> ids) throws CrudException {
        log.trace("ids=\"" + ids + "\"");
        DaoFunction<Void> function = new DaoFunction<Void>() {
            public Void doFunction(EntityManager entityManager) throws Exception {
                for (Long id : ids) {
                    deleteCustomer(id);
                }
                return null;
            }

            public String getErrorMessage() {
                return "Error deleting one or more customers with ids=\"" + ids + "\".";
            }
        };
        doInTransaction(function);
        log.trace("all deletes completed successfully");
    }
}