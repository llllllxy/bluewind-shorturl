package com.bluewind.shorturl.common.util.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-10 9:17
 * @description
 **/
public class Page implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer pageNum;

    private Integer pageSize;

    private Integer total;

    private Integer pages;

    private List<Map<String, Object>> records;

    public Page() {

    }

    public Page(Integer pageNum, Integer pageSize) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }


    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<Map<String, Object>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, Object>> records) {
        this.records = records;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
        this.pages = (total + pageSize - 1) / pageSize;
    }

    @Override
    public String toString() {
        return "Page {pageNum=" + pageNum + ", pageSize=" + pageSize + ", total=" + total + ", pages=" + pages
                + ", records=" + records + "}";
    }

}
