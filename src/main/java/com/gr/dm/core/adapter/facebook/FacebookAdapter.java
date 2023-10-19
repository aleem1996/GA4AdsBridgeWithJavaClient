package com.gr.dm.core.adapter.facebook;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.AdCreative;
import com.facebook.ads.sdk.AdReportRun;
import com.facebook.ads.sdk.AdsInsights;
import com.gr.dm.core.adapter.Adapter;
import com.gr.dm.core.entity.Ad;
import com.gr.dm.core.entity.AdAttribution;
import com.gr.dm.core.entity.AdDetail;
import com.gr.dm.core.entity.AdGroup;
import com.gr.dm.core.entity.AdGroupAttribution;
import com.gr.dm.core.entity.AdGroupDetail;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignAttribution;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.service.AdService;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.Util;

/**
 * @author Aleem Malik
 */
@Service
public class FacebookAdapter implements Adapter {

	public static final Logger logger = Logger.getLogger(FacebookAdapter.class.getName());
	
	@Autowired
	private FacebookAdapterSettings facebookAdapterSettings;
	
	@Autowired
	private AdService adService;
	
	private final FacebookClient facebookClient = new FacebookClient();
	
	public void loadCampaignData(List<Campaign> campaigns, List<CampaignDetail> campaignDetails, List<CampaignAttribution> campaignAttributions, String startDate, String endDate) throws Exception {
		logger.info("LoadCampaignData() method started");
		String[] requestFields = new String[]{Constants.FB_CAMPAIGN_ID, Constants.FB_CAMPAIGN_NAME, Constants.FB_SPEND, Constants.FB_FIELD_ACTIONS,
				Constants.FB_CLICKS, Constants.FB_IMPRESSIONS, Constants.FB_ACTION_VALUES};
		String filter = "";
		APINodeList<AdsInsights> adsInsights = facebookClient.getInsights(AdsInsights.EnumLevel.VALUE_CAMPAIGN, Arrays.asList(requestFields), filter, startDate, endDate);
		FacebookMapper.mapCampaignData(adsInsights, startDate, endDate, campaigns, campaignDetails, campaignAttributions);
		logger.info("LoadCampaignData() method ended");
	}
	
	public void loadAdGroupData(List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList, List<AdGroupAttribution> adGroupAttributions, String startDate, String endDate) throws Exception {
		String[] requestFields = new String[]{Constants.FB_ADSET_ID, Constants.FB_ADSET_NAME, Constants.FB_CAMPAIGN_ID, Constants.FB_SPEND, Constants.FB_FIELD_ACTIONS,
				Constants.FB_CLICKS, Constants.FB_IMPRESSIONS, Constants.FB_ACTION_VALUES};
		String filter = "";
		APINodeList<AdsInsights> adsInsights = facebookClient.getInsights(AdsInsights.EnumLevel.VALUE_ADSET, Arrays.asList(requestFields), filter, startDate, endDate);
		FacebookMapper.mapAdGroupData(adsInsights, startDate, endDate, adGroupList, adGroupDetailList, adGroupAttributions);
	}
	
	public void loadAdData(List<Ad> adList, List<AdDetail> adDetailList, List<AdAttribution> adAttributions, String startDate, String endDate) throws Exception {
		logger.info("LoadAdData() method started");
		String[] requestFields = new String[]{Constants.FB_AD_ID, Constants.FB_AD_NAME, Constants.FB_ADSET_ID, Constants.FB_SPEND, Constants.FB_FIELD_ACTIONS,
				Constants.FB_CLICKS, Constants.FB_IMPRESSIONS, Constants.FB_ACTION_VALUES};
		String filter = "";
		APINodeList<AdsInsights> adsInsights = facebookClient.getInsights(AdsInsights.EnumLevel.VALUE_AD, Arrays.asList(requestFields), filter, startDate, endDate);
		FacebookMapper.mapAdData(adsInsights, startDate, endDate, adList, adDetailList, adAttributions);
		loadAdImage(adList);
		logger.info("LoadAdData() method ended");
	}
	
