/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package ${basePackage}.service.convert;

import ${basePackage}.dto.request.${modelNameUpperCamel}ReqDTO;
import ${basePackage}.dto.response.${modelNameUpperCamel}ResDTO;
import ${basePackage}.model.${modelNameUpperCamel}DO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ${modelNameUpperCamel}Convert {

    public static ${modelNameUpperCamel}DO convert2DO(${modelNameUpperCamel}ReqDTO reqDTO) {
        ${modelNameUpperCamel}DO ${modelNameLowerCamel}DO = new ${modelNameUpperCamel}DO();
        BeanUtils.copyProperties(reqDTO,${modelNameLowerCamel}DO);
        return ${modelNameLowerCamel}DO;
    }
    public static List<${modelNameUpperCamel}DO> convert2DOList(List<${modelNameUpperCamel}ReqDTO> reqDTOs) {
    return  reqDTOs.stream().map(dto -> convert2DO(dto)).collect(Collectors.toList());
    }

    public static ${modelNameUpperCamel}ResDTO convert2ResDTO(${modelNameUpperCamel}DO ${modelNameLowerCamel}DO) {
    ${modelNameUpperCamel}ResDTO resDTO = new ${modelNameUpperCamel}ResDTO();
    BeanUtils.copyProperties(${modelNameLowerCamel}DO,resDTO);
    return resDTO;
    }

    public static List<${modelNameUpperCamel}ResDTO> convert2ResDTOList(List<${modelNameUpperCamel}DO> ${modelNameLowerCamel}DOs) {
        return  ${modelNameLowerCamel}DOs.stream().map(data -> convert2ResDTO(data)).collect(Collectors.toList());
    }
}
