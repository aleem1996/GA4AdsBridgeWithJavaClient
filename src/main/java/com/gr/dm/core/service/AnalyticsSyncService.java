package com.gr.dm.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.SearchUserActivityResponse;
import com.gr.dm.core.adapter.AdapterSettingsBase;
import com.gr.dm.core.adapter.adwords.AdWordsAdapter;
import com.gr.dm.core.adapter.bing.BingAdapter;
import com.gr.dm.core.adapter.facebook.FacebookAdapter;
import com.gr.dm.core.adapter.googleads.GoogleAdsAdapter;
import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsAdapter;
import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsMapper;
import com.gr.dm.core.adapter.googleanalytics4.GoogleAnalytics4Adapter;
import com.gr.dm.core.adapter.gws.CrmAdapter;
import com.gr.dm.core.adapter.stackadapt.StackAdaptAdapter;
import com.gr.dm.core.dto.BingCampaignStatsDto;
import com.gr.dm.core.dto.CampaignLpStatsRequest;
import com.gr.dm.core.dto.FbCampaignStatsDto;
import com.gr.dm.core.dto.LandingPageStatsDto;
import com.gr.dm.core.dto.StatsDto;
import com.gr.dm.core.dto.crm.CrmTransactionCount;
import com.gr.dm.core.entity.Ad;
import com.gr.dm.core.entity.AdAttribution;
import com.gr.dm.core.entity.AdDetail;
import com.gr.dm.core.entity.AdGroup;
import com.gr.dm.core.entity.AdGroupAttribution;
import com.gr.dm.core.entity.AdGroupDetail;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignAttribution;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.Keyword;
import com.gr.dm.core.entity.KeywordDetail;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

/**
 * @author ufarooq
 */
@Service
public class AnalyticsSyncService {

	public static final Logger logger = Logger.getLogger(AdapterSettingsBase.class.getName());
	
	private final SimpleDateFormat analyticsDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	GoogleAnalyticsAdapter gaAdapter;
	
	@Autowired
	GoogleAnalytics4Adapter ga4Adapter;
	
	@Autowired
	CrmAdapter crmAdapter;
	
	@Autowired
	FacebookAdapter fbAdapter;
	
	@Autowired
	AdWordsAdapter adwordsAdapter;
	
	@Autowired
	GoogleAdsAdapter googleAdsAdapter;
	
	@Autowired
	BingAdapter bingAdapter;
	
	@Autowired
	StackAdaptAdapter stackAdaptAdapter;
	
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	NotificationService notificationService;
	
	@Autowired
	AdGroupService adGroupService;
	
	@Autowired
	AdService adService;
	
	@Autowired
	KeywordService keywordService;
	
	@Autowired
	CacheService cacheService;

	@Async
	public CompletableFuture<Boolean> loadYesterdayData() {
		String startDate = DateUtil.getYesterdayDate();
		String endDate = DateUtil.getYesterdayDate();
		return this.loadDataBetween(DateUtil.getDate(startDate), DateUtil.getDate(endDate));
	}
	
	@Async
	public CompletableFuture<Boolean> loadCompleteData() {
		this.clearData();

		String startDateString = "2017-12-26";
		Date startDate = DateUtil.getDate(startDateString);
		Date endDate = DateUtil.getDate(DateUtil.getYesterdayDate());
		return loadDataBetween(startDate, endDate);
	}
	
