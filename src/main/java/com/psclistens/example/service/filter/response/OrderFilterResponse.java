package com.psclistens.example.service.filter.response;

import java.util.ArrayList;
import java.util.List;

import com.psclistens.example.service.OrderService;
import com.psclistens.example.service.filter.request.OrderFilterRequest;
import com.psclistens.example.service.vo.OrderListVO;

/**
 * Used to contain the results from a filtered query of the orders in the database.
 * 
 * @author LYNCHNF
 * @see OrderService#filterOrders(OrderFilterRequest) OrderService.filterOrders()
 */
public class OrderFilterResponse extends BaseFilterResponse {
    private List<OrderListVO> resultList = new ArrayList<OrderListVO>();

    public List<OrderListVO> getResultList() {
        return resultList;
    }

    public void setResultList(List<OrderListVO> resultList) {
        this.resultList = resultList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderFilterResponse [");
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