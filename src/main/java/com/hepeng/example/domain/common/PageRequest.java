package com.hepeng.example.domain.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {
    /**
     * 分页：页数
     */
    private Integer pageNo;
    /**
     * 分页：页面大小
     */
    private Integer pageSize;

    /**
     * JPA分页
     * @return <org.springframework.data.domain.PageRequest/>
     */
    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(getPageNo() - 1, getPageSize());
    }
}