	@Async
	public CompletableFuture<Boolean> loadDataBetween(Date startDate, Date endDate) {
		
		try {
			Calendar comparisonCalendar = Calendar.getInstance();
	        comparisonCalendar.set(2023, Calendar.MAY, 04);
	        Date customDate = comparisonCalendar.getTime();
	        
//	        if (endDate.before(customDate) || endDate.equals(customDate)) {
//				loadAnalyticsData(startDate, endDate);
//	        } else if (startDate.after(customDate)) {
//	        	loadGA4Data(startDate, endDate);
//	        } else {
//				Calendar ga4Calendar = Calendar.getInstance();
//				ga4Calendar.set(2023, Calendar.JULY, 01);
//		        Date ga4StartDate = comparisonCalendar.getTime();
//				loadAnalyticsData(startDate, customDate);
//	        	loadGA4Data(ga4StartDate, endDate);
//	        }
			
			loadAnalyticsData(startDate, endDate);
			//this check is added since we have GA4 data since may05,2023
			if(startDate.after(customDate) && endDate.after(customDate)) {
	        	loadGA4Data(startDate, endDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			loadFbData(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			loadGoogleAdsData(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			loadBingData(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			loadDynamicsData(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			loadStackAdaptData(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("Loading data completed.");
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public void loadDataBetween(Date startDate, Date endDate, CampaignSource campaignSource)
			throws Exception {
		
		if (CampaignSource.Google.equals(campaignSource)) {
			Calendar comparisonCalendar = Calendar.getInstance();
	        comparisonCalendar.set(2023, Calendar.MAY, 04);
	        Date customDate = comparisonCalendar.getTime();
//	        if (endDate.before(customEndDate) || endDate.equals(customEndDate)) {
//				loadAnalyticsData(startDate, endDate);
//	        } else if (startDate.after(customEndDate)) {
//	        	loadGA4Data(startDate, endDate);
//	        } else {
//				Calendar ga4Calendar = Calendar.getInstance();
//				ga4Calendar.set(2023, Calendar.JULY, 01);
//		        Date customStartDate = comparisonCalendar.getTime();
//				loadAnalyticsData(startDate, customEndDate);
//	        	loadGA4Data(customStartDate, endDate);
//	        }
	        loadAnalyticsData(startDate, endDate);
	        
	      //this check is added since we have GA4 data since may05,2023
			if(startDate.after(customDate) && endDate.after(customDate)) {
	        	loadGA4Data(startDate, endDate);
			}
			
			loadGoogleAdsData(startDate, endDate);
		} else if (CampaignSource.Facebook.equals(campaignSource)) {
			loadFbData(startDate, endDate);
		} else if (CampaignSource.Bing.equals(campaignSource)) {
			loadBingData(startDate, endDate);
		} else if (CampaignSource.Email.equals(campaignSource)) {
			loadDynamicsData(startDate, endDate);
		} else if (CampaignSource.StackAdapt.equals(campaignSource)) {
			loadStackAdaptData(startDate, endDate);
		}
		
	}
	
	@Async
	public CompletableFuture<Boolean> loadStackAdaptData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			while (startDate.compareTo(endDate) <= 0) {
				List<Campaign> campaigns = new ArrayList<Campaign>();
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
				stackAdaptAdapter.loadAllStackAdaptCampaigns(campaigns, campaignDetails, startDateString, endDateString);

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);
				
				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			cacheService.clearCache();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> loadAnalyticsData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			while (startDate.compareTo(endDate) <= 0) {
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
				List<Campaign> campaigns = new ArrayList<Campaign>();
				gaAdapter.loadCampaignData(campaigns, campaignDetails, startDateString, endDateString);

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);
				
				List<CampaignTransaction> campaignTransactions = new ArrayList<CampaignTransaction>();
				List<TransactionDetail> transactionDetails = new ArrayList<TransactionDetail>();
				gaAdapter.loadAssistedTransactionsData(campaignTransactions, transactionDetails, startDateString, endDateString);
				
				transactionService.saveTransaction(campaignTransactions);
				transactionService.saveTransactionDetail(transactionDetails);

				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			
			campaignService.updateAssistedConversionsData();
			cacheService.clearCache();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);

	}
	
	@Async
	public CompletableFuture<Boolean> loadGA4Data(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			while (startDate.compareTo(endDate) <= 0) {
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
				List<Campaign> campaigns = new ArrayList<Campaign>();
				ga4Adapter.loadCampaignData(campaigns, campaignDetails, startDateString, endDateString);

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);
				
				List<CampaignTransaction> campaignTransactions = new ArrayList<CampaignTransaction>();
				List<TransactionDetail> transactionDetails = new ArrayList<TransactionDetail>();
				ga4Adapter.loadAssistedTransactionsData(campaignTransactions, transactionDetails, startDateString, endDateString);

				
				transactionService.saveTransaction(campaignTransactions);
				transactionService.saveTransactionDetail(transactionDetails);

				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			
			campaignService.updateAssistedConversionsData();
			cacheService.clearCache();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);

	}
	
	@Async
	public CompletableFuture<Boolean> loadDynamicsData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			
			while (startDate.compareTo(endDate) <= 0) {
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
				List<Campaign> campaigns = new ArrayList<Campaign>();
				crmAdapter.loadAllCampaigns(startDateString, endDateString, campaigns, campaignDetails);

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);

				List<CampaignTransaction> campaignTransactions = new ArrayList<CampaignTransaction>();
				List<TransactionDetail> transactionDetails = new ArrayList<TransactionDetail>();
				List<CampaignAttribution> campaignAttributions = new ArrayList<CampaignAttribution>();

				campaignDetails = new ArrayList<CampaignDetail>();
				crmAdapter.loadAllTransactions(startDateString, endDateString, campaignTransactions, transactionDetails, campaignAttributions, campaignDetails);
				
				for (CampaignDetail campaignDetail : campaignDetails) {
					if (!CampaignDetailSource.Email.equals(campaignDetail.getCampaignDetailSource())) {
						campaignService.saveGrCampaignDetail(campaignDetail);
					}
				}
				
				transactionService.saveTransaction(campaignTransactions);
				transactionService.saveTransactionDetail(transactionDetails);
				
				campaignService.saveCampaignAttribution(campaignAttributions);

				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			cacheService.clearCache();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);

	}
	
	@Async
	public CompletableFuture<Boolean> loadFbData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			while (startDate.compareTo(endDate) <= 0) {
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
				List<Campaign> campaigns = new ArrayList<Campaign>();
				List<CampaignAttribution> campaignAttributions = new ArrayList<CampaignAttribution>();
				fbAdapter.loadCampaignData(campaigns, campaignDetails, campaignAttributions, "2023-09-12", "2023-09-12");

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);
				campaignService.saveCampaignAttribution(campaignAttributions);

				List<AdGroup> adGroupList = new ArrayList<AdGroup>();
				List<AdGroupDetail> adGroupDetailList = new ArrayList<AdGroupDetail>();
				List<AdGroupAttribution> adGroupAttributions = new ArrayList<AdGroupAttribution>();
				fbAdapter.loadAdGroupData(adGroupList, adGroupDetailList, adGroupAttributions, "2023-09-12", "2023-09-12");

				adGroupService.saveAdGroup(adGroupList);
				adGroupService.saveAdGroupDetail(adGroupDetailList);
				adGroupService.saveAdGroupAttribution(adGroupAttributions);

				List<Ad> adList = new ArrayList<Ad>();
				List<AdDetail> adDetailList = new ArrayList<AdDetail>();
				List<AdAttribution> adAttributions = new ArrayList<AdAttribution>();
				fbAdapter.loadAdData(adList, adDetailList, adAttributions, "2023-09-12", "2023-09-12");

				adService.saveAd(adList);
				adService.saveAdDetail(adDetailList);
				adService.saveAdAttribution(adAttributions);

				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			cacheService.clearCache();
		} catch (Exception ex) {
			ex.getCause().getCause().getMessage();
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			if (Util.isNotNull(ex.getCause()) && Util.isNotNull(ex.getCause().getCause()) && Util.isNotNull(ex.getCause().getCause().getMessage())) {
				logger.log(Level.SEVERE, ex.getCause().getCause().getMessage(), ex.getCause());
			}
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);

	}
	
	@Async
	public CompletableFuture<Boolean> loadGoogleAdsData(Date startDate, Date endDate) throws Exception {
		
		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			while (startDate.compareTo(endDate) <= 0) {
				List<Campaign> campaigns = new ArrayList<Campaign>();
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
//				adwordsAdapter.loadCampaignData(campaigns, campaignDetails, startDateString, endDateString);
				googleAdsAdapter.loadCampaignData(campaigns, campaignDetails, startDateString, endDateString);

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);

				List<AdGroup> adGroupList = new ArrayList<AdGroup>();
				List<AdGroupDetail> adGroupDetailList = new ArrayList<AdGroupDetail>();
//				adwordsAdapter.loadAdGroupData(adGroupList, adGroupDetailList, startDateString, endDateString);
				googleAdsAdapter.loadAdGroupData(adGroupList, adGroupDetailList, startDateString, endDateString);

				adGroupService.saveAdGroup(adGroupList);
				adGroupService.saveAdGroupDetail(adGroupDetailList);

				List<Ad> adList = new ArrayList<Ad>();
				List<AdDetail> adDetailList = new ArrayList<AdDetail>();
//				adwordsAdapter.loadAdData(adList, adDetailList, startDateString, endDateString);
				googleAdsAdapter.loadAdData(adList, adDetailList, startDateString, endDateString);

				adService.saveAd(adList);
				adService.saveAdDetail(adDetailList);

				List<Keyword> keywordList = new ArrayList<Keyword>();
				List<KeywordDetail> keywordDetailList = new ArrayList<KeywordDetail>();
//				adwordsAdapter.loadKeywordData(keywordList, keywordDetailList, startDateString, endDateString);
				googleAdsAdapter.loadKeywordData(keywordList, keywordDetailList, startDateString, endDateString);

				
				keywordService.saveKeyword(keywordList);
				keywordService.saveKeywordDetail(keywordDetailList);
				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			cacheService.clearCache();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> loadBingData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(startDate);
			while (startDate.compareTo(endDate) <= 0) {
				List<Campaign> campaigns = new ArrayList<Campaign>();
				List<CampaignDetail> campaignDetails = new ArrayList<CampaignDetail>();
				bingAdapter.loadCampaignData(campaigns, campaignDetails, startDateString, endDateString);

				campaignService.saveCampaign(campaigns);
				campaignService.saveCampaignDetail(campaignDetails);

				List<AdGroup> adGroupList = new ArrayList<AdGroup>();
				List<AdGroupDetail> adGroupDetailList = new ArrayList<AdGroupDetail>();
				bingAdapter.loadAdGroupData(adGroupList, adGroupDetailList, startDateString, endDateString);

				adGroupService.saveAdGroup(adGroupList);
				adGroupService.saveAdGroupDetail(adGroupDetailList);

				List<Ad> adList = new ArrayList<Ad>();
				List<AdDetail> adDetailList = new ArrayList<AdDetail>();
				bingAdapter.loadAdData(adList, adDetailList, startDateString, endDateString);

				adService.saveAd(adList);
				adService.saveAdDetail(adDetailList);

				List<Keyword> keywordList = new ArrayList<Keyword>();
				List<KeywordDetail> keywordDetailList = new ArrayList<KeywordDetail>();
				bingAdapter.loadKeywordData(keywordList, keywordDetailList, startDateString, endDateString);

				keywordService.saveKeyword(keywordList);
				keywordService.saveKeywordDetail(keywordDetailList);
				
				startDate = DateUtil.addDaysToDate(startDate, 1);
				startDateString = endDateString = analyticsDateFormat.format(startDate);
			}
			cacheService.clearCache();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	/**
	 * 1. Load data between given date range.
	 * 2. Verify the loaded data from database.
	 * 3. If transaction count does not match, get the mid date between start and end date.
	 * 4. Recursively call this function for date ranges: Start - Mid, Mid + 1 - End.
	 * 5. Stop if start and end date are same.
	 */
	@Async
	public CompletableFuture<Boolean> verifyGoogleAdsData(Date startDate, Date endDate) throws Exception {
		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);

			StatsDto stats = campaignService.getGoogleCampaignStats(startDate, endDate);
			if (!Util.isNull(stats)) {
				Long transactionCount = googleAdsAdapter.getTransactionCount(startDateString, endDateString);
				if (!transactionCount.equals(stats.getAdWordsConversions())) {
					if (startDateString.equals(endDateString)) {
						// TODO: send an email that data mismatched for this particular date
						return loadGoogleAdsData(startDate, endDate); 
					}
					Date midDate = DateUtil.getMidDate(startDate, endDate);
					Date nextDate = DateUtil.addDaysToDate(midDate, 1);
					if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
						verifyGoogleAdsData(startDate, midDate);
						verifyGoogleAdsData(nextDate, endDate);
					} else {
						verifyGoogleAdsData(startDate, startDate);
						verifyGoogleAdsData(endDate, endDate);
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> verifyAnalyticsData(Date startDate, Date endDate) throws Exception {
		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);

			StatsDto stats = campaignService.getGoogleCampaignStats(startDate, endDate);
			if (!Util.isNull(stats)) {
				Long transactionCount = gaAdapter.getTransactionCount(startDateString, endDateString);
				if (!transactionCount.equals(stats.getGaConversions())) {
					if (startDateString.equals(endDateString)) {
						// TODO: send an email that data mismatched for this particular date
						return loadAnalyticsData(startDate, endDate); 
					}
					Date midDate = DateUtil.getMidDate(startDate, endDate);
					Date nextDate = DateUtil.addDaysToDate(midDate, 1);
					if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
						verifyAnalyticsData(startDate, midDate);
						verifyAnalyticsData(nextDate, endDate);
					} else {
						verifyAnalyticsData(startDate, startDate);
						verifyAnalyticsData(endDate, endDate);
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> verifyGA4Data(Date startDate, Date endDate) throws Exception {
		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);

			StatsDto stats = campaignService.getGoogleCampaignStats(startDate, endDate);
			if (!Util.isNull(stats)) {
				Long transactionCount = ga4Adapter.getTransactionCount(startDateString, endDateString);
				if (!transactionCount.equals(stats.getGaConversions())) {
					if (startDateString.equals(endDateString)) {
						// TODO: send an email that data mismatched for this particular date
						return loadGA4Data(startDate, endDate); 
					}
					Date midDate = DateUtil.getMidDate(startDate, endDate);
					Date nextDate = DateUtil.addDaysToDate(midDate, 1);
					if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
						verifyGA4Data(startDate, midDate);
						verifyGA4Data(nextDate, endDate);
					} else {
						verifyGA4Data(startDate, startDate);
						verifyGA4Data(endDate, endDate);
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> verifyFacebookData(Date startDate, Date endDate) throws Exception {
		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);
			FbCampaignStatsDto stats = campaignService.getFacebookCampaignStats(startDate, endDate, CampaignSource.Facebook);
			if (!Util.isNull(stats)) {
				Map<String, Long> transactionMap = fbAdapter.getTransactionCount(startDateString, endDateString);
				
				if (!transactionMap.get(Constants.FB_PURCHASES).equals(stats.getConversions())) {
					if (startDateString.equals(endDateString)) {
						// TODO: send an email that data mismatched for this particular date
						return loadFbData(startDate, endDate);
					}
					
					Date midDate = DateUtil.getMidDate(startDate, endDate);
					Date nextDate = DateUtil.addDaysToDate(midDate, 1);
					Thread.sleep(5000);
					
					if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
						verifyFacebookData(startDate, midDate);
						verifyFacebookData(nextDate, endDate);
					} else {
						verifyFacebookData(startDate, startDate);
						verifyFacebookData(endDate, endDate);
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> verifyBingData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);

			BingCampaignStatsDto stats = campaignService.getBingCampaignStats(startDate, endDate);
			if (!Util.isNull(stats)) {
				Long transactionCount = bingAdapter.getTransactionCount(startDateString, endDateString);
				if (!transactionCount.equals(stats.getConversions()) && !transactionCount.equals(0L)) {
					if (startDateString.equals(endDateString)) {
						// TODO: send an email that data mismatched for this particular date
						return loadBingData(startDate, endDate);
					}
					Date midDate = DateUtil.getMidDate(startDate, endDate);
					Date nextDate = DateUtil.addDaysToDate(midDate, 1);
					if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
						verifyBingData(startDate, midDate);
						verifyBingData(nextDate, endDate);
					} else {
						verifyBingData(startDate, startDate);
						verifyBingData(endDate, endDate);
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> verifyDynamicsData(Date startDate, Date endDate) throws Exception {
		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);

			FbCampaignStatsDto stats = campaignService.getFacebookCampaignStats(startDate, endDate, CampaignSource.Email);
			if (!Util.isNull(stats)) {
				CrmTransactionCount crmTransactionCount = crmAdapter.loadTransactionCount(startDateString, endDateString);
				Long transactionCount = Long.valueOf(crmTransactionCount.getTransactionCount());
				if (!transactionCount.equals(stats.getConversions())) {
					if (startDateString.equals(endDateString)) {
						// TODO: send an email that data mismatched for this particular date
						return loadDynamicsData(startDate, endDate); 
					}
					Date midDate = DateUtil.getMidDate(startDate, endDate);
					Date nextDate = DateUtil.addDaysToDate(midDate, 1);
					if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
						verifyDynamicsData(startDate, midDate);
						verifyDynamicsData(nextDate, endDate);
					} else {
						verifyDynamicsData(startDate, startDate);
						verifyDynamicsData(endDate, endDate);
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	@Async
	public CompletableFuture<Boolean> verifyStackAdaptData(Date startDate, Date endDate) throws Exception {

		try {
			String startDateString = analyticsDateFormat.format(startDate);
			String endDateString = analyticsDateFormat.format(endDate);

			BingCampaignStatsDto stats = campaignService.getStackAdaptCampaignStats(startDate, endDate);
			Long transactionCount = stackAdaptAdapter.getStackAdaptTransactionCount(startDateString, endDateString);
			verifyStackAdaptData(stats, transactionCount, startDate, startDate);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
			notificationService.sendExceptionEmailNotification(ex);
			throw ex;
		}

		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	private CompletableFuture<Boolean> verifyStackAdaptData(BingCampaignStatsDto stackAdaptstats, Long transactionCount, Date startDate, Date endDate) throws Exception {
		
		String startDateString = analyticsDateFormat.format(startDate);
		String endDateString = analyticsDateFormat.format(endDate);
		
		if (!Util.isNull(stackAdaptstats)) {
			if (!transactionCount.equals(stackAdaptstats.getConversions()) && !transactionCount.equals(0L)) {
				if (startDateString.equals(endDateString)) {
					// TODO: send an email that data mismatched for this particular date
					return loadStackAdaptData(startDate, endDate);
				}
				Date midDate = DateUtil.getMidDate(startDate, endDate);
				Date nextDate = DateUtil.addDaysToDate(midDate, 1);
				if (DateTimeComparator.getDateOnlyInstance().compare(midDate, endDate) < 0) {
					verifyStackAdaptData(stackAdaptstats, transactionCount, startDate, midDate);
					verifyStackAdaptData(stackAdaptstats, transactionCount, nextDate, endDate);
				} else {
					verifyStackAdaptData(stackAdaptstats, transactionCount, startDate, startDate);
					verifyStackAdaptData(stackAdaptstats, transactionCount, endDate, endDate);
				}
			}
	}
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}
	
	// TODO: This function is for testing purpose
	public Boolean clearData() {
		
		campaignService.deleteCampaignsData();
		transactionService.deleteTransactionsData();
		return Boolean.TRUE;
	}
	public List<LandingPageStatsDto> getCampaignLandingPageStats(Date startDate, Date endDate, List<CampaignLpStatsRequest> campaignLpStatsRequestList) throws Exception{
		final List<LandingPageStatsDto> landingPageStatsDtoList = new ArrayList<>();
		campaignLpStatsRequestList.stream().parallel().forEach((campaignLpStatsRequest)->{
			try {	
				landingPageStatsDtoList.addAll(getLandingPageStats(startDate, endDate, campaignLpStatsRequest.getCampaign(), campaignLpStatsRequest.getLandingPages()));
			} catch (Exception e) {
				e.printStackTrace();
				campaignLpStatsRequestList.add(null);
			}
		});
		return landingPageStatsDtoList;
	}
	public List<LandingPageStatsDto> getLandingPageStats(Date startDate, Date endDate, String campaign, List<String> paths) throws Exception{
		final List<LandingPageStatsDto> landingPageStatsDtoList = new ArrayList<>();
		paths.stream().parallel().forEach((path)->{
				try {
					landingPageStatsDtoList.add(this.getLandingPageStats(startDate, endDate, campaign, path));
				} catch (Exception e) {
					e.printStackTrace();
					landingPageStatsDtoList.add(null);
				}
		});
		return landingPageStatsDtoList;
	}
	
	private LandingPageStatsDto getLandingPageStats(Date startDate, Date endDate, String campaign, String path) throws Exception{
		String filtersExpression = Constants.GA_PAGE_PATH + "=@" + path;
		if(!Util.isNullOrEmpty(campaign)) {
			filtersExpression +=";" + Constants.GA_CAMPAIGN + "=@" + campaign;
		}
		GetReportsResponse metricsTotalResponse = gaAdapter.getCustomFilteredData(analyticsDateFormat.format(startDate), analyticsDateFormat.format(endDate), new String[] {Constants.GA_SESSIONS, Constants.GA_PERCENT_NEW_SESSIONS, Constants.GA_NEW_USERS, Constants.GA_BOUNCE_RATE, Constants.GA_AVG_SESSION_DURATION, Constants.GA_TRANSACTIONS, Constants.GA_TRANSACTION_REVENUE, Constants.GA_PAGE_VIEWS, Constants.GA_UNIQUE_PAGE_VIEWS, Constants.GA_AVG_TIME_ON_PAGE}, new String[] {}, filtersExpression);
		GetReportsResponse eventsResponse = gaAdapter.getCustomFilteredData(analyticsDateFormat.format(startDate), analyticsDateFormat.format(endDate), new String[] {Constants.GA_TOTAL_EVENTS}, new String[] {Constants.GA_EVENT_CATEGORY, Constants.GA_EVENT_ACTION, Constants.GA_EVENT_LABEL}, filtersExpression);
		LandingPageStatsDto response = new LandingPageStatsDto(path, campaign, GoogleAnalyticsMapper.transformReportResponseTotalsToMap(metricsTotalResponse),
				GoogleAnalyticsMapper.transformReportResponseToEvents(eventsResponse));
		return response;
	}
	
	public SearchUserActivityResponse getUserActivityReport(String userId, Optional<Date> startDate, Optional<Date> endDate) throws Exception{
		
		String startDateStr = "2020-01-01";
		String endDateStr = analyticsDateFormat.format(new Date());
		if(startDate.isPresent()) {
			startDateStr = analyticsDateFormat.format(startDate.get());
		}
		
		if(endDate.isPresent()) {
			endDateStr = analyticsDateFormat.format(endDate.get());
		}
		return gaAdapter.getUserActivity(userId, startDateStr, endDateStr);
	}
	
}
