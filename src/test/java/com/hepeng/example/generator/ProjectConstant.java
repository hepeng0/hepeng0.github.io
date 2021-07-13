/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.hepeng.example.generator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目常量
 */
public final class ProjectConstant {

    //生成代码所在的基础包名称，可根据自己公司的项目修改（注意：这个配置修改之后需要手工修改src目录项目默认的包路径，使其保持一致，不然会找不到类）
    public static final String BASE_PACKAGE = "com.hepeng.example";

    //生成的Model所在包
    public static final String MODEL_PACKAGE = BASE_PACKAGE + "domain.po";

    //生成的Mapper所在包
    public static final String MAPPER_PACKAGE = BASE_PACKAGE + ".mapper";

    //生成的Service所在包
    public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";

    //生成的ServiceImpl所在包
    public static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";

    //生成的Controller所在包
    public static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller";

    //生成的dto request所在包
    public static final String DTO_REQ_PACKAGE = BASE_PACKAGE + ".domain.dto.request";

    //生成的dto response所在包
    public static final String DTO_RES_PACKAGE = BASE_PACKAGE + "domain.dto.response";

    //Mapper插件基础接口的完全限定名
    public static final String MAPPER_INTERFACE_REFERENCE = "tk.mybatis.mapper.common.Mapper,tk.mybatis.mapper.common.special.InsertUseGeneratedKeysMapper";

    //生成的dto response 文件名后缀
    public static final String DTO_RES_SUFFIX = "ResDTO";

    //生成的dto request 文件名后缀
    public static final String DTO_REQ_SUFFIX = "ReqDTO";


    //项目在硬盘上的基础路径
    public static final String PROJECT_PATH = System.getProperty("user.dir");

    //模板位置
    public static final String TEMPLATE_FILE_PATH = PROJECT_PATH + "/src/test/resources/generator/template";

    //java文件路径
    public static final String JAVA_PATH = "/src/main/java";
    //资源文件路径
    public static final String RESOURCES_PATH = "/src/main/resources";

    //@date
    public static final String DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());


    //生成的DTO_REQ实现存放路径
    public static final String PACKAGE_PATH_DTO_REQ = packageConvertPath(ProjectConstant.DTO_REQ_PACKAGE);

    //生成的DTO_RES实现存放路径
    public static final String PACKAGE_PATH_DTO_RES = packageConvertPath(ProjectConstant.DTO_RES_PACKAGE);

    //生成的Service存放路径
    public static final String PACKAGE_PATH_SERVICE = packageConvertPath(ProjectConstant.SERVICE_PACKAGE);

    //生成的Service实现存放路径
    public static final String PACKAGE_PATH_SERVICE_IMPL = packageConvertPath(ProjectConstant.SERVICE_IMPL_PACKAGE);


    //生成的Controller存放路径
    public static final String PACKAGE_PATH_CONTROLLER = packageConvertPath(ProjectConstant.CONTROLLER_PACKAGE);


    private static String packageConvertPath(String packageName) {
        return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
    }
}
