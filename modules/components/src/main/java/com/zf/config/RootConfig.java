package com.zf.config;

import java.beans.PropertyVetoException;
import java.io.IOException;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@ComponentScan(basePackages = "com.zf", excludeFilters = { @Filter(Configuration.class), @Filter(Controller.class) })
@PropertySources({ @PropertySource("classpath:jdbc.properties"), @PropertySource("classpath:attachment.properties") })
@MapperScan("com.zf.dao")
@EnableTransactionManagement
@EnableCaching
public class RootConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource(@Value("${db.driverClassName}") String driverClassName, @Value("${db.url}") String url, @Value("${db.username}") String username, @Value("${db.password}") String password) throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource, @Value("#{'classpath:mybatis/mappers/**/*.xml'}") Resource[] resources) throws IOException {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(resources);
		bean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
		bean.setTypeAliasesPackage("com.zf.model");
		return bean;
	}

	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public CommonsMultipartResolver multipartResolver(@Value("${UPLOAD_MAX_SIZE}") long maxUploadSize) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(maxUploadSize);
		multipartResolver.setResolveLazily(true);
		return multipartResolver;
	}

	@Bean
	public CacheManager getEhCacheManager() {
		return new EhCacheCacheManager(getEhCacheFactory().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean getEhCacheFactory() {
		EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
		factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		factoryBean.setShared(true);
		return factoryBean;
	}
}
