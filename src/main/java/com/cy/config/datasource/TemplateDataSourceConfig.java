package com.cy.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(prefix = TemplateDataSourceConfig.DATASOURCE_CONFIG_PREFIX, name = "enable", havingValue = "true")
@ConditionalOnClass(DruidConfig.class)
@MapperScan(basePackages = {TemplateDataSourceConfig.MAPPER_SCAN_PACKAGE}, sqlSessionTemplateRef = TemplateDataSourceConfig.SESSION_TEMPLATE_BEAN_NAME)
public class TemplateDataSourceConfig {
    /**
     * 数据源名称, 可以自行定义, 一般以物理实例区分
     */
    protected static final String DATASOURCE_NAME = "template";

    /**
     * 数据源对应bean名称
     */
    protected static final String DATASOURCE_BEAN_NAME = DATASOURCE_NAME + "DataSource";

    /**
     * 数据源对应SessionTemplate名称
     */
    protected static final String SESSION_TEMPLATE_BEAN_NAME = DATASOURCE_NAME + "SessionTemplate";

    /**
     * 数据源对应SessionFactory名称
     */
    protected static final String SESSION_FACTORY_BEAN_NAME = DATASOURCE_NAME + "SessionFactory";

    /**
     * 数据源配置前缀, 读取该数据源对应的yaml中的前缀, 不存在时不会加载此类对应的所有bean
     */
    protected static final String DATASOURCE_CONFIG_PREFIX = "spring.datasource.druid." + DATASOURCE_NAME;

    /**
     * 数据源扫描的包路径
     */
    protected static final String MAPPER_SCAN_PACKAGE = "com.cy.mapper." + DATASOURCE_NAME;

    /**
     * 数据源扫描的XML路径
     */
    protected static final String MAPPER_SCAN_XML_LOCATION = "classpath:mapper/" + DATASOURCE_NAME + "/**/*.xml";

    @Bean(DATASOURCE_BEAN_NAME)
    @ConfigurationProperties(DATASOURCE_CONFIG_PREFIX)
    public DataSource dataSource(DruidConfig druidConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setName(DATASOURCE_NAME);

        return druidConfig.config(dataSource);
    }

    /**
     * 返回data数据库的会话工厂
     */
    @Bean(SESSION_FACTORY_BEAN_NAME)
    public SqlSessionFactory getSqlSessionFactory(@Qualifier(DATASOURCE_BEAN_NAME) DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setGlobalConfig(new GlobalConfig().setBanner(false));
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_SCAN_XML_LOCATION));
        //全局下划线转驼峰
        Objects.requireNonNull(bean.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true);

        return bean.getObject();
    }

    @Bean(SESSION_TEMPLATE_BEAN_NAME)
    public SqlSessionTemplate getSessionTemplate(@Qualifier(SESSION_FACTORY_BEAN_NAME) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

