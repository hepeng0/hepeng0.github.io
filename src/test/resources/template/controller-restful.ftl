/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package ${basePackage}.controller;

import ${basePackage}.dto.request.${modelNameUpperCamel}ReqDTO;
import ${basePackage}.dto.response.${modelNameUpperCamel}ResDTO;
import ${basePackage}.service.${modelNameUpperCamel}Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO description
 *
 * @author ${author}
 * @date ${date}
 * @since 1.0
 */
@RestController
@RequestMapping("${baseRequestMapping}")
public class ${modelNameUpperCamel}Controller {
    @Resource
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @PostMapping
    public ${modelNameUpperCamel}ResDTO add(@RequestBody ${modelNameUpperCamel}ReqDTO ${modelNameLowerCamel}ReqDTO) {
        return ${modelNameLowerCamel}Service.save(${modelNameLowerCamel}ReqDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        ${modelNameLowerCamel}Service.deleteById(id);
    }

    @PutMapping
    public ${modelNameUpperCamel}ResDTO  update(@RequestBody ${modelNameUpperCamel}ReqDTO ${modelNameLowerCamel}ReqDTO) {
        return ${modelNameLowerCamel}Service.update(${modelNameLowerCamel}ReqDTO);
    }

    @GetMapping("/{id}")
    public ${modelNameUpperCamel}ResDTO detail(@PathVariable Integer id) {
        return ${modelNameLowerCamel}Service.findById(id);
    }

}
