package com.psclistens.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.psclistens.example.crud.CrudException;
import com.psclistens.example.domain.Customer;
import com.psclistens.example.domain.Customer_;
import com.psclistens.example.domain.Item;
import com.psclistens.example.domain.Item_;
import com.psclistens.example.domain.OrderHeader;
import com.psclistens.example.domain.OrderHeader_;
import com.psclistens.example.domain.OrderLine;
import com.psclistens.example.domain.OrderLine_;
import com.psclistens.example.domain.OrderStatus;
import com.psclistens.example.service.filter.request.OrderByDirection;
import com.psclistens.example.service.filter.request.OrderFilterRequest;
import com.psclistens.example.service.filter.request.OrderOrderByField;
import com.psclistens.example.service.filter.response.OrderFilterResponse;
import com.psclistens.example.service.vo.OrderEditHeaderVO;
import com.psclistens.example.service.vo.OrderEditLineVO;
import com.psclistens.example.service.vo.OrderListVO;

/**
 * This class does all database operations and business validation for order header and order line processing.
 * 
 * @author LYNCHNF
 */
public class OrderService extends BaseService {
    private static Log log = LogFactory.getLog(OrderService.class);

    public static OrderHeader readOrderHeader(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<OrderHeader> function = new DaoFunction<OrderHeader>() {
            public OrderHeader doFunction(EntityManager entityManager) throws Exception {
                return entityManager.find(OrderHeader.class, id);
            }

            public String getErrorMessage() {
                return "Error reading order for id=\"" + id + "\".";
            }
        };
        OrderHeader orderHeader = doWithEntityManager(function);
        log.trace("orderHeader=\"" + orderHeader + "\"");
        return orderHeader;
    }

    public static List<OrderHeader> readOrderHeadersByCustomer(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<List<OrderHeader>> function = new DaoFunction<List<OrderHeader>>() {
            public List<OrderHeader> doFunction(EntityManager entityManager) throws Exception {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<OrderHeader> cq = cb.createQuery(OrderHeader.class);
                Root<OrderHeader> root = cq.from(OrderHeader.class);
                Join<OrderHeader, Customer> join = root.join(OrderHeader_.customer);
                Predicate predicate = cb.equal(join.get(Customer_.id), id);
                Order order = cb.asc(root.get(OrderHeader_.id));
                cq.select(root).where(predicate).orderBy(order);
                TypedQuery<OrderHeader> query = entityManager.createQuery(cq);
                return query.getResultList();
            }

            public String getErrorMessage() {
                return "Error reading orders for customer id=\"" + id + "\".";
            }
        };
        List<OrderHeader> resultList = doWithEntityManager(function);
        log.trace("result.size=\"" + resultList.size() + "\"");
        return resultList;
    }

    public static OrderLine readOrderLine(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<OrderLine> function = new DaoFunction<OrderLine>() {
            public OrderLine doFunction(EntityManager entityManager) throws Exception {
                return entityManager.find(OrderLine.class, id);
            }

            public String getErrorMessage() {
                return "Error reading order line id=\"" + id + "\".";
            }
        };
        OrderLine orderLine = doWithEntityManager(function);
        log.trace("orderLine=\"" + orderLine + "\"");
        return orderLine;
    }

    public static List<OrderLine> readOrderLinesByItem(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<List<OrderLine>> function = new DaoFunction<List<OrderLine>>() {
            public List<OrderLine> doFunction(EntityManager entityManager) throws Exception {
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<OrderLine> cq = cb.createQuery(OrderLine.class);
                Root<OrderLine> root = cq.from(OrderLine.class);
                Join<OrderLine, Item> join = root.join(OrderLine_.item);
                Predicate predicate = cb.equal(join.get(Item_.id), id);
                Order order = cb.asc(root.get(OrderLine_.id));
                cq.select(root).where(predicate).orderBy(order);
                TypedQuery<OrderLine> query = entityManager.createQuery(cq);
                return query.getResultList();
            }

            public String getErrorMessage() {
                return "Error reading order lines for item id=\"" + id + "\".";
            }
        };
        List<OrderLine> resultList = doWithEntityManager(function);
        log.trace("result.size=\"" + resultList.size() + "\"");
        return resultList;
    }

    public static OrderEditHeaderVO getOrderForEdit(Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        OrderEditHeaderVO order = getOrderForEditImpl(id);
        updateTotals(order);
        log.trace("order=\"" + order + "\"");
        return order;
    }

