package com.hepeng.example.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * description UserBO
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/13 10:56
 * @since 1.0
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {
    private String usercode;
    private String username;
}
