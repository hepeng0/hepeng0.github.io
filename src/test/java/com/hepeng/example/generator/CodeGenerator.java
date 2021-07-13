/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */
package com.hepeng.example.generator;

import com.google.common.base.CaseFormat;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器，根据数据表名称生成对应的Model、Mapper、Controller简化开发。
 *
 * @author t17153 [tan.gang@h3c.com]
 * @date 2018/9/13 22:10
 * @since 1.0
 */
public class CodeGenerator {
    //JDBC配置，请修改为你项目的实际配置
    private static final String JDBC_URL = "jdbc:postgresql://192.168.110.22:5432/icap?currentSchema=attendance";
    private static final String JDBC_USERNAME = "postgres";
    private static final String JDBC_PASSWORD = "postgres";
    private static final String JDBC_DIVER_CLASS_NAME = "org.postgresql.Driver";

    //@author
    private static final String AUTHOR = "hepeng [he.peng@unisinsight.com]";




    public static void main(String[] args) {
        Assert.hasLength(AUTHOR,"AUTHOR不能为空");
        //genCode("","attendant_info");
        genCode("","attendant_group");
        //genCode("","attendance_record");
        //genCode("","attendance_statistics");
        //genCode("","attendance_rule");
        //genCodeByCustomModelName("输入表名","输入自定义Model名称");
    }

    /**
     * 通过数据表名称生成代码，Model 名称通过解析数据表名称获得，下划线转大驼峰的形式。
     * 如输入表名称 "t_user_detail" 将生成 TUserDetail、TUserDetailMapper、TUserDetailService ...
     * @param tableNames 数据表名称...
     */
    public static void genCode(String schema, String... tableNames) {
        for (String tableName : tableNames) {
            genCodeByCustomModelName(schema, tableName, null);
        }
    }

    /**
     * 通过数据表名称，和自定义的 Model 名称生成代码
     * 如输入表名称 "t_user_detail" 和自定义的 Model 名称 "User" 将生成 User、UserMapper、UserService ...
     * @param tableName 数据表名称
     * @param modelName 自定义的 Model 名称
     */
    public static void genCodeByCustomModelName(String schema, String tableName, String modelName) {
        genModelAndMapper(schema, tableName, modelName);
        genEmptyDTO(tableName, modelName);
        genService(tableName, modelName);
        genController(tableName, modelName);
    }


    public static void genModelAndMapper(String schema, String tableName, String modelName) {
        Context context = new Context(ModelType.FLAT);
        context.setId("Potato");
        context.setTargetRuntime("MyBatis3Simple");
/*        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");*/

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(JDBC_URL);
        jdbcConnectionConfiguration.setUserId(JDBC_USERNAME);
        jdbcConnectionConfiguration.setPassword(JDBC_PASSWORD);
        jdbcConnectionConfiguration.setDriverClass(JDBC_DIVER_CLASS_NAME);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
        pluginConfiguration.addProperty("mappers", ProjectConstant.MAPPER_INTERFACE_REFERENCE);
        context.addPluginConfiguration(pluginConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH);
        javaModelGeneratorConfiguration.setTargetPackage(ProjectConstant.MODEL_PACKAGE);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(ProjectConstant.PROJECT_PATH + ProjectConstant.RESOURCES_PATH);
        sqlMapGeneratorConfiguration.setTargetPackage("mybatis");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetProject(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH);
        javaClientGeneratorConfiguration.setTargetPackage(ProjectConstant.MAPPER_PACKAGE);
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        if(StringUtils.isNotEmpty(schema)) tableConfiguration.setSchema(schema);
        if (StringUtils.isNotEmpty(modelName)) {
            tableConfiguration.setDomainObjectName(modelName);
        } else {
            tableConfiguration.setDomainObjectName(tableNameConvertUpperCamel(tableName));

        }
        //tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
        context.addTableConfiguration(tableConfiguration);

        List<String> warnings;
        MyBatisGenerator generator;
        try {
            Configuration config = new Configuration();
            config.addContext(context);
            config.validate();

            boolean overwrite = true;
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            warnings = new ArrayList<>();
            generator = new MyBatisGenerator(config, callback, warnings);
            generator.generate(null);
        } catch (Exception e) {
            throw new RuntimeException("生成Model和Mapper失败", e);
        }

        if (generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
            throw new RuntimeException("生成Model和Mapper失败：" + warnings);
        }
        if (StringUtils.isEmpty(modelName)) modelName = tableNameConvertUpperCamel(tableName);
        System.out.println(modelName + ".java 生成成功");
        System.out.println(modelName + "Mapper.java 生成成功");
        System.out.println(modelName + "Mapper.xml 生成成功");
    }