    public static OrderEditHeaderVO refreshOrderForEdit(final OrderEditHeaderVO inOrder, final boolean addNewLine) throws CrudException {
        log.trace("incoming order=\"" + inOrder + "\"");
        DaoFunction<OrderEditHeaderVO> function = new DaoFunction<OrderEditHeaderVO>() {
            public OrderEditHeaderVO doFunction(EntityManager entityManager) throws Exception {

                OrderEditHeaderVO outOrder = null;
                if (inOrder.getId() == null) {
                    outOrder = new OrderEditHeaderVO();
                    Customer customer = null;
                    if (inOrder.getCustomerId() != null) customer = CustomerService.readCustomer(inOrder.getCustomerId());
                    if (customer != null) {
                        outOrder.setCustomerName(customer.getName());
                        outOrder.setCustomerDiscount(customer.getDiscount());
                    }
                    outOrder.setCustomerId(inOrder.getCustomerId());
                    outOrder.setEntryDate(inOrder.getEntryDate());
                    outOrder.setOrderStatus(inOrder.getOrderStatus());
                } else {
                    outOrder = getOrderForEditImpl(inOrder.getId());
                }

                // Update old lines. (Matched by line id.)
                for (OrderEditLineVO outLine : outOrder.getLines()) {
                    OrderEditLineVO inLine = findAndRemoveLine(inOrder.getLines(), outLine.getLineId());
                    if (inLine != null) {
                        outLine.setQuantity(inLine.getQuantity());
                        outLine.setRemoved(inLine.isRemoved());
                    }
                }

                // Add new lines. (Get item by item id.)
                for (OrderEditLineVO inLine : inOrder.getLines()) {
                    OrderEditLineVO outLine = new OrderEditLineVO();
                    Item item = ItemService.readItem(inLine.getItemId());
                    outLine.setItemId(item.getId());
                    outLine.setSku(item.getSku());
                    outLine.setItemDescription(item.getDescription());
                    outLine.setUnitPrice(item.getUnitPrice());
                    outLine.setQuantity(inLine.getQuantity());
                    outLine.setRemoved(inLine.isRemoved());
                    outOrder.getLines().add(outLine);
                }

                // Add newest line. (Get item by sku.)
                if (addNewLine && inOrder.getNewLineSku() != null) {
                    Item newItem = ItemService.readItemBySku(inOrder.getNewLineSku());
                    if (newItem == null) {
                        String msg = "Unexpected Error: Item not found for sku=\"" + inOrder.getNewLineSku() + "\".";
                        log.error(msg);
                        throw new CrudException(msg);
                    } else {
                        OrderEditLineVO outLine = new OrderEditLineVO();
                        outLine.setItemId(newItem.getId());
                        outLine.setSku(newItem.getSku());
                        outLine.setItemDescription(newItem.getDescription());
                        outLine.setUnitPrice(newItem.getUnitPrice());
                        outLine.setQuantity(inOrder.getNewLineQuantity());
                        outOrder.getLines().add(outLine);
                    }
                } else {
                    outOrder.setNewLineSku(inOrder.getNewLineSku());
                    outOrder.setNewLineQuantity(inOrder.getNewLineQuantity());
                }

                return outOrder;
            }

            public String getErrorMessage() {
                return "Error refreshing order.";
            }
        };
        OrderEditHeaderVO outOrder = doWithEntityManager(function);
        updateTotals(outOrder);
        log.trace("outgoing order=\"" + outOrder + "\"");
        return outOrder;
    }

