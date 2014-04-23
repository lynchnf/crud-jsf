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
import com.psclistens.example.domain.Item;
import com.psclistens.example.domain.Item_;
import com.psclistens.example.domain.OrderLine;
import com.psclistens.example.service.filter.request.ItemFilterRequest;
import com.psclistens.example.service.filter.request.ItemOrderByField;
import com.psclistens.example.service.filter.request.OrderByDirection;
import com.psclistens.example.service.filter.response.ItemFilterResponse;

/**
 * This class does all database operations and business validation for item processing.
 * 
 * @author LYNCHNF
 */
public class ItemService extends BaseService {
    private static Log log = LogFactory.getLog(ItemService.class);

    public static Item readItem(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<Item> function = new DaoFunction<Item>() {
            public Item doFunction(EntityManager entityManager) throws Exception {
                return entityManager.find(Item.class, id);
            }

            public String getErrorMessage() {
                return "Error reading item id=\"" + id + "\".";
            }
        };
        Item item = doWithEntityManager(function);
        log.trace("item=\"" + item + "\"");
        return item;
    }

    public static Item readItemBySku(final String sku) throws CrudException {
        log.trace("sku=\"" + sku + "\"");
        DaoFunction<Item> function = new DaoFunction<Item>() {
            public Item doFunction(EntityManager entityManager) throws Exception {
                // Select ...
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<Item> cq = cb.createQuery(Item.class);
                Root<Item> item = cq.from(Item.class);
                cq.select(item);

                // Where ...
                Predicate skuEquals = cb.equal(item.get(Item_.sku), sku);
                cq.where(skuEquals);

                // Get records. Sku is unique so there should no more than one.
                TypedQuery<Item> query = entityManager.createQuery(cq);
                List<Item> resultList = query.getResultList();
                if (resultList.isEmpty()) return null;
                return resultList.get(0);
            }

            public String getErrorMessage() {
                return "Error reading item by sku=\"" + sku + "\".";
            }
        };
        Item item = doWithEntityManager(function);
        log.trace("item=\"" + item + "\"");
        return item;
    }

    public static ItemFilterResponse filterItems(final ItemFilterRequest request) throws CrudException {
        log.trace("request=\"" + request + "\"");
        DaoFunction<ItemFilterResponse> function = new DaoFunction<ItemFilterResponse>() {
            public ItemFilterResponse doFunction(EntityManager entityManager) throws Exception {
                // Select ...
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<Item> cq = cb.createQuery(Item.class);
                Root<Item> item = cq.from(Item.class);
                cq.select(item);
                CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
                Root<Item> item2 = cq2.from(Item.class);
                cq2.select(cb.count(item2));

                // Where ...
                Collection<Predicate> whereCollection = new HashSet<Predicate>();
                if (request.getWhereSku() != null) {
                    Expression<String> lowerSku = cb.lower(item.get(Item_.sku));
                    String pattern = request.getWhereSku().toLowerCase() + "%";
                    Predicate skuBeginsWith = cb.like(lowerSku, pattern);
                    whereCollection.add(skuBeginsWith);
                }
                if (request.getWhereDescription() != null) {
                    Expression<String> lowerDescription = cb.lower(item.get(Item_.description));
                    String pattern = "%" + request.getWhereDescription().toLowerCase() + "%";
                    Predicate descriptionLike = cb.like(lowerDescription, pattern);
                    whereCollection.add(descriptionLike);
                }
                if (request.getWhereUnitPrice() != null) {
                    Predicate unitPriceLessOrEqual = cb.le(item.get(Item_.unitPrice), request.getWhereUnitPrice());
                    whereCollection.add(unitPriceLessOrEqual);
                }
                if (!whereCollection.isEmpty()) {
                    Predicate[] whereArray = new Predicate[whereCollection.size()];
                    whereArray = whereCollection.toArray(whereArray);
                    cq.where(whereArray);
                    cq2.where(whereArray);
                }

                // Order by ...
                List<Path<?>> orderByPathList = new ArrayList<Path<?>>();
                if (request.getOrderByField() == ItemOrderByField.SKU) {
                    orderByPathList.add(item.get(Item_.sku));
                } else if (request.getOrderByField() == ItemOrderByField.DESCRIPTION) {
                    orderByPathList.add(item.get(Item_.description));
                } else if (request.getOrderByField() == ItemOrderByField.UNIT_PRICE) {
                    orderByPathList.add(item.get(Item_.unitPrice));
                }
                // Always order by id.
                orderByPathList.add(item.get(Item_.id));
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

                ItemFilterResponse response = new ItemFilterResponse();

                // Get a page of records.
                TypedQuery<Item> query = entityManager.createQuery(cq);
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
                return "Error reading items with filter request=\"" + request + "\".";
            }
        };
        ItemFilterResponse response = doWithEntityManager(function);
        log.trace("response.result.size=\"" + response.getResultList().size() + "\", response.count=\"" + response.getCount() + "\"");
        return response;
    }

