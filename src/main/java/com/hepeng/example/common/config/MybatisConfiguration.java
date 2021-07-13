//package com.hepeng.example.common.config;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import tk.mybatis.spring.mapper.MapperScannerConfigurer;
//
//import javax.sql.DataSource;
//import java.util.Properties;
//
///**
// * description MybatisConfiguration
// * @author hepeng [he.peng@unisinsight.com]
// * @date 2020/4/28 11:24
// * @since 1.0
// */
//@Configuration
//public class MybatisConfiguration {
//    @Bean
//    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setVfs(SpringBootVFS.class);
//        factory.setTypeAliasesPackage("com.hepeng.example.domain.po");
//
//        //添加XML目录
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        factory.setMapperLocations(resolver.getResources("classpath:mybatis/*.xml"));
//        return factory.getObject();
//    }
//
//
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
//        mapperScannerConfigurer.setBasePackage("com.hepeng.example.mapper");
//
//        //配置通用Mapper，详情请查阅官方文档
//        Properties properties = new Properties();
//        properties.setProperty("notEmpty", "false"); //insert、update是否判断字符串类型!='' 即 test="str != null"表达式内是否追加 and str != ''
//        properties.setProperty("IDENTITY", "POSTGRES");
//        mapperScannerConfigurer.setProperties(properties);
//
//        return mapperScannerConfigurer;
//    }
//}