    public static OrderFilterResponse filterOrders(final OrderFilterRequest request) throws CrudException {
        log.trace("request=\"" + request + "\"");
        DaoFunction<OrderFilterResponse> function = new DaoFunction<OrderFilterResponse>() {
            public OrderFilterResponse doFunction(EntityManager entityManager) throws Exception {
                // Select ...
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<OrderHeader> cq = cb.createQuery(OrderHeader.class);
                Root<OrderHeader> orderHeader = cq.from(OrderHeader.class);
                Join<OrderHeader, Customer> customer = orderHeader.join(OrderHeader_.customer);
                cq.select(orderHeader);
                CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
                Root<OrderHeader> orderHeader2 = cq2.from(OrderHeader.class);
                orderHeader2.join(OrderHeader_.customer);
                cq2.select(cb.count(orderHeader2));

                // Where ...
                Collection<Predicate> whereCollection = new HashSet<Predicate>();
                if (request.getWhereId() != null) {
                    Predicate idEquals = cb.equal(orderHeader.get(OrderHeader_.id), request.getWhereId());
                    whereCollection.add(idEquals);
                }
                if (request.getWhereCustomerId() != null) {
                    Predicate customerIdEquals = cb.equal(customer.get(Customer_.id), request.getWhereCustomerId());
                    whereCollection.add(customerIdEquals);
                }
                if (request.getWhereCustomerName() != null) {
                    Expression<String> lowerName = cb.lower(customer.get(Customer_.name));
                    String pattern = "%" + request.getWhereCustomerName().toLowerCase() + "%";
                    Predicate nameLike = cb.like(lowerName, pattern);
                    whereCollection.add(nameLike);
                }
                if (request.getWhereEntryDate() != null) {
                    Predicate entryDateGreaterOrEqual = cb.greaterThanOrEqualTo(orderHeader.get(OrderHeader_.entryDate), request.getWhereEntryDate());
                    whereCollection.add(entryDateGreaterOrEqual);
                }
                if (request.getWhereOrderStatus() != null) {
                    Predicate orderStatusEquals = cb.equal(orderHeader.get(OrderHeader_.orderStatus), request.getWhereOrderStatus());
                    whereCollection.add(orderStatusEquals);
                }
                if (!whereCollection.isEmpty()) {
                    Predicate[] whereArray = new Predicate[whereCollection.size()];
                    whereArray = whereCollection.toArray(whereArray);
                    cq.where(whereArray);
                    cq2.where(whereArray);
                }

                // Order by ...
                List<Path<?>> orderByPathList = new ArrayList<Path<?>>();
                if (request.getOrderByField() == OrderOrderByField.CUSTOMER_ID) {
                    orderByPathList.add(customer.get(Customer_.id));
                } else if (request.getOrderByField() == OrderOrderByField.CUSTOMER_NAME) {
                    orderByPathList.add(customer.get(Customer_.name));
                } else if (request.getOrderByField() == OrderOrderByField.ENTRY_DATE) {
                    orderByPathList.add(orderHeader.get(OrderHeader_.entryDate));
                } else if (request.getOrderByField() == OrderOrderByField.ORDER_STATUS) {
                    orderByPathList.add(orderHeader.get(OrderHeader_.orderStatus));
                }
                // Always order by id.
                orderByPathList.add(orderHeader.get(OrderHeader_.id));
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

                OrderFilterResponse response = new OrderFilterResponse();

                // Get a page of records.
                TypedQuery<OrderHeader> query = entityManager.createQuery(cq);
                if (request.getFirst() > 0) query.setFirstResult(request.getFirst());
                if (request.getMax() >= 0) query.setMaxResults(request.getMax());
                mapToOrderListVOs(query.getResultList(), response.getResultList());

                // Get total record count.
                TypedQuery<Long> query2 = entityManager.createQuery(cq2);
                Long count = query2.getSingleResult();
                response.setCount(count);
                return response;
            }

            public String getErrorMessage() {
                return "Error reading orders with filter request=\"" + request + "\".";
            }
        };
        OrderFilterResponse response = doWithEntityManager(function);
        log.trace("response.result.size=\"" + response.getResultList().size() + "\", response.count=\"" + response.getCount() + "\"");
        return response;
    }

    public static List<String> validateSaveOrder(OrderEditHeaderVO order) {
        log.trace("order=\"" + order + "\"");
        List<String> errors = new ArrayList<String>();

        // If we have a customer id, it must be valid.
        if (order.getCustomerId() != null) {
            try {
                Customer customer = CustomerService.readCustomer(order.getCustomerId());
                if (customer == null) {
                    String msg = "Customer not found for id=" + order.getCustomerId() + ".";
                    errors.add(msg);
                }
            } catch (CrudException e) {
                errors.add(e.getMessage());
            }
        }

        int lineNbr = 1;
        for (Iterator<OrderEditLineVO> iter = order.getLines().iterator(); iter.hasNext();) {
            OrderEditLineVO line = iter.next();
            // If this line is being removed, don't bother validating the quality. Otherwise, ...
            if (!line.isRemoved()) {
                // Business says quantity must be more than zero.
                if (line.getQuantity() != null && line.getQuantity().intValue() <= 0) {
                    String msg = "Quantity must be more than zero for line " + lineNbr + ".";
                    errors.add(msg);
                }
            }
            lineNbr++;
        }

        // SKU for new line must be valid (if one was entered).
        if (order.getNewLineSku() != null) {
            try {
                Item item = ItemService.readItemBySku(order.getNewLineSku());
                if (item == null) {
                    String msg = "Item not found for SKU=" + order.getNewLineSku() + ".";
                    errors.add(msg);
                }
            } catch (CrudException e) {
                errors.add(e.getMessage());
            }
        }

        // Business says quantity must be more than zero.
        if (order.getNewLineQuantity() != null && order.getNewLineQuantity().intValue() <= 0) {
            String msg = "Quantity must be more than zero for new line.";
            errors.add(msg);
        }

        log.trace("errors=\"" + errors + "\"");
        return errors;
    }

