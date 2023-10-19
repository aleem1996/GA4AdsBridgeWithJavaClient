package com.gr.dm.core.adapter.googleads;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v12.services.GoogleAdsRow;
import com.google.ads.googleads.v12.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v12.services.SearchGoogleAdsStreamRequest;
import com.google.ads.googleads.v12.services.SearchGoogleAdsStreamResponse;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.gax.rpc.ServerStream;
import com.gr.dm.core.adapter.Adapter;
import com.gr.dm.core.entity.Ad;
import com.gr.dm.core.entity.AdDetail;
import com.gr.dm.core.entity.AdGroup;
import com.gr.dm.core.entity.AdGroupDetail;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.Keyword;
import com.gr.dm.core.entity.KeywordDetail;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
@Service
public class GoogleAdsAdapter implements Adapter {

	@Value("${dm.googleads.clientCustomerId}")
	private String clientCustomerId;

	@Autowired
	private GoogleAdsAdapterSettings settings;

	public void loadCampaignData(List<Campaign> campaignsList, List<CampaignDetail> campaignDetailList, String startDate, String endDate)
			throws OAuthException, IOException, ValidationException, ConfigurationLoadException {
		GoogleAdsServiceClient googleAdsService = GoogleAdServiceClient.getGoogleAdsService(settings);
		String query = "Select " + Constants.GOOGLEADS_CAMPAIGN_REPORT_COLUMNS + " from campaign where segments.date >= '" + startDate + "' AND segments.date <= '" + endDate + "'";
		SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder().setCustomerId(clientCustomerId).setQuery(query).build();
		ServerStream<SearchGoogleAdsStreamResponse> stream = googleAdsService.searchStreamCallable().call(request);
		for (SearchGoogleAdsStreamResponse response : stream) {
			for (GoogleAdsRow googleAdsRow : response.getResultsList()) {
				String campaignName = googleAdsRow.getCampaign().getName();
				String campaignId = String.valueOf(googleAdsRow.getCampaign().getId());
				String campaignStatus = googleAdsRow.getCampaign().getStatus().toString();
				Integer conversions = Double.valueOf(googleAdsRow.getMetrics().getConversions()).intValue();
				String conversionsValue = Double.toString(googleAdsRow.getMetrics().getConversionsValue());
				String cost = Long.toString(googleAdsRow.getMetrics().getCostMicros());
				Integer impressions = Math.toIntExact(googleAdsRow.getMetrics().getImpressions());
				Integer clicks = Math.toIntExact(googleAdsRow.getMetrics().getClicks());
				GoogleAdsMapper.mapCampaignData(campaignName, campaignId, campaignStatus, conversions, conversionsValue, cost, impressions, clicks, startDate, endDate,
						campaignsList, campaignDetailList, CampaignSource.Google, CampaignDetailSource.Adwords);

			}
		}

	}

	public void loadAdGroupData(List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList, String startDate, String endDate)
			throws OAuthException, IOException, ValidationException, ConfigurationLoadException {
		GoogleAdsServiceClient googleAdsService = GoogleAdServiceClient.getGoogleAdsService(settings);
		String query = "Select " + Constants.GOOGLEADS_ADGROUP_REPORT_COLUMNS + " from ad_group where segments.date >= '" + startDate + "' AND segments.date <= '" + endDate + "'";
		SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder().setCustomerId(clientCustomerId).setQuery(query).build();
		ServerStream<SearchGoogleAdsStreamResponse> stream = googleAdsService.searchStreamCallable().call(request);
		for (SearchGoogleAdsStreamResponse response : stream) {
			for (GoogleAdsRow googleAdsRow : response.getResultsList()) {

				String campaignId = String.valueOf(googleAdsRow.getCampaign().getId());
				String adGroupId = String.valueOf(googleAdsRow.getAdGroup().getId());
				String adGroupName = googleAdsRow.getAdGroup().getName();
				Integer clicks = Math.toIntExact(googleAdsRow.getMetrics().getClicks());
				String cost = Long.toString(googleAdsRow.getMetrics().getCostMicros());
				Integer impressions = Math.toIntExact(googleAdsRow.getMetrics().getImpressions());
				String conversionsValue = Double.toString(googleAdsRow.getMetrics().getConversionsValue());
				Integer conversions = Double.valueOf(googleAdsRow.getMetrics().getConversions()).intValue();
				GoogleAdsMapper.mapAdGroupData(campaignId, adGroupId, adGroupName, clicks, cost, impressions, conversions, conversionsValue, startDate, endDate, adGroupList,
						adGroupDetailList, CampaignSource.Google);

			}
		}
	}

