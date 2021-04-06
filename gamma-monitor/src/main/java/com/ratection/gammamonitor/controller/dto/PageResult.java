package com.ratection.gammamonitor.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {


    @Getter
    private int pageNumber;

    @Getter
    private int pageSize;

    @Getter
    private long totalCount;

    @Getter
    private List<T> results;

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber <= 0 ? 1 : pageNumber;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
