package com.gr.dm.core.adapter.stackadapt;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.gr.dm.core.dto.StackAdaptCampaginStats;
import com.gr.dm.core.dto.StackAdaptDataDto;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;


public class StackAdaptMapper {
	public static final Logger logger = Logger.getLogger(StackAdaptMapper.class.getName());
	
	public static void mapCampaignData(StackAdaptDataDto[] stackAdaptData, List<StackAdaptCampaginStats> stackAdaptCampaginStatsList, List<Campaign> campaigns, List<CampaignDetail> campaignDetails, String startDate, String endDate) {
		if (Util.isNull(stackAdaptData) ||  Util.isNull(stackAdaptCampaginStatsList)) {
			return;
		}
		
		for (int i = 0; i<stackAdaptData.length; i++) {
			if (!Util.isNull(stackAdaptCampaginStatsList.get(i))) {
			Campaign campaign = new Campaign();
			campaign.setCampaignId(stackAdaptData[i].getId().toString());
			campaign.setName(stackAdaptData[i].getName());
			campaign.setCampaignSource(CampaignSource.StackAdapt);
			boolean isActive = false;
			
			Date todayDate = DateUtil.getDate(DateUtil.getYesterdayDate());
			todayDate = DateUtil.addDaysToDate(todayDate, 1);
			
			if (Util.isNullOrEmpty(stackAdaptData[i].getEnd_date()) || todayDate.before(DateUtil.getDate(stackAdaptData[i].getEnd_date())) || todayDate.equals(DateUtil.getDate(stackAdaptData[i].getEnd_date()))) {
				isActive = true;
			}
			campaign.setActive(isActive);
			campaigns.add(campaign);
			
			CampaignDetail campaignDetail = new CampaignDetail();
			campaignDetail.setCampaignId(campaign.getCampaignId());
			campaignDetail.setCost(stackAdaptCampaginStatsList.get(i).getCost());
			campaignDetail.setRevenue(stackAdaptCampaginStatsList.get(i).getConv_rev());
			campaignDetail.setStartDate(DateUtil.getDate(startDate));
			campaignDetail.setEndDate(DateUtil.getDate(endDate));
			campaignDetail.setCampaignDetailSource(CampaignDetailSource.StackAdapt);
			campaignDetail.setClicks(stackAdaptCampaginStatsList.get(i).getClick());
			campaignDetail.setImpressions(stackAdaptCampaginStatsList.get(i).getImp());
			campaignDetail.setTransactionCount(stackAdaptCampaginStatsList.get(i).getConv());
			campaignDetail.setIsDataSynced(Boolean.TRUE);
			campaignDetail.setLastUpdated(new Date());
			campaignDetails.add(campaignDetail);
			}
		}
	}
}