	public Map<String, Long> getTransactionCount(String startDate, String endDate) throws Exception {
		String[] requestFields = new String[]{ Constants.FB_FIELD_ACTIONS };
		String filter = "";
		APINodeList<AdsInsights> adsInsights = facebookClient.getInsights(AdsInsights.EnumLevel.VALUE_CAMPAIGN, Arrays.asList(requestFields), filter, startDate, endDate);
		return FacebookMapper.getTransactionCount(adsInsights);
	}
	
	private void loadAdImage(List<Ad> adList) throws Exception {
		for (Ad ad : adList) {
			Ad savedAd = adService.getAd(ad.getAdGroupId(), ad.getAdId());
			if(Util.isNull(savedAd) || Util.isNull(savedAd.getImageUrl())) {
				String[] fields = new String[] { Constants.FB_IMAGE_URL };
				APINodeList<AdCreative> adCreatives = facebookClient.getAdCreativesByAdId(ad.getAdId(),
						Arrays.asList(fields));
				if (!Util.isNull(adCreatives) && adCreatives.size() > 0) {
					ad.setImageUrl(adCreatives.get(0).getFieldImageUrl());
				}
				Thread.sleep(5000);
			}
		}
	}
	
	private class FacebookClient {
		
		private APINodeList<AdsInsights> getInsights(AdsInsights.EnumLevel enumLevel, List<String> requestFields, String filter, String startDate, String endDate) throws APIException {
			APINodeList<AdsInsights> adsInsights = new AdAccount(facebookAdapterSettings.getAdAccountId(), getContext()).getInsights()
					   .setLevel(enumLevel)
					   .setFiltering(filter)
					   .setTimeRange("{\"since\":\"" + startDate + "\",\"until\":\"" + endDate + "\"}")
					   .requestFields(requestFields)
					   .setParam(Constants.FB_ACTION_ATTRIBUTION, Constants.FB_ACTION_ATTRIBUTION_VALUES)
					   .execute();
			return adsInsights;
		}
		
		private APINodeList<AdsInsights> getInsightsAsync(AdsInsights.EnumLevel enumLevel, List<String> requestFields, String filter, String startDate, String endDate) throws APIException {
			AdReportRun adReportRun = new AdAccount(facebookAdapterSettings.getAdAccountId(), getContext()).getInsightsAsync()
					   .setLevel(enumLevel)
					   .setFiltering(filter)
					   .setTimeRange("{\"since\":\"" + startDate + "\",\"until\":\"" + endDate + "\"}")
					   .requestFields(requestFields)
					   .setParam(Constants.FB_ACTION_ATTRIBUTION, Constants.FB_ACTION_ATTRIBUTION_VALUES)
					   .execute();
			
			adReportRun = adReportRun.fetch();
			int polling = 0;
			Long percentage = adReportRun.getFieldAsyncPercentCompletion() == null ? 0 : adReportRun.getFieldAsyncPercentCompletion();
			logger.info("StartDate: " + startDate + " , EndDate: " + endDate);
			while (!"Job Completed".equalsIgnoreCase(adReportRun.getFieldAsyncStatus()) && polling < 300) {
				logger.info("Facebook data polling: " + polling + " seconds. Job Status: " + adReportRun.getFieldAsyncStatus() + " and PercentCompletion " + percentage + "%");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if ("Job Failed".equalsIgnoreCase(adReportRun.getFieldAsyncStatus())) {
					logger.info("Facebook data fetch Failed. Job Status: " + adReportRun.getFieldAsyncStatus());
					break;
				}
				
				adReportRun = adReportRun.fetch();
				percentage = adReportRun.getFieldAsyncPercentCompletion() == null ? 0 : adReportRun.getFieldAsyncPercentCompletion();
				polling ++;
			}
			
			logger.info("Facebook data fetch status: " + adReportRun.getFieldAsyncStatus());
			return adReportRun.getInsights().execute();
		}
		
		private APINodeList<AdCreative> getAdCreativesByAdId(String adId, List<String> fields) throws APIException {
			return new com.facebook.ads.sdk.Ad(adId, getContext())
					.getAdCreatives()
					.requestFields(fields)
					.execute();
		}
		
		private APIContext getContext() {
			return new APIContext(facebookAdapterSettings.getAccessToken());
		}
	}
}
