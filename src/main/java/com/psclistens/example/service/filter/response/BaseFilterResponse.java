package com.psclistens.example.service.filter.response;

/**
 * Abstract base class for all filter responses.
 * 
 * @author LYNCHNF
 */
public abstract class BaseFilterResponse {
    protected Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}