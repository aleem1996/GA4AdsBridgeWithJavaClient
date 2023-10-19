package com.gr.dm.core.adapter.googleads;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.google.ads.googleads.v12.services.GoogleAdsRow;
import com.google.ads.googleads.v12.services.SearchGoogleAdsStreamResponse;
import com.google.api.gax.rpc.ServerStream;
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
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */

public class GoogleAdsMapper {

	public static void mapCampaignData(String campaignName, String campaignId, String campaignStatus, Integer conversions, String conversionsValue, String cost,
			Integer impressions, Integer clicks, String startDate, String endDate, List<Campaign> campaignList, List<CampaignDetail> campaignDetailList,
			CampaignSource campaignSource, CampaignDetailSource campaignDetailSource) throws IOException {

		Campaign campaign = new Campaign();
		campaign.setName(campaignName);
		campaign.setCampaignId(campaignId);
		campaign.setCampaignSource(campaignSource);
		String status = campaignStatus;
		boolean active = false;
		if (!Util.isNullOrEmpty(status) && status.toLowerCase().equals("enabled") || status.toLowerCase().equals("active")) {
			active = true;
		}
		campaign.setActive(active);
		campaignList.add(campaign);

		CampaignDetail campaignDetail = new CampaignDetail();
		campaignDetail.setIsDataSynced(Boolean.TRUE);
		campaignDetail.setLastUpdated(new Date());
		campaignDetail.setCampaignId(campaign.getCampaignId());
		campaignDetail.setTransactionCount(conversions);
		conversionsValue = conversionsValue.replaceAll(",", "");
		campaignDetail.setRevenue(Double.valueOf(conversionsValue));
		campaignDetail.setCost(getCost(cost, campaignSource));
		campaignDetail.setImpressions(impressions);
		campaignDetail.setClicks(clicks);

		campaignDetail.setStartDate(DateUtil.getDate(startDate));
		campaignDetail.setEndDate(DateUtil.getDate(endDate));
		campaignDetail.setCampaignDetailSource(campaignDetailSource);
		campaignDetailList.add(campaignDetail);
	}

	public static void mapAdGroupData(String campaignId, String adGroupId, String adGroupName, Integer clicks, String cost, Integer impressions, Integer conversions,
			String conversionsValue, String startDate, String endDate, List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList, CampaignSource campaignSource)
			throws IOException {

		AdGroup adGroup = new AdGroup();
		adGroup.setAdGroupSource(campaignSource);
		adGroup.setCampaignId(campaignId);
		adGroup.setAdGroupId(adGroupId);
		adGroup.setName(adGroupName);
		adGroupList.add(adGroup);

		AdGroupDetail adGroupDetail = new AdGroupDetail();
		adGroupDetail.setAdGroupId(adGroupId);
		adGroupDetail.setClicks(clicks);
		adGroupDetail.setCost(getCost(cost, campaignSource));
		adGroupDetail.setEndDate(DateUtil.getDate(endDate));
		adGroupDetail.setStartDate(DateUtil.getDate(startDate));
		adGroupDetail.setImpressions(impressions);
		conversionsValue = conversionsValue.replaceAll(",", "");
		adGroupDetail.setRevenue(Double.valueOf(conversionsValue));
		adGroupDetail.setTransactionCount(conversions);
		adGroupDetailList.add(adGroupDetail);
	}

	public static void mapAdData(String headlinePart1, String headlinePart2, String adGroupId, String adId, Integer clicks, String cost, Integer impressions,
			String conversionsValue, Integer conversions, String startDate, String endDate, List<Ad> adList, List<AdDetail> adDetailList, CampaignSource campaignSource)
			throws IOException {

		Ad ad = new Ad();
		ad.setAdGroupId(adGroupId);
		ad.setAdId(adId);
		ad.setHeadLine1(headlinePart1);
		ad.setHeadLine2(headlinePart2);
		adList.add(ad);

		AdDetail adDetail = new AdDetail();
		adDetail.setAdId(adId);
		adDetail.setClicks(clicks);
		adDetail.setCost(getCost(cost, campaignSource));
		adDetail.setEndDate(DateUtil.getDate(endDate));
		adDetail.setImpressions(impressions);
		conversionsValue = conversionsValue.replaceAll(",", "");
		adDetail.setRevenue(Double.valueOf(conversionsValue));
		adDetail.setStartDate(DateUtil.getDate(startDate));
		adDetail.setTransactionCount(conversions);
		adDetailList.add(adDetail);
	}

	public static void mapKeywordData(String keywordId, String adGroupId, String keywordName, Integer clicks, String cost, Integer impressions, String conversionsValue,
			Integer conversions, String startDate, String endDate, List<Keyword> keywordList, List<KeywordDetail> keywordDetailList, CampaignSource campaignSource)
			throws IOException {

		Keyword keyword = new Keyword();
		keyword.setAdGroupId(adGroupId);
		keyword.setKeywordId(keywordId);
		keywordName = keywordName.replaceAll("\"", "");
		keyword.setName(keywordName);

		keywordList.add(keyword);

		KeywordDetail keywordDetail = new KeywordDetail();
		keywordDetail.setKeywordId(keywordId);
		keywordDetail.setClicks(clicks);
		keywordDetail.setCost(getCost(cost, campaignSource));
		keywordDetail.setEndDate(DateUtil.getDate(endDate));
		keywordDetail.setImpressions(impressions);
		conversionsValue = conversionsValue.replaceAll(",", "");
		keywordDetail.setRevenue(Double.valueOf(conversionsValue));
		keywordDetail.setStartDate(DateUtil.getDate(startDate));
		keywordDetail.setTransactionCount(conversions);
		keywordDetailList.add(keywordDetail);
	}

	public static Long getTransactionCount(ServerStream<SearchGoogleAdsStreamResponse> stream) throws IOException {

		Long transactionCount = 0L;

		for (SearchGoogleAdsStreamResponse response : stream) {
			for (GoogleAdsRow googleAdsRow : response.getResultsList()) {
				transactionCount += Double.valueOf(googleAdsRow.getMetrics().getConversions()).longValue();
			}
		}
		return transactionCount;
	}

	private static Double getCost(String value, CampaignSource campaignSource) {
		Double cost = Double.valueOf(value);
		if (CampaignSource.Google.equals(campaignSource)) {
			cost = cost / 1000000;
		}
		return Math.round(cost * 100D) / 100D;
	}
}