    public static Long saveOrder(final OrderEditHeaderVO order) throws CrudException {
        log.trace("order=\"" + order + "\"");
        DaoFunction<Long> function = new DaoFunction<Long>() {
            public Long doFunction(EntityManager entityManager) throws Exception {
                OrderHeader orderHeader = new OrderHeader();
                orderHeader.setId(order.getId());
                orderHeader.setVersion(order.getVersion());

                // If we're creating a new order, we'll have a customer id.
                if (order.getId() == null) {
                    Customer customer = CustomerService.readCustomer(order.getCustomerId());
                    orderHeader.setCustomer(customer);
                    orderHeader.setEntryDate(order.getEntryDate());
                    orderHeader.setOrderStatus(order.getOrderStatus());
                } else {
                    // Otherwise, we need to get the customer from old order header. Entry date and status too.
                    OrderHeader oldOrderHeader = readOrderHeader(order.getId());
                    orderHeader.setCustomer(oldOrderHeader.getCustomer());
                    orderHeader.setEntryDate(oldOrderHeader.getEntryDate());
                    orderHeader.setOrderStatus(oldOrderHeader.getOrderStatus());
                }
                for (OrderEditLineVO line : order.getLines()) {
                    if (line.isRemoved()) {
                        if (line.getLineId() != null) deleteOrderLine(line.getLineId());
                    } else {
                        OrderLine orderLine = new OrderLine();
                        orderLine.setId(line.getLineId());
                        orderLine.setVersion(line.getLineVersion());
                        orderLine.setOrderHeader(orderHeader);

                        // If this is a new line, we'll have an item id.
                        if (line.getLineId() == null) {
                            Item item = ItemService.readItem(line.getItemId());
                            orderLine.setItem(item);
                        } else {
                            // Otherwise, we need to get the item from old order line.
                            OrderLine oldOrderLine = readOrderLine(line.getLineId());
                            orderLine.setItem(oldOrderLine.getItem());
                        }

                        orderLine.setQuantity(line.getQuantity());
                        orderHeader.getOrderLines().add(orderLine);
                    }
                }
                if (order.getNewLineSku() != null) {
                    OrderLine orderLine = new OrderLine();
                    orderLine.setOrderHeader(orderHeader);
                    Item item = ItemService.readItemBySku(order.getNewLineSku());
                    orderLine.setItem(item);
                    orderLine.setQuantity(order.getNewLineQuantity());
                    orderHeader.getOrderLines().add(orderLine);
                }
                OrderHeader orderHeader2 = entityManager.merge(orderHeader);
                return orderHeader2.getId();
            }

            public String getErrorMessage() {
                return "Error saving order";
            }
        };
        Long savedId = doInTransaction(function);
        log.trace("savedId=\"" + savedId + "\"");
        return savedId;
    }

    public static Long cancelOrder(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<Long> function = new DaoFunction<Long>() {
            public Long doFunction(EntityManager entityManager) throws Exception {
                OrderHeader orderHeader = readOrderHeader(id);
                orderHeader.setOrderStatus(OrderStatus.CANCELED);
                OrderHeader orderHeader2 = entityManager.merge(orderHeader);
                return orderHeader2.getId();
            }

            public String getErrorMessage() {
                return "Error saving order id=" + id + ".";
            }
        };
        Long savedId = doInTransaction(function);
        log.trace("savedId=\"" + savedId + "\"");
        return savedId;
    }

