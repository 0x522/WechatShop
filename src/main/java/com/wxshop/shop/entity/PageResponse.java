package com.wxshop.shop.entity;

import java.util.List;

public class PageResponse<T> {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalPage;
    private List<T> data;

    public PageResponse() {
    }

    private PageResponse(Integer pageNumber, Integer pageSize, Integer totalPage, List<T> data) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.data = data;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public static <T> PageResponse<T> pagedData(int pageNumber, int pageSize, int totalPage, List<T> data) {
        PageResponse<T> result = new PageResponse<>();
        result.setData(data);
        result.setPageNumber(pageNumber);
        result.setTotalPage(totalPage);
        result.setPageSize(pageSize);
        return result;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
