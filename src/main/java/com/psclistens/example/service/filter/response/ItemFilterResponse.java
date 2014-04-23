package com.psclistens.example.service.filter.response;

import java.util.ArrayList;
import java.util.List;

import com.psclistens.example.domain.Item;
import com.psclistens.example.service.ItemService;
import com.psclistens.example.service.filter.request.ItemFilterRequest;

/**
 * Used to contain the results from a filtered query of the items in the database.
 * 
 * @author LYNCHNF
 * @see ItemService#filterItems(ItemFilterRequest) ItemService.filterItems()
 */
public class ItemFilterResponse extends BaseFilterResponse {
    private List<Item> resultList = new ArrayList<Item>();

    public List<Item> getResultList() {
        return resultList;
    }

    public void setResultList(List<Item> resultList) {
        this.resultList = resultList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemFilterResponse [");
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