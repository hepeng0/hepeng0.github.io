package com.hepeng.example.mapper;

import com.hepeng.example.domain.po.DemoPO;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;
import tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper;

/**
 * description DemoMapper
 * @author hepeng [he.peng@unisinsight.com]
 * @date 2020/5/7 18:34
 * @since 1.0
 */
public interface DemoMapper extends Mapper<DemoPO>, InsertUseGeneratedKeysMapper<DemoPO>, InsertListMapper<DemoPO> {
}
