/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package ${basePackage}.service;
import ${basePackage}.dto.request.${modelNameUpperCamel}ReqDTO;
import ${basePackage}.dto.response.${modelNameUpperCamel}ResDTO;

import java.util.List;


/**
 * TODO description
 *
 * @author ${author}
 * @date ${date}
 * @since 1.0
 */
public interface ${modelNameUpperCamel}Service  {

/**
     * 新增
     * @param reqDTO
     * @return
     */
    ${modelNameUpperCamel}ResDTO save(${modelNameUpperCamel}ReqDTO reqDTO);


    /**
    * 通过主键删除
    * @param id
    * @return
    */
    void deleteById(Integer id);

    /**
    * 更新
    * @param updateDTO
    * @return
    */
    ${modelNameUpperCamel}ResDTO update(${modelNameUpperCamel}ReqDTO updateDTO);

    /**
    * 通过ID查找
    * @param id
    * @return
    */
    ${modelNameUpperCamel}ResDTO findById(Integer id);



}