    public static void genService(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", ProjectConstant.DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName): modelName;
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", tableNameConvertLowerCamel(tableName));

            data.put("basePackage", ProjectConstant.BASE_PACKAGE);

            File file = new File(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH + ProjectConstant.PACKAGE_PATH_SERVICE + modelNameUpperCamel + "Service.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("service.ftl").process(data,
                    new FileWriter(file));
            System.out.println(modelNameUpperCamel + "service.java 生成成功");

            File file1 = new File(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH + ProjectConstant.PACKAGE_PATH_SERVICE_IMPL + modelNameUpperCamel + "ServiceImpl.java");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            cfg.getTemplate("service-impl.ftl").process(data,
                    new FileWriter(file1));
            System.out.println(modelNameUpperCamel + "ServiceImpl.java 生成成功");
        } catch (Exception e) {
            throw new RuntimeException("生成Service失败", e);
        }
    }

    public static void genEmptyDTO(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", ProjectConstant.DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName): modelName;

            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", tableNameConvertLowerCamel(tableName));

            data.put("basePackage", ProjectConstant.BASE_PACKAGE);

            File file = new File(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH + ProjectConstant.PACKAGE_PATH_DTO_REQ + modelNameUpperCamel + ProjectConstant.DTO_REQ_SUFFIX + ".java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("dto-req.ftl").process(data,
                    new FileWriter(file));
            System.out.println(modelNameUpperCamel + ProjectConstant.DTO_REQ_SUFFIX + ".java 生成成功 该文件是空文件，根据实际需求，增加变量定义！");

            File file1 = new File(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH + ProjectConstant.PACKAGE_PATH_DTO_RES + modelNameUpperCamel + ProjectConstant.DTO_RES_SUFFIX + ".java");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            cfg.getTemplate("dto-res.ftl").process(data,
                    new FileWriter(file1));
            System.out.println(modelNameUpperCamel + ProjectConstant.DTO_RES_SUFFIX + ".java 生成成功 该文件是空文件，根据实际需求，增加变量定义！");
        } catch (Exception e) {
            throw new RuntimeException("生成DTO失败", e);
        }
    }

    public static void genController(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", ProjectConstant.DATE);
            data.put("author", AUTHOR);
            String modelNameUpperCamel = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            data.put("baseRequestMapping", modelNameConvertMappingPath(modelNameUpperCamel));
            data.put("modelNameUpperCamel", modelNameUpperCamel);
            data.put("modelNameLowerCamel", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
            data.put("basePackage", ProjectConstant.BASE_PACKAGE);

            File file = new File(ProjectConstant.PROJECT_PATH + ProjectConstant.JAVA_PATH + ProjectConstant.PACKAGE_PATH_CONTROLLER + modelNameUpperCamel + "Controller.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.getTemplate("controller-restful.ftl").process(data, new FileWriter(file));

            System.out.println(modelNameUpperCamel + "Controller.java 生成成功");
        } catch (Exception e) {
            throw new RuntimeException("生成Controller失败", e);
        }

    }

    private static freemarker.template.Configuration getConfiguration() throws IOException {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
        cfg.setDirectoryForTemplateLoading(new File(ProjectConstant.TEMPLATE_FILE_PATH));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return cfg;
    }

    private static String tableNameConvertLowerCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
    }

    private static String tableNameConvertUpperCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());

    }

    private static String tableNameConvertMappingPath(String tableName) {
        tableName = tableName.toLowerCase();//兼容使用大写的表名
        return "/" + (tableName.contains("_") ? tableName.replaceAll("_", "/") : tableName);
    }

    private static String modelNameConvertMappingPath(String modelName) {
        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
        return tableNameConvertMappingPath(tableName);
    }



}
