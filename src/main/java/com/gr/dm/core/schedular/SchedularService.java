package com.gr.dm.core.schedular;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gr.dm.core.service.AnalyticsSyncService;
import com.gr.dm.core.util.DateUtil;

/**
 * 
 * This service is used to load and verify data from Adwords, Analytics,
 * Facebook and Bing.
 *
 */
@Service
public class SchedularService {

	public static final Logger logger = LoggerFactory.getLogger(SchedularService.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Value("${dm.scheduler.data_load.daysfrom}")
	private Integer daysFrom;

	@Value("${dm.scheduler.data_verification.facebook.daysfrom}")
	private Integer facebookVerificationDaysFrom;
	
	@Value("${dm.scheduler.data_verification.stackadapt.daysfrom}")
	private Integer stackAdaptVerificationDaysFrom;

	@Value("${dm.scheduler.data_verification.dynamics.daysfrom}")
	private Integer dynamicsVerificationDaysFrom;

	@Value("${dm.scheduler.data_verification.bing.daysfrom}")
	private Integer bingVerificationDaysFrom;
	
	@Value("${dm.scheduler.data_verification.google.daysfrom}")
	private Integer googleVerificationDaysFrom;

	@Autowired
	AnalyticsSyncService syncService;

	@Autowired
	private Environment env;

	@Scheduled(cron = "${dm.scheduler.data_load.google.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadAnalyticsData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.google.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing analytics data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadAnalyticsData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}
	
//	@Scheduled(cron = "${dm.scheduler.data_load.googlega4.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadGA4Data() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.google.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing analytics data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadGA4Data(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_load.dynamics.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadDynamicsData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.dynamics.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing dynamics data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadDynamicsData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_load.google.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadGoogleAdsData() throws Exception {
		
		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.google.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing adwords data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadGoogleAdsData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_load.facebook.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadFacebookData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.facebook.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing facebook data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadFbData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_load.bing.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadBingData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.bing.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing bing data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadBingData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}
	
	@Scheduled(cron = "${dm.scheduler.data_load.stackadapt.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void loadStackAdaptData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.stackadapt.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for syncing stackadapt data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			Date startDate = DateUtil.addDaysToDate(endDate, -daysFrom);

			CompletableFuture<Boolean> result = this.syncService.loadStackAdaptData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}
	

	@Scheduled(cron = "${dm.scheduler.data_verification.google.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyAdwordsData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.google.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying adwords data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -googleVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyGoogleAdsData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_verification.google.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyAnalyticsData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.google.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying analytics data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -googleVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyAnalyticsData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}
	
//	@Scheduled(cron = "${dm.scheduler.data_verification.googlega4.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyGA4Data() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.google.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying analytics data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -googleVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyGA4Data(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_verification.facebook.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyFacebookData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.facebook.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying facebook data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -facebookVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyFacebookData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_verification.bing.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyBingData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.bing.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying bing data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -bingVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyBingData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}

	@Scheduled(cron = "${dm.scheduler.data_verification.dynamics.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyDynamicsData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.dynamics.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying dynamics data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -dynamicsVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyDynamicsData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}
	
	@Scheduled(cron = "${dm.scheduler.data_verification.stackadapt.cronexpression}")
	@Retryable(value = { Exception.class }, maxAttempts = 5, backoff = @Backoff(delay = 10000))
	public void verifyStackAdaptData() throws Exception {

		Boolean runScheduler = shouldEnableSchedular("dm.scheduler.stackadapt.enabled");
		if (runScheduler) {
			logger.info("Scheduler Triggered for verifying stackadapt data {}", dateFormat.format(new Date()));

			Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			endDate = DateUtil.addDaysToDate(endDate, -daysFrom);
			Date startDate = DateUtil.addDaysToDate(endDate, -stackAdaptVerificationDaysFrom);

			CompletableFuture<Boolean> result = this.syncService.verifyStackAdaptData(startDate, endDate);

			logger.info("Task completed. Success status: {}", result.get());
		}
	}
	
	private Boolean shouldEnableSchedular(String propertyName) {
		return env.getProperty(propertyName, Boolean.class, false);
	}
}