    private static void deleteOrderLine(final Long id) throws CrudException {
        log.trace("id=\"" + id + "\"");
        DaoFunction<Void> function = new DaoFunction<Void>() {
            public Void doFunction(EntityManager entityManager) throws Exception {
                OrderLine orderLine = readOrderLine(id);
                if (orderLine != null) entityManager.remove(orderLine);
                return null;
            }

            public String getErrorMessage() {
                return "Error deleting order line id=\"" + id + "\".";
            }
        };
        doInTransaction(function);
        log.trace("delete completed successfully");
    }

    private static OrderEditHeaderVO getOrderForEditImpl(final Long id) throws CrudException {
        DaoFunction<OrderEditHeaderVO> function = new DaoFunction<OrderEditHeaderVO>() {
            public OrderEditHeaderVO doFunction(EntityManager entityManager) throws Exception {
                OrderHeader orderHeader = entityManager.find(OrderHeader.class, id);
                if (orderHeader == null) return null;
                OrderEditHeaderVO order = new OrderEditHeaderVO();
                order.setId(orderHeader.getId());
                order.setVersion(orderHeader.getVersion());
                order.setCustomerId(orderHeader.getCustomer().getId());
                order.setCustomerName(orderHeader.getCustomer().getName());
                order.setCustomerDiscount(orderHeader.getCustomer().getDiscount());
                order.setEntryDate(orderHeader.getEntryDate());
                order.setOrderStatus(orderHeader.getOrderStatus());
                for (OrderLine orderLine : orderHeader.getOrderLines()) {
                    OrderEditLineVO line = new OrderEditLineVO();
                    line.setLineId(orderLine.getId());
                    line.setLineVersion(orderLine.getVersion());
                    line.setItemId(orderLine.getItem().getId());
                    line.setSku(orderLine.getItem().getSku());
                    line.setItemDescription(orderLine.getItem().getDescription());
                    line.setUnitPrice(orderLine.getItem().getUnitPrice());
                    line.setQuantity(orderLine.getQuantity());
                    order.getLines().add(line);
                }
                return order;
            }

            public String getErrorMessage() {
                return "Error reading order id=\"" + id + "\".";
            }
        };
        return doWithEntityManager(function);
    }

    private static void updateTotals(OrderEditHeaderVO order) {
        order.setOrderSubTotal(BigDecimal.ZERO);
        List<OrderEditLineVO> lines = order.getLines();
        for (OrderEditLineVO line : lines) {
            if (line.getUnitPrice() != null && line.getQuantity() != null && !line.isRemoved()) {
                BigDecimal linePrice = line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity().longValue()));
                line.setLinePrice(linePrice);
                order.setOrderSubTotal(order.getOrderSubTotal().add(linePrice));
            }
        }
        if (order.getCustomerDiscount() == null) {
            order.setOrderDiscount(BigDecimal.ZERO);
            order.setOrderFinalTotal(order.getOrderSubTotal());
        } else {
            BigDecimal orderDiscount = order.getOrderSubTotal().multiply(order.getCustomerDiscount()).setScale(2, RoundingMode.HALF_UP);
            order.setOrderDiscount(orderDiscount);
            order.setOrderFinalTotal(order.getOrderSubTotal().subtract(orderDiscount));
        }
    }

    private static OrderEditLineVO findAndRemoveLine(List<OrderEditLineVO> lines, Long lineId) {
        for (Iterator<OrderEditLineVO> iter = lines.iterator(); iter.hasNext();) {
            OrderEditLineVO line = iter.next();
            if (lineId.equals(line.getLineId())) {
                iter.remove();
                return line;
            }
        }
        return null;
    }

    private static void mapToOrderListVOs(List<OrderHeader> orderHeaderList, List<OrderListVO> orderVOList) {
        for (OrderHeader orderHeader : orderHeaderList) {
            OrderListVO orderListVO = new OrderListVO();
            mapToOrderListVO(orderHeader, orderListVO);
            orderVOList.add(orderListVO);
        }
    }

    private static void mapToOrderListVO(OrderHeader orderHeader, OrderListVO orderListVO) {
        orderListVO.setId(orderHeader.getId());
        orderListVO.setCustomerId(orderHeader.getCustomer().getId());
        orderListVO.setCustomerName(orderHeader.getCustomer().getName());
        orderListVO.setEntryDate(orderHeader.getEntryDate());
        orderListVO.setOrderStatus(orderHeader.getOrderStatus());
    }
}