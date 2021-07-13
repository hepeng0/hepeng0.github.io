package com.hepeng.example.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * description DemoPO
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 18:35
 * @since 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_demo")
public class DemoPO {
    @Id
    private Long id;
}
