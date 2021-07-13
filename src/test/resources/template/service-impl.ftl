/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package ${basePackage}.service.impl;

import ${basePackage}.dto.request.${modelNameUpperCamel}ReqDTO;
import ${basePackage}.dto.response.${modelNameUpperCamel}ResDTO;
import ${basePackage}.mapper.${modelNameUpperCamel}Mapper;
import ${basePackage}.model.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * TODO description
 *
 * @author ${author}
 * @date ${date}
 * @since 1.0
 */
@Service
public class ${modelNameUpperCamel}ServiceImpl  implements ${modelNameUpperCamel}Service {
    @Resource
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;


    @Override
    public ${modelNameUpperCamel}ResDTO save(${modelNameUpperCamel}ReqDTO reqDTO) {
        //TODO 请实现具体的业务逻辑
        return null;
    }


    @Override
    public void deleteById(Integer id) {
        //TODO 请实现具体的业务逻辑
    }

    @Override
    public ${modelNameUpperCamel}ResDTO update(${modelNameUpperCamel}ReqDTO updateDTO) {
        //TODO 请实现具体的业务逻辑
        return null;
    }

    @Override
    public ${modelNameUpperCamel}ResDTO findById(Integer id) {
        //TODO 请实现具体的业务逻辑
        return null;
    }

}
