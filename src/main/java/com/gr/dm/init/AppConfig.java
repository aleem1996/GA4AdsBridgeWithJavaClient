package com.gr.dm.init;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import com.gr.dm.core.util.Constants;


@Configuration
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:dm.properties"),
    @PropertySource("classpath:hibernate.properties"),
    @PropertySource("classpath:queries.properties")
})
public class AppConfig {

	@Autowired
	private Environment env;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Constants.THREAD_CORE_POOL_SIZE);
		executor.setMaxPoolSize(Constants.THREAD_POOL_MAX_SIZE);
		executor.setQueueCapacity(Constants.THREAD_QUEUE_CAPACITY);
		executor.setThreadNamePrefix("AnalyticsSync-");
		executor.initialize();
		return executor;
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(Constants.THREAD_POOL_SIZE);
		return threadPoolTaskScheduler;
	}

	@Bean
	public JavaMailSender getMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost(env.getProperty("dm.mail.host"));
		mailSender.setPort(Integer.valueOf(env.getProperty("dm.mail.port")));
		mailSender.setUsername(env.getProperty("dm.mail.username"));
		mailSender.setPassword(env.getProperty("dm.mail.password"));

		Properties javaMailProperties = mailSender.getJavaMailProperties();
		javaMailProperties.put("mail.smtp.starttls.enable", env.getProperty("dm.mail.smtp.starttls.enable"));
		javaMailProperties.put("mail.smtp.auth", env.getProperty("dm.mail.smtp.auth"));
		javaMailProperties.put("mail.transport.protocol", env.getProperty("dm.mail.transport.protocol"));
		javaMailProperties.put("mail.debug", env.getProperty("dm.mail.debug"));

		return mailSender;
	}

	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(cacheMangerFactory().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean cacheMangerFactory() {
		EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		bean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		bean.setShared(true);
		return bean;
	}

	@Bean
	public RestTemplate restTemplate() {

		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
		restTemplate.setInterceptors(Collections.singletonList(new DmClientHttpRequestInterceptor()));
		restTemplate.setErrorHandler(new DmClientHttpResponseErrorHandler());
		return restTemplate;
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(180000);
		factory.setConnectTimeout(180000);
		return factory;
	}
}