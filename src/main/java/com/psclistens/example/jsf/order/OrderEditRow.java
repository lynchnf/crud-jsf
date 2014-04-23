package com.psclistens.example.jsf.order;

import java.io.Serializable;

import com.psclistens.example.service.vo.OrderEditLineVO;

public class OrderEditRow implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean newLine;
    private OrderEditLineVO orderEditLineVO;

    public OrderEditRow(OrderEditLineVO orderEditLineVO) {
        this.orderEditLineVO = orderEditLineVO;
    }

    public boolean isNewLine() {
        return newLine;
    }

    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    public OrderEditLineVO getOrderEditLineVO() {
        return orderEditLineVO;
    }

    public void setOrderEditLineVO(OrderEditLineVO orderEditLineVO) {
        this.orderEditLineVO = orderEditLineVO;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderEditRow [newLine=");
        builder.append(newLine);
        builder.append(", ");
        if (orderEditLineVO != null) {
            builder.append("orderEditLineVO=");
            builder.append(orderEditLineVO);
        }
        builder.append("]");
        return builder.toString();
    }
}