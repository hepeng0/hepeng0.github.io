---
title: 数据库版本管理之Liquibase使用指南
date: 2021-08-11 10:12:37
tags: [数据库版本管理]
categories: 实用工具
---

### 为什么需要数据库版本管理
研发过程中经常涉及到数据库变更，对表结构的修复及对数据的修改，为了保证各环境都能正确的进行变更我们可能需要维护一个数据库升级文档来保存这些记录，有需要升级的环境按文档进行升级。

这样手工维护有几个缺点：

* 无法保证每个环境都按要求执行
* 遇到问题不一定有相对的回滚语句
* 无法自动化

为了解决这些问题，我们进行了一些调研，主要调研对象是Liquibase和Flyway，我们希望通过数据库版本管理工具实现以下几个目标：

* 数据库升级
* 数据库回滚
* 版本标记

### 数据库版本管理工具Liquibase简介
首先，上[官方文档](https://docs.liquibase.com/home.html)

#### 核心概念
首先，Liquibase是用于管理数据库版本的，所以就会有这些概念：
* 版本号
  * 它的版本号由开发人员来维护，使用 author + id(由ChangeSet定义)
* 管理的数据
* 差异比较
* 版本回滚

提交数据，比较差异，版本回滚 可以使用命令行 或者 maven ，ant 等构建工具来完成

##### Changelog 文件
开发人员将数据库更改存储在其本地开发计算机上基于文本的文件中，并将其应用于其本地数据库。Changelog文件可以任意嵌套，以便更好地管理。

所有Liquibase更改的根源是更改日志文件, Liquibase使用更改日志按顺序列出对数据库所做的所有更改。

它是一个包含所有数据库更改记录的文件（变更集s）, Liquibase使用此更改日志记录审核您的数据库并执行尚未应用于您的数据库的任何更改。

**可用属性**

* logicalFilePath: 用于在创建changeSet的唯一标识符时覆盖文件名和路径。移动或重命名change logs时是必需的。

**可用的子标签**

* preConditions: 执行更改日志所需的先决条件。[read more](http://www.liquibase.org/documentation/preconditions.html)

  * 记录更改日志的编写者在创建changelog时的假设。
  * 强制使运行change log的用户不会违反这些假设
  * 在执行不可恢复的更改（如 drop_Table）之前执行数据检查
  * 根据数据库的状态控制哪些changeSet运行
    
    <details><summary>demo</summary>
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    
    <databaseChangeLog
    xmlns="http://www.liquibase.org/xml/ns/dbchangelog/1.8"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog/1.8
    http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.8.xsd">
        <preConditions>
            <dbms type="oracle" />
            <runningAs username="SYSTEM" />
        </preConditions>
    
        <changeSet id="1" author="bob">
            <preConditions onFail="WARN">
                <sqlCheck expectedResult="0">select count(*) from oldtable</sqlCheck>
            </preConditions>
            <comment>Comments should go after preCondition. If they are before then liquibase usually gives error.</comment>
            <dropTable tableName="oldtable"/>
        </changeSet>
    </databaseChangeLog>
    ```
    仅当针对 Oracle执行的数据库和执行脚本的数据库用户为SYSTEM时，才会运行上述databasechangelog。
    仅当"oldtable"中没有值时，它才会运行 drop_Table命令。
    </details>
    
* property: 将属性设置为的值（如果不是通过其他方法设置）。[read more](http://www.liquibase.org/documentation/changelog_parameters.html)
* changeSet: 要执行的changeSet。[read more](http://www.liquibase.org/documentation/changeset.html)
* include: 包含要执行的changeSet的其他文件。[read more](http://www.liquibase.org/documentation/include.html)

当 Liquibase 迁移器运行时，它将分析数据库 ChangeLog 标记。它首先检查指定的先决条件。如果先决条件失败，Liquibase将退出，并显示一条错误消息，解释失败的原因。先决条件对于记录和强制执行更改日志编写器的预期和假设（如要针对的 DBMS 或以用户身份运行更改）非常有用。

如果满足所有的先决条件，Liquibase将会开始运行在databaseChangeLog文件中按照顺序出现changeSet和include标签。

**changelog文件格式说明**

具体格式参考[官方文档](https://docs.liquibase.com/concepts/basic/changelog.html)

本文列举两种常见格式:
* SQL 文件格式

  其实各种文件格式使用生成数据库脚本就可以看到格式了，照着写就行：
    ```
    --liquibase formatted sql
    
    --changeset <author>:<version> 
    sqls
    
    --rollback rollback sqls 
    
    --comment: 注释都有特殊含义了，所以注释要这样加
    ```

* XML 文件格式

  xml 比 sql 更加可控，它可以加一个预判断条件，来判断这个后面的 changeSet 要不要执行，但相应的就必须照它的语法来写语句了，没 sql 方便了，还好提供了 xsd
    ```
    <preConditions>
        <runningAs username="liquibase"/>
    </preConditions>
    <!-- 版本 1 的修改-->
    <changeSet id="1" author="sanri">
        <addColumn tableName="person">
            <column name="username" type="varchar(8)"/>
        </addColumn>
    </changeSet>
    ```
  
##### ChangeSet
changeSet由author和id属性以及changelog文件的位置唯一标识，是 Liquibase 跟踪执行的单位（管理的数据最小单元）。

changeSet 可以用 xml,yaml,json,sql 来编写

运行 Liquibase 时，它会查询标记为已执行的changSet的DATABASECHANGELOG 表，然后执行更改日志文件中尚未执行的所有changeSet。

##### Changes
每个changeSet通常包含一个更改，该更改描述要应用于数据库的更改/重构。

Liquibase 支持为支持的数据库和原始 SQL 生成 SQL 的描述性更改。

通常，**每个changeSet应只有一个更改**，以避免可能使数据库处于意外状态的自动提交语句失败。

##### Preconditions
先决条件可以应用于整个changelog或单个changeSet。如果先决条件失败，liquibase将停止执行。

##### Contexts
可以将上下文应用于changeSet，以控制在不同环境中运行的changeSet。例如，某些changeSet可以标记为production，另一些可以标记为test。如果未指定上下文，则无论执行上下文如何，changset都将运行。

#### Liquibase是如何工作的

Liquibase的核心是依靠一种简单的机制来跟踪、版本和部署更改：

* Liquibase 使用更改日志（是更改的分类）按特定顺序显式列出数据库更改。更改日志中的每个更改都是一个change set。更改日志可以任意嵌套，以帮助组织和管理数据库迁移。
    * 最佳做法是确保每个change set都尽可能原子性更改，以避免失败的结果使数据库中剩下的未处理的语句处于unknown 状态;
    * 不过，可以将大型 SQL 脚本视为单个更改集。
* Liquibase 使用跟踪表（具体称为DATABASECHANGELOG），该表位于每个数据库上，并跟踪已部署更改日志中的change set。
    * 如果 Liquibase所在的数据库没有跟踪表，Liquibase 将创建一个跟踪表。
    * 为了协助处理您未从空白数据库开始的项目，Liquibase具有生成一条更改日志以表示数据库模式当前状态的功能。
        ```
        它会在你的目标数据库生成一张表 DATABASECHANGELOG 来管理版本 ，另一个 lock 的是防止多人同时操作数据库加的锁。
        ```

使用分类和跟踪表，Liquibase 能够：

* 跟踪和以版本控制数据库更改 – 用户确切知道已部署到数据库的更改以及尚未部署的更改。
* 部署更改 — 具体来说，通过将分类(ledger)中的内容与跟踪表中的内容进行比较，Liquibase 只能将以前尚未部署到数据库的更改部署到数据库中。
    * Liquibase 具有上下文、标签和先决条件等高级功能，可精确控制changeSet的部署时间以及位置。

#### liquibase使用
##### 命令行方式
虽然使用可以集成自 springboot ，但这种数据库脚本一般公司都是运维在维护，使用命令行是最方便的方式，所以我先说下使用命令行, [官网示例](http://www.liquibase.org/documentation/command_line.html) 

为先为了不每次都要写一大堆参数，可以在 liquibase 根目录加一个 liquibase.properties，用于配置数据库 jar、url、用户名、密码等参数, [配置详情](http://www.liquibase.org/documentation/config_properties.html) 

命令格式： liquibase [options] [command] [command parameters]

###### 比较开发库和测试库的差异，并生成升级包
如果要升级哪个，则哪个要做为源，则配置中的 url 不是 referenceUrl，使用如下命令创建升级包
```shell
liquibase --changeLogFile="changeLogFiledevtest.postgresql.sql" diffChangeLog
```
changeLogFile 是有命名规则的，命名必须为 *.dbType.format ，如上所示

###### 为测试库打一个标签
```shell
liquibase tag v1.0
```

###### 使用差异升级源库
```shell
liquibase --changeLogFile="sqls/changeLogFiledevtest.postgresql.sql" update
```

###### 升级有问题需要回滚
liquibase 有几种回滚策略，一种是根据标签回滚，回滚次数，和根据日期回滚；有 9 个与之对应的命令

回滚要求对应的 changeLogFile 有回滚标签 ，这个在后面文件格式说

```shell
# 按照 changeSet 次数回滚
liquibase  --changeLogFile="sqls/changeLogFiledevtest.postgresql.sql" rollbackCount 1
# 按照标签回滚
liquibase  --changeLogFile="sqls/changeLogFiledevtest.postgresql.sql" rollback v1.0
```

###### 生成数据库脚本(新环境)

liquibase --changeLogFile="sqls/create_table.mysql.sql"  generateChangeLog

##### 使用构建工具
我们也可以使用 maven 来执行这些操作，引入 maven 的一个插件就行
```xml
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>3.6.3</version>
    <configuration>
                    <!--指定执行主文件 -->
<!--                    <changeLogFile>${basedir}/src/main/resources/liquibase/master_changelog.xml</changeLogFile>-->
<!--                    <diffChangeLogFile>${basedir}/src/main/resources/liquibase/changelog/${maven.build.timestamp}_changelog.xml</diffChangeLogFile>-->
<!--                    <outputChangeLogFile>${basedir}/src/main/resources/liquibase/changelog/changelog_original.xml</outputChangeLogFile>-->

                   <propertyFile>src/main/resources/liquibase/liquibase.properties</propertyFile>

                    <dropFirst>false</dropFirst>
                    <verbose>true</verbose>
                    <logging>debug</logging>
                    <!-- 是否需要弹出确认框 -->
                    <promptOnNonLocalDatabase>false</promptOnNonLocalDatabase>
                    <!--输出文件的编码 -->
                    <outputFileEncoding>UTF-8</outputFileEncoding>
                    <!--执行的时候是否显示详细的参数信息 -->
                    <verbose>true</verbose>
                    <!--是否每次都重新加载properties -->
                    <propertyFileWillOverride>true</propertyFileWillOverride>
                    <rollbackTag>${project.version}</rollbackTag>
                    <tag>${project.version}</tag>
                </configuration>
</plugin>
```

相应的命令做成了目标(goal)，使用 -Dkey=value 来指定参数，如
```shell
# 执行更新 sql 
mvn liquibase:update -DchangeLogFile="file"
# 打标签，这个版本号在插件中配置成项目版本了
mvn liquibase:tag 
# 将当前库导出表结构
mvn liquibase:generateChangeLog 
```

##### 集成进 springboot, 在项目启动的时候执行版本管理

具体实现方案参考文章[springboot引入liquibase](https://blog.csdn.net/qq_39508627/article/details/89883549?utm_medium=distribute.pc_feed_404.none-task-blog-2~default~BlogCommendFromBaidu~default-3.nonecase&depth_1-utm_source=distribute.pc_feed_404.none-task-blog-2~default~BlogCommendFromBaidu~default-3.nonecas)

### 最佳实践
#### 项目开发中存在的问题
随着项目的发展，一个项目中的代码量会非常庞大，同时数据库表也会错综复杂。如果一个项目使用了Liquibase对数据库结构进行管理，越来越多的问题会浮现出来。
* ChangeSet文件同时多人在修改，自己的ChangeSet被改掉，甚至被删除掉。
* 开发人员将ChangeSet添加到已经执行过的文件中，导致执行顺序出问题。
* 开发人员擅自添加对业务数据的修改，其它环境无法执行并报错。
* ChangeSet中SQL包含schema名称，导致其它环境schema名称变化时，ChangeSet报错。
* 开发人员不小心改动了已经执行过的ChangeSet，在启动时会报错。

#### Liquibase基本规范
* ChangeSet id使用[任务ID]-[日期]-[序号]，如 T100-20181009-001
* ChangeSet必须填写author
* Liquibase禁止对业务数据进行sql操作
* 使用<sql>时，禁止包含schema名称
* Liquibase禁止使用存储过程
* 所有表，列要加remarks进行注释
* 已经执行过的ChangeSet严禁修改。
* 不要随便升级项目liquibase版本，特别是大版本升级。不同版本ChangeSet MD5SUM的算法不一样。

其它数据库规范不再赘述。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
    <changeSet id="T100-20181009-001" author="markfredchen" >
        <createTable tableName="demo_user" remarks="用户表">
            <column name="id" type="bigint" remarks="用户ID,主键">
                <constraints nullable="false" primaryKey="true" primaryKeyName="pk_demo_user_id"/>
            </column>
            <column name="username" type="varchar(100)" remarks="用户名">
                <constraints nullable="false"/>
            </column>
            ...
        </createTable>
    </changeSet>
</databaseChangeLog>
```

#### 有效文件管理
使用Liquibase中提供<include file="xxx"/>tag，可以将ChangeSet分布在不同文件中。同时<include/>支持多级引用。

基于此功能可以对项目中的ChangeSet进行有效管理。推荐使用以下规范进行管理。

##### 根据发布进行管理
* 每个发布新建一个文件夹，所有发布相关的ChangeSet文件以及数据初始化文件，均放在些文件夹中。
* 每个发布新建一个master.xml。此master.xml中，include本次发布需要执行的ChangeSet文件
* 根据开发小组独立ChangeSet文件(可选)
* 根据功能独立ChangeSet文件。例如user.xml, company.xml
    ```
    resources
    |-liquibase
    |-user
    | |- master.xml
    | |- release.1.0.0
    | | |- release.xml
    | | |- user.xml -- 用户相关表ChangeSet
    | | |- user.csv -- 用户初始化数据
    | | |- company.xml -- 公司相关表ChangeSet
    | |- release.1.1.0
    | | |- release.xml
    | | |- ...
    ```
  
##### 模块化管理
当项目变得庞大之后，一个服务可能包含的功能模块会越来越多。此时大家会想尽办法进行模块拆分，逐步进行微服务化。然而在面对错综复杂的Liquibase ChangeSet就会无从下手。

针对这种将来可能会面对的问题，项目初期就对Liquibase进行模块化管理，将在未来带来很大收益。

首先说明一下Spring Boot中Liquibase默认是如何执行以及执行结果。
* 在启动时，LiquibaseAutoConfiguration会根据默认配置初始化SpringLiquibase
* SpringLiquibase.afterPropertiesSet()中执行ChangeSet文件
* 第一次跑ChangeSets的时候，会在数据库中自动创建两个表databasechangelog和databasechangeloglock

因此我们可以认为一个SpringLiquibase执行为一个模块。

引入多模块管理时，基于上节文件管理规范，我们基于模块管理再做下调整。

```
resources
  |-liquibase
    |-user
    | |- master.xml
    | |- release.1.0.0
    | | |- release.xml
    | | |- user.xml -- 用户相关表ChangeSet
    | | |- user.csv -- 用户初始化数据
    | | |- company.xml -- 公司相关表ChangeSet
    | |- release.1.1.0
    | | |- release.xml
    | | |- ...
    |- order
    | |- master.xml
    | |- release.1.0.0
    | | |- ...
```

当有一天我们需要把订单模块拆分成独立服务时，我们只需要将模块相关的ChangeSet文件迁出来。即可完成数据结构的拆分。

那如何在一个Spring Boot运行多个SpringLiquibase呢？需要对代码进行以下调整。

1. 禁用Spring Boot自动运行Liquibase。

    当以下配置被启用时，Spring Boot AutoConfigure会使用默认配置初始化名为springLiquibase的Bean。然后我们不对其进行配置，Spring Boot启动时会报错。
   
    ```properties
    # application.properties
    # spring boot 2以上
    spring.liquibase.enabled=false
    # spring boot 2以下
    liquibase.enabled=false
    ```
   
2. Spring Boot配置Liquibase Bean

    配置两个SpringLiquibase Bean，Bean名称分别为userLiquibase和orderLiqubase。

    ```java
    @Configuration
    public class LiquibaseConfiguration() {
    
        /**
         *  用户模块Liquibase   
         */
        @Bean
        public SpringLiquibase userLiquibase(DataSource dataSource) {
            SpringLiquibase liquibase = new SpringLiquibase();
            // 用户模块Liquibase文件路径
            liquibase.setChangeLog("classpath:liquibase/user/master.xml");
            liquibase.setDataSource(dataSource);
            liquibase.setShouldRun(true);
            liquibase.setResourceLoader(new DefaultResourceLoader());
            // 覆盖Liquibase changelog表名
            liquibase.setDatabaseChangeLogTable("user_changelog_table");
            liquibase.setDatabaseChangeLogLockTable("user_changelog_lock_table");
            return liquibase;
        }
        /**
         *  订单模块Liquibase   
         */
        @Bean
        public SpringLiquibase orderLiquibase() {
          SpringLiquibase liquibase = new SpringLiquibase();
          liquibase.setChangeLog("classpath:liquibase/order/master.xml");
          liquibase.setDataSource(dataSource);
          liquibase.setShouldRun(true);
          liquibase.setResourceLoader(new DefaultResourceLoader());
          liquibase.setDatabaseChangeLogTable("order_changelog_table");
          liquibase.setDatabaseChangeLogLockTable("order_changelog_lock_table");
          return liquibase;
        }
    }
    ```
   
### 参考文献

[LiquiBase中文学习指南](https://blog.csdn.net/u012934325/article/details/100652805)