	public void loadAdData(List<Ad> adList, List<AdDetail> adDetailList, String startDate, String endDate)
			throws OAuthException, IOException, ValidationException, ConfigurationLoadException {
		GoogleAdsServiceClient googleAdsService = GoogleAdServiceClient.getGoogleAdsService(settings);
		String query = "Select " + Constants.GOOGLEADS_AD_REPORT_COLUMNS + " from ad_group_ad where segments.date >= '" + startDate + "' AND segments.date <= '" + endDate
				+ "' AND ad_group_ad.ad.type IN (" + Constants.GOOGLEADS_ADTYPES + ")";
		SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder().setCustomerId(clientCustomerId).setQuery(query).build();
		ServerStream<SearchGoogleAdsStreamResponse> stream = googleAdsService.searchStreamCallable().call(request);
		for (SearchGoogleAdsStreamResponse response : stream) {
			for (GoogleAdsRow googleAdsRow : response.getResultsList()) {

				String headlinePart1 = googleAdsRow.getAdGroupAd().getAd().getExpandedTextAd().getHeadlinePart1();
				String headlinePart2 = googleAdsRow.getAdGroupAd().getAd().getExpandedTextAd().getHeadlinePart2();
				String adGroupId = String.valueOf(googleAdsRow.getAdGroup().getId());
				String adId = String.valueOf(googleAdsRow.getAdGroupAd().getAd().getId());
				Integer clicks = Math.toIntExact(googleAdsRow.getMetrics().getClicks());
				String cost = Long.toString(googleAdsRow.getMetrics().getCostMicros());
				Integer impressions = Math.toIntExact(googleAdsRow.getMetrics().getImpressions());
				String conversionsValue = Double.toString(googleAdsRow.getMetrics().getConversionsValue());
				Integer conversions = Double.valueOf(googleAdsRow.getMetrics().getConversions()).intValue();
				GoogleAdsMapper.mapAdData(headlinePart1, headlinePart2, adGroupId, adId, clicks, cost, impressions, conversionsValue, conversions, startDate, endDate,
						adList, adDetailList, CampaignSource.Google);
			}
		}

	}

	public void loadKeywordData(List<Keyword> keywordList, List<KeywordDetail> keywordDetailList, String startDate, String endDate) throws Exception {
		GoogleAdsServiceClient googleAdsService = GoogleAdServiceClient.getGoogleAdsService(settings);
		String query = "Select " + Constants.GOOGLEADS_KEYWORD_REPORT_COLUMNS + " from keyword_view where segments.date >= '" + startDate + "' AND segments.date <= '" + endDate
				+ "'";
		SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder().setCustomerId(clientCustomerId).setQuery(query).build();
		ServerStream<SearchGoogleAdsStreamResponse> stream = googleAdsService.searchStreamCallable().call(request);
		for (SearchGoogleAdsStreamResponse response : stream) {
			for (GoogleAdsRow googleAdsRow : response.getResultsList()) {

				String adGroupId = String.valueOf(googleAdsRow.getAdGroup().getId());
				String keywordId = String.valueOf(googleAdsRow.getAdGroupCriterion().getCriterionId());
				String keywordName = googleAdsRow.getAdGroupCriterion().getKeyword().getText();
				Integer clicks = Math.toIntExact(googleAdsRow.getMetrics().getClicks());
				String cost = Long.toString(googleAdsRow.getMetrics().getCostMicros());
				Integer impressions = Math.toIntExact(googleAdsRow.getMetrics().getImpressions());
				String conversionsValue = Double.toString(googleAdsRow.getMetrics().getConversionsValue());
				Integer conversions = Double.valueOf(googleAdsRow.getMetrics().getConversions()).intValue();
				GoogleAdsMapper.mapKeywordData(keywordId, adGroupId, keywordName, clicks, cost, impressions, conversionsValue, conversions, startDate, endDate, keywordList,
						keywordDetailList, CampaignSource.Google);

			}
		}
	}

	public Long getTransactionCount(String startDate, String endDate) throws Exception {

		GoogleAdsServiceClient googleAdsService = GoogleAdServiceClient.getGoogleAdsService(settings);
		String query = "Select campaign.id, metrics.conversions from keyword_view where segments.date >= '" + startDate + "' AND segments.date <= '" + endDate + "'";
		SearchGoogleAdsStreamRequest request = SearchGoogleAdsStreamRequest.newBuilder().setCustomerId(clientCustomerId).setQuery(query).build();
		ServerStream<SearchGoogleAdsStreamResponse> stream = googleAdsService.searchStreamCallable().call(request);
		return GoogleAdsMapper.getTransactionCount(stream);
	}

	static class GoogleAdServiceClient {

		private static GoogleAdsServiceClient googleAdsService = null;

		private GoogleAdServiceClient() {
		}

		static GoogleAdsServiceClient getGoogleAdsService(GoogleAdsAdapterSettings settings) throws IOException, OAuthException, ValidationException, ConfigurationLoadException {

			if (Util.isNull(googleAdsService)) {
				synchronized (GoogleAdsServiceClient.class) {
					if (Util.isNull(googleAdsService)) {
						GoogleAdsClient googleAdsClient = null;
						File configFile = new ClassPathResource(settings.getConfigFileName()).getFile();
						googleAdsClient = GoogleAdsClient.newBuilder().fromPropertiesFile(configFile).build();

						googleAdsService = googleAdsClient.getLatestVersion().createGoogleAdsServiceClient();
					}
				}
			}
			return googleAdsService;
		}
	}

}
