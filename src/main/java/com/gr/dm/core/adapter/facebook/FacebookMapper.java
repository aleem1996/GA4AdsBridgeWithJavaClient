package com.gr.dm.core.adapter.facebook;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdsActionStats;
import com.facebook.ads.sdk.AdsInsights;
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
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

public class FacebookMapper {
	public static void mapCampaignData(APINodeList<AdsInsights> adsInsights, String startDate, String endDate,
			List<Campaign> campaignList, List<CampaignDetail> campaignDetailList, List<CampaignAttribution> campaignAttributions) {
		for (AdsInsights adsInsight : adsInsights) {
			Campaign campaign = new Campaign();
			campaign.setCampaignSource(CampaignSource.Facebook);
			campaign.setCampaignId(adsInsight.getFieldCampaignId());
			campaign.setName(adsInsight.getFieldCampaignName());
			campaignList.add(campaign);

			CampaignDetail campaignDetail = new CampaignDetail();
			campaignDetail.setIsDataSynced(Boolean.TRUE);
			campaignDetail.setLastUpdated(new Date());
			campaignDetail.setCampaignDetailSource(CampaignDetailSource.Facebook);
			campaignDetail.setCampaignId(campaign.getCampaignId());
			campaignDetail.setCost(adsInsight.getFieldSpend() == null ? 0 : Double.valueOf(adsInsight.getFieldSpend()));
			campaignDetail.setEndDate(DateUtil.getDate(endDate));
			campaignDetail.setStartDate(DateUtil.getDate(startDate));
			campaignDetail.setClicks(adsInsight.getFieldInlineLinkClicks() == null ? 0 : Integer.valueOf(adsInsight.getFieldInlineLinkClicks()));
			campaignDetail.setImpressions(adsInsight.getFieldImpressions() == null ? 0 : Integer.valueOf(adsInsight.getFieldImpressions()));
			
			CampaignAttribution campaignAttribution = new CampaignAttribution();
			campaignAttribution.setCampaignId(campaign.getCampaignId());
			campaignAttribution.setEndDate(DateUtil.getDate(endDate));
			campaignAttribution.setStartDate(DateUtil.getDate(startDate));

			if(!Util.isNull(adsInsight.getFieldActions())) {
				for (AdsActionStats stats : adsInsight.getFieldActions()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						if(!Util.isNull(stats.getField1dView())) {
							campaignAttribution.setPurchases1DayView(Integer.valueOf(stats.getField1dView()));
						}
						if(!Util.isNull(stats.getField7dView())) {
							campaignAttribution.setPurchases7DayView(Integer.valueOf(stats.getField7dView()));
						}
						if(!Util.isNull(stats.getField28dView())) {
							campaignAttribution.setPurchases28DayView(Integer.valueOf(stats.getField28dView()));
						}
						if(!Util.isNull(stats.getField1dClick())) {
							campaignAttribution.setPurchases1DayClick(Integer.valueOf(stats.getField1dClick()));
						}
						if(!Util.isNull(stats.getField7dClick())) {
							campaignAttribution.setPurchases7DayClick(Integer.valueOf(stats.getField7dClick()));
						}
						if(!Util.isNull(stats.getField28dClick())) {
							campaignAttribution.setPurchases28DayClick(Integer.valueOf(stats.getField28dClick()));
						}
						break;
					}
				}
			}
			
			if(!Util.isNull(adsInsight.getFieldActionValues())) {
				for (AdsActionStats stats : adsInsight.getFieldActionValues()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						if(!Util.isNull(stats.getField1dView())) {
							campaignAttribution.setRevenue1DayView(Double.valueOf(stats.getField1dView()));
						}
						if(!Util.isNull(stats.getField7dView())) {
							campaignAttribution.setRevenue7DayView(Double.valueOf(stats.getField7dView()));
						}
						if(!Util.isNull(stats.getField28dView())) {
							campaignAttribution.setRevenue28DayView(Double.valueOf(stats.getField28dView()));
						}
						if(!Util.isNull(stats.getField1dClick())) {
							campaignAttribution.setRevenue1DayClick(Double.valueOf(stats.getField1dClick()));
						}
						if(!Util.isNull(stats.getField7dClick())) {
							campaignAttribution.setRevenue7DayClick(Double.valueOf(stats.getField7dClick()));
						}
						if(!Util.isNull(stats.getField28dClick())) {
							campaignAttribution.setRevenue28DayClick(Double.valueOf(stats.getField28dClick()));
						}
						break;
					}
				}
			}
			
			Double revenue = Util.isNotNull(campaignAttribution.getRevenue7DayClick()) ? campaignAttribution.getRevenue7DayClick() : 0.0;
			revenue += Util.isNotNull(campaignAttribution.getRevenue1DayView()) ? campaignAttribution.getRevenue1DayView() : 0.0;
			campaignDetail.setRevenue(revenue);
			
			Integer transactionCount = Util.isNotNull(campaignAttribution.getPurchases7DayClick()) ? campaignAttribution.getPurchases7DayClick() : 0;
			transactionCount += Util.isNotNull(campaignAttribution.getPurchases1DayView()) ? campaignAttribution.getPurchases1DayView() : 0;
			campaignDetail.setTransactionCount(transactionCount);
			
			campaignAttributions.add(campaignAttribution);
			campaignDetailList.add(campaignDetail);
		}
	}
	
	
	public static void mapAdGroupData(APINodeList<AdsInsights> adsInsights, String startDate, String endDate,
			List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList,
			List<AdGroupAttribution> adGroupAttributions) {
		for (AdsInsights adsInsight : adsInsights) {
			AdGroup adGroup = new AdGroup();
			adGroup.setAdGroupSource(CampaignSource.Facebook);
			adGroup.setCampaignId(adsInsight.getFieldCampaignId());
			adGroup.setName(adsInsight.getFieldAdsetName());
			adGroup.setAdGroupId(adsInsight.getFieldAdsetId());
			adGroupList.add(adGroup);

			AdGroupDetail adGroupDetail = new AdGroupDetail();
			adGroupDetail.setAdGroupId(adGroup.getAdGroupId());
			adGroupDetail.setCost(adsInsight.getFieldSpend() == null ? 0 : Double.valueOf(adsInsight.getFieldSpend()));
			adGroupDetail.setEndDate(DateUtil.getDate(endDate));
			adGroupDetail.setStartDate(DateUtil.getDate(startDate));
			adGroupDetail.setClicks(adsInsight.getFieldInlineLinkClicks() == null ? 0 : Integer.valueOf(adsInsight.getFieldInlineLinkClicks()));
			adGroupDetail.setImpressions(adsInsight.getFieldImpressions() == null ? 0 : Integer.valueOf(adsInsight.getFieldImpressions()));

			AdGroupAttribution adGroupAttribution = new AdGroupAttribution();
			adGroupAttribution.setAdGroupId(adGroup.getAdGroupId());
			adGroupAttribution.setEndDate(DateUtil.getDate(endDate));
			adGroupAttribution.setStartDate(DateUtil.getDate(startDate));

			if(!Util.isNull(adsInsight.getFieldActions())) {
				for (AdsActionStats stats : adsInsight.getFieldActions()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						if(!Util.isNull(stats.getField1dView())) {
							adGroupAttribution.setPurchases1DayView(Integer.valueOf(stats.getField1dView()));
						}
						if(!Util.isNull(stats.getField7dView())) {
							adGroupAttribution.setPurchases7DayView(Integer.valueOf(stats.getField7dView()));
						}
						if(!Util.isNull(stats.getField28dView())) {
							adGroupAttribution.setPurchases28DayView(Integer.valueOf(stats.getField28dView()));
						}
						if(!Util.isNull(stats.getField1dClick())) {
							adGroupAttribution.setPurchases1DayClick(Integer.valueOf(stats.getField1dClick()));
						}
						if(!Util.isNull(stats.getField7dClick())) {
							adGroupAttribution.setPurchases7DayClick(Integer.valueOf(stats.getField7dClick()));
						}
						if(!Util.isNull(stats.getField28dClick())) {
							adGroupAttribution.setPurchases28DayClick(Integer.valueOf(stats.getField28dClick()));
						}
						break;
					}
				}
			}
			
			if(!Util.isNull(adsInsight.getFieldActionValues())) {
				for (AdsActionStats stats : adsInsight.getFieldActionValues()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						if(!Util.isNull(stats.getField1dView())) {
							adGroupAttribution.setRevenue1DayView(Double.valueOf(stats.getField1dView()));
						}
						if(!Util.isNull(stats.getField7dView())) {
							adGroupAttribution.setRevenue7DayView(Double.valueOf(stats.getField7dView()));
						}
						if(!Util.isNull(stats.getField28dView())) {
							adGroupAttribution.setRevenue28DayView(Double.valueOf(stats.getField28dView()));
						}
						if(!Util.isNull(stats.getField1dClick())) {
							adGroupAttribution.setRevenue1DayClick(Double.valueOf(stats.getField1dClick()));
						}
						if(!Util.isNull(stats.getField7dClick())) {
							adGroupAttribution.setRevenue7DayClick(Double.valueOf(stats.getField7dClick()));
						}
						if(!Util.isNull(stats.getField28dClick())) {
							adGroupAttribution.setRevenue28DayClick(Double.valueOf(stats.getField28dClick()));
						}
						break;
					}
				}
			}
			
			Double revenue = Util.isNotNull(adGroupAttribution.getRevenue7DayClick()) ? adGroupAttribution.getRevenue7DayClick() : 0.0;
			revenue += Util.isNotNull(adGroupAttribution.getRevenue1DayView()) ? adGroupAttribution.getRevenue1DayView() : 0.0;
			adGroupDetail.setRevenue(revenue);
			
			Integer transactionCount = Util.isNotNull(adGroupAttribution.getPurchases7DayClick()) ? adGroupAttribution.getPurchases7DayClick() : 0;
			transactionCount += Util.isNotNull(adGroupAttribution.getPurchases1DayView()) ? adGroupAttribution.getPurchases1DayView() : 0;
			adGroupDetail.setTransactionCount(transactionCount);
			
			adGroupAttributions.add(adGroupAttribution);
			adGroupDetailList.add(adGroupDetail);
		}
	}
	
	public static void mapAdData(APINodeList<AdsInsights> adsInsights, String startDate, String endDate,
			List<Ad> adList, List<AdDetail> adDetailList, List<AdAttribution> adAttributions) {
		for (AdsInsights adsInsight : adsInsights) {
			Ad ad = new Ad();
			ad.setAdId(adsInsight.getFieldAdId());
			ad.setHeadLine1(adsInsight.getFieldAdName());
			ad.setAdGroupId(adsInsight.getFieldAdsetId());
			adList.add(ad);

			AdDetail adDetail = new AdDetail();
			adDetail.setAdId(ad.getAdId());
			adDetail.setCost(adsInsight.getFieldSpend() == null ? 0 : Double.valueOf(adsInsight.getFieldSpend()));
			adDetail.setEndDate(DateUtil.getDate(endDate));
			adDetail.setStartDate(DateUtil.getDate(startDate));
			adDetail.setClicks(adsInsight.getFieldInlineLinkClicks() == null ? 0 : Integer.valueOf(adsInsight.getFieldInlineLinkClicks()));
			adDetail.setImpressions(adsInsight.getFieldImpressions() == null ? 0 : Integer.valueOf(adsInsight.getFieldImpressions()));

			AdAttribution adAttribution = new AdAttribution();
			adAttribution.setAdId(ad.getAdId());
			adAttribution.setEndDate(DateUtil.getDate(endDate));
			adAttribution.setStartDate(DateUtil.getDate(startDate));

			if(!Util.isNull(adsInsight.getFieldActions())) {
				for (AdsActionStats stats : adsInsight.getFieldActions()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						if(!Util.isNull(stats.getField1dView())) {
							adAttribution.setPurchases1DayView(Integer.valueOf(stats.getField1dView()));
						}
						if(!Util.isNull(stats.getField7dView())) {
							adAttribution.setPurchases7DayView(Integer.valueOf(stats.getField7dView()));
						}
						if(!Util.isNull(stats.getField28dView())) {
							adAttribution.setPurchases28DayView(Integer.valueOf(stats.getField28dView()));
						}
						if(!Util.isNull(stats.getField1dClick())) {
							adAttribution.setPurchases1DayClick(Integer.valueOf(stats.getField1dClick()));
						}
						if(!Util.isNull(stats.getField7dClick())) {
							adAttribution.setPurchases7DayClick(Integer.valueOf(stats.getField7dClick()));
						}
						if(!Util.isNull(stats.getField28dClick())) {
							adAttribution.setPurchases28DayClick(Integer.valueOf(stats.getField28dClick()));
						}
						break;
					}
				}
			}
			

			if(!Util.isNull(adsInsight.getFieldActionValues())) {
				for (AdsActionStats stats : adsInsight.getFieldActionValues()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						if(!Util.isNull(stats.getField1dView())) {
							adAttribution.setRevenue1DayView(Double.valueOf(stats.getField1dView()));
						}
						if(!Util.isNull(stats.getField7dView())) {
							adAttribution.setRevenue7DayView(Double.valueOf(stats.getField7dView()));
						}
						if(!Util.isNull(stats.getField28dView())) {
							adAttribution.setRevenue28DayView(Double.valueOf(stats.getField28dView()));
						}
						if(!Util.isNull(stats.getField1dClick())) {
							adAttribution.setRevenue1DayClick(Double.valueOf(stats.getField1dClick()));
						}
						if(!Util.isNull(stats.getField7dClick())) {
							adAttribution.setRevenue7DayClick(Double.valueOf(stats.getField7dClick()));
						}
						if(!Util.isNull(stats.getField28dClick())) {
							adAttribution.setRevenue28DayClick(Double.valueOf(stats.getField28dClick()));
						}
						break;
					}
				}
			}
			
			Double revenue = Util.isNotNull(adAttribution.getRevenue7DayClick()) ? adAttribution.getRevenue7DayClick() : 0.0;
			revenue += Util.isNotNull(adAttribution.getRevenue1DayView()) ? adAttribution.getRevenue1DayView() : 0.0;
			adDetail.setRevenue(revenue);
			
			Integer transactionCount = Util.isNotNull(adAttribution.getPurchases7DayClick()) ? adAttribution.getPurchases7DayClick() : 0;
			transactionCount += Util.isNotNull(adAttribution.getPurchases1DayView()) ? adAttribution.getPurchases1DayView() : 0;
			adDetail.setTransactionCount(transactionCount);
			
			adAttributions.add(adAttribution);
			adDetailList.add(adDetail);
		}
	}
	
	
	public static Map<String, Long> getTransactionCount(APINodeList<AdsInsights> adsInsights) {
		Map<String, Long> transactionCountMap = new HashMap<String, Long>();
		transactionCountMap.put(Constants.FB_PURCHASES, 0L);
		transactionCountMap.put(Constants.FB_PURCHASES_7_DAY, 0L);
		transactionCountMap.put(Constants.FB_PURCHASES_28_DAY, 0L);

		for (AdsInsights adsInsight : adsInsights) {

			if (!Util.isNull(adsInsight.getFieldActions())) {
				for (AdsActionStats stats : adsInsight.getFieldActions()) {
					if (Constants.FB_PIXEL_PURCHASE_EVENT.equals(stats.getFieldActionType())) {
						
						long purchases = 0;
						
						if (Util.isNotNull(stats.getField1dView())) {
							purchases += Long.valueOf(stats.getField1dView());
						}
						
						if (Util.isNotNull(stats.getField7dClick())) {
							purchases += Long.valueOf(stats.getField7dClick());
						}
						
						transactionCountMap.put(Constants.FB_PURCHASES, purchases);
						
						
//						if (Util.isNotNull(stats.getFieldValue())) {
//							long purchases = transactionCountMap.get(Constants.FB_PURCHASES) + Long.valueOf(stats.getFieldValue());
//							transactionCountMap.put(Constants.FB_PURCHASES, purchases);
//						}
//						if (Util.isNotNull(stats.getField7dView())) {
//							long purchases = transactionCountMap.get(Constants.FB_PURCHASES_7_DAY) + Long.valueOf(stats.getField7dView());
//							transactionCountMap.put(Constants.FB_PURCHASES_7_DAY, purchases);
//						}
//						if (Util.isNotNull(stats.getField28dView())) {
//							long purchases = transactionCountMap.get(Constants.FB_PURCHASES_28_DAY) + Long.valueOf(stats.getField28dView());
//							transactionCountMap.put(Constants.FB_PURCHASES_28_DAY, purchases);
//						}
//						if (Util.isNotNull(stats.getField7dClick())) {
//							long purchases = transactionCountMap.get(Constants.FB_PURCHASES_7_DAY) + Long.valueOf(stats.getField7dClick());
//							transactionCountMap.put(Constants.FB_PURCHASES_7_DAY, purchases);
//						}
//						if (Util.isNotNull(stats.getField28dClick())) {
//							long purchases = transactionCountMap.get(Constants.FB_PURCHASES_28_DAY) + Long.valueOf(stats.getField28dClick());
//							transactionCountMap.put(Constants.FB_PURCHASES_28_DAY, purchases);
//						}
						break;
					}
				}
			}

		}
		return transactionCountMap;
	}
}
