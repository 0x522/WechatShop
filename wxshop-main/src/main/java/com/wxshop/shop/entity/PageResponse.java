package com.wxshop.shop.entity;

import java.util.List;

public class PageResponse<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPage;
    private List<T> data;

    public PageResponse() {
    }

    private PageResponse(Integer pageNum, Integer pageSize, Integer totalPage, List<T> data) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.data = data;
    }


    public static <T> PageResponse<T> pagedData(int pageNum, int pageSize, int totalPage, List<T> data) {
        PageResponse<T> result = new PageResponse<>();
        result.setData(data);
        result.setPageNum(pageNum);
        result.setTotalPage(totalPage);
        result.setPageSize(pageSize);
        return result;
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

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
