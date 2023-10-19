package com.gr.dm.core.adapter.adwords;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

import au.com.bytecode.opencsv.CSVReader;

public class AdWordsMapper {
	public static void mapCampaignData(CSVReader reader, String startDate, String endDate,
			List<Campaign> campaignList, List<CampaignDetail> campaignDetailList, CampaignSource campaignSource, CampaignDetailSource campaignDetailSource) throws IOException {

		// Skip first line in document
		reader.readNext();
		@SuppressWarnings("unchecked")
		List<String[]> data = reader.readAll();
		for (String[] values : data) {
			Campaign campaign = new Campaign();
			campaign.setName(values[0]);
			campaign.setCampaignId(values[1]);
			campaign.setCampaignSource(campaignSource);
			String status = values[7];
			boolean active = false;
			if(!Util.isNullOrEmpty(status) && status.toLowerCase().equals("enabled") || status.toLowerCase().equals("active")) {
				active = true;
			}
			campaign.setActive(active);
			campaignList.add(campaign);

			CampaignDetail campaignDetail = new CampaignDetail();
			campaignDetail.setIsDataSynced(Boolean.TRUE);
			campaignDetail.setLastUpdated(new Date());
			campaignDetail.setCampaignId(campaign.getCampaignId());
			campaignDetail.setTransactionCount(Double.valueOf(values[2]).intValue());
			values[3] = values[3].replaceAll(",", "");
			campaignDetail.setRevenue(Double.valueOf(values[3]));
			campaignDetail.setCost(getCost(values[4], campaignSource));
			campaignDetail.setImpressions(Integer.valueOf(values[5]));
			campaignDetail.setClicks(Integer.valueOf(values[6]));

			campaignDetail.setStartDate(DateUtil.getDate(startDate));
			campaignDetail.setEndDate(DateUtil.getDate(endDate));
			campaignDetail.setCampaignDetailSource(campaignDetailSource);
			campaignDetailList.add(campaignDetail);
		}
	}

	public static void mapAdGroupData(CSVReader reader, String startDate, String endDate,
			List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList, CampaignSource campaignSource) throws IOException {

		// Skip first line in document
		reader.readNext();
		@SuppressWarnings("unchecked")
		List<String[]> data = reader.readAll();
		for (String[] values : data) {

			AdGroup adGroup = new AdGroup();
			adGroup.setAdGroupSource(campaignSource);
			adGroup.setCampaignId(values[0]);
			adGroup.setAdGroupId(values[1]);
			adGroup.setName(values[2]);
			adGroupList.add(adGroup);

			AdGroupDetail adGroupDetail = new AdGroupDetail();
			adGroupDetail.setAdGroupId(values[1]);
			adGroupDetail.setClicks(Integer.valueOf(values[7]));
			adGroupDetail.setCost(getCost(values[5], campaignSource));
			adGroupDetail.setEndDate(DateUtil.getDate(endDate));
			adGroupDetail.setStartDate(DateUtil.getDate(startDate));
			adGroupDetail.setImpressions(Integer.valueOf(values[6]));
			values[4] = values[4].replaceAll(",", "");
			adGroupDetail.setRevenue(Double.valueOf(values[4]));
			adGroupDetail.setTransactionCount(Double.valueOf(values[3]).intValue());
			adGroupDetailList.add(adGroupDetail);
		}
	}

	public static void mapAdData(CSVReader reader, String startDate, String endDate,
			List<Ad> adList, List<AdDetail> adDetailList, CampaignSource campaignSource) throws IOException {
		
		// Skip first line in document
		reader.readNext();
		@SuppressWarnings("unchecked")
		List<String[]> data = reader.readAll();
		for (String[] values : data) {

			Ad ad = new Ad();
			ad.setAdGroupId(values[1]);
			ad.setAdId(values[0]);
			ad.setHeadLine1(values[2]);
			ad.setHeadLine2(values[3]);
			adList.add(ad);

			AdDetail adDetail = new AdDetail();
			adDetail.setAdId(values[0]);
			adDetail.setClicks(Integer.valueOf(values[8]));
			adDetail.setCost(getCost(values[6], campaignSource));
			adDetail.setEndDate(DateUtil.getDate(endDate));
			adDetail.setImpressions(Integer.valueOf(values[7]));
			values[5] = values[5].replaceAll(",", "");
			adDetail.setRevenue(Double.valueOf(values[5]));
			adDetail.setStartDate(DateUtil.getDate(startDate));
			adDetail.setTransactionCount(Double.valueOf(values[4]).intValue());
			adDetailList.add(adDetail);
		}
	}

	public static void mapKeywordData(CSVReader reader, String startDate, String endDate,
			List<Keyword> keywordList, List<KeywordDetail> keywordDetailList, CampaignSource campaignSource) throws IOException {

		// Skip first line in document
		reader.readNext();
		@SuppressWarnings("unchecked")
		List<String[]> data = reader.readAll();
		for (String[] values : data) {

			Keyword keyword = new Keyword();
			keyword.setAdGroupId(values[1]);
			keyword.setKeywordId(values[0]);
			String keywords = values[2].replaceAll("\"", "");
			keyword.setName(keywords);

			keywordList.add(keyword);

			KeywordDetail keywordDetail = new KeywordDetail();
			keywordDetail.setKeywordId(keyword.getKeywordId());
			keywordDetail.setClicks(Integer.valueOf(values[7]));
			keywordDetail.setCost(getCost(values[5], campaignSource));
			keywordDetail.setEndDate(DateUtil.getDate(endDate));
			keywordDetail.setImpressions(Integer.valueOf(values[6]));
			values[4] = values[4].replaceAll(",", "");
			keywordDetail.setRevenue(Double.valueOf(values[4]));
			keywordDetail.setStartDate(DateUtil.getDate(startDate));
			keywordDetail.setTransactionCount(Double.valueOf(values[3]).intValue());
			keywordDetailList.add(keywordDetail);
		}
	}
	
	
	public static Long getTransactionCount(CSVReader reader) throws IOException {

		Long transactionCount = 0L;
		// Skip first line in document
		reader.readNext();
		@SuppressWarnings("unchecked")
		List<String[]> data = reader.readAll();
		for (String[] values : data) {

			if(Util.isNotNull(values)) {
				transactionCount += Double.valueOf(values[1]).longValue();
			}
		}
		return transactionCount;
	}
	
	private static Double getCost(String value, CampaignSource campaignSource) {
		Double cost = Double.valueOf(value);
		if(CampaignSource.Google.equals(campaignSource)) {
			cost = cost / 1000000;
		}
		return Math.round(cost * 100D) / 100D;
	}
}
