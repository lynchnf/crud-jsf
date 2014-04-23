package com.psclistens.example.jsf.order;

import java.io.Serializable;

import com.psclistens.example.service.vo.OrderListVO;

/**
 * This class adds a boolean check box to the order list value object.
 * 
 * @author LYNCHNF
 */
public class OrderListRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean checked;
    private OrderListVO orderListVO;

    public OrderListRow(OrderListVO orderListVO) {
        this.orderListVO = orderListVO;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public OrderListVO getOrderListVO() {
        return orderListVO;
    }

    public void setOrderListVO(OrderListVO orderListVO) {
        this.orderListVO = orderListVO;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderListRow [checked=");
        builder.append(checked);
        builder.append(", ");
        if (orderListVO != null) {
            builder.append("orderListVO=");
            builder.append(orderListVO);
        }
        builder.append("]");
        return builder.toString();
    }
}