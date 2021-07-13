package com.hepeng.example.domain.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {
    private List<T> data;
    private Paging paging;
    /**
     * 分页响应
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Paging {
        private Integer pageNo;
        private Integer pageSize;
        private Long total;
    }
}