    public static List<String> validateSaveItem(Item item) {
        log.trace("item=\"" + item + "\"");
        List<String> errors = new ArrayList<String>();

        if (item.getSku() != null) {
            try {
                Item other = readItemBySku(item.getSku());
                if (other != null && !other.getId().equals(item.getId())) {
                    String msg = "SKU must be unique. Another item already has this SKU.";
                    errors.add(msg);
                }
            } catch (CrudException e) {
                errors.add(e.getMessage());
            }
        }

        // Business says unit prices cannot be zero or negative.
        if (item.getUnitPrice() != null && item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            String msg = "If this item has a unit price, it must be greater than zero.";
            errors.add(msg);
        }
        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static Item saveItem(final Item item) throws CrudException {
        log.trace("item=\"" + item + "\"");
        DaoFunction<Item> function = new DaoFunction<Item>() {
            public Item doFunction(EntityManager entityManager) throws Exception {
                return entityManager.merge(item);
            }

            public String getErrorMessage() {
                return "Error saving item=\"" + item + "\".";
            }
        };
        Item item2 = doInTransaction(function);
        log.trace("item2=\"" + item2 + "\"");
        return item2;
    }

    public static List<String> validateDeleteItem(Long id) {
        log.trace("id=\"" + id + "\"");
        List<String> errors = new ArrayList<String>();

        // Verify this item is not in any orders.
        try {
            List<OrderLine> orderLines = OrderService.readOrderLinesByItem(id);
            if (!orderLines.isEmpty()) {
                String msg = "Item with id=\"" + id + "\" cannot be deleted. Orders exist with this item.";
                errors.add(msg);
            }
        } catch (CrudException e) {
            errors.add(e.getMessage());
        }
        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static void deleteItem(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<Void> function = new DaoFunction<Void>() {
            public Void doFunction(EntityManager entityManager) throws Exception {
                Item item = readItem(id);
                if (item != null) entityManager.remove(item);
                return null;
            }

            public String getErrorMessage() {
                return "Error deleting item id=\"" + id + "\".";
            }

        };
        doInTransaction(function);
        log.trace("delete completed successfully");
    }

    public static List<String> validateDeleteItems(final Collection<Long> ids) {
        log.trace("ids=\"" + ids + "\"");
        List<String> errors = new ArrayList<String>();
        for (Long id : ids) {
            errors.addAll(validateDeleteItem(id));
        }
        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static void deleteItems(final Collection<Long> ids) throws CrudException {
        log.trace("ids=\"" + ids + "\"");
        DaoFunction<Void> function = new DaoFunction<Void>() {
            public Void doFunction(EntityManager entityManager) throws Exception {
                for (Long id : ids) {
                    deleteItem(id);
                }
                return null;
            }

            public String getErrorMessage() {
                return "Error deleting one or more items with ids=\"" + ids + "\".";
            }
        };
        doInTransaction(function);
        log.trace("all deletes completed successfully");
    }
}