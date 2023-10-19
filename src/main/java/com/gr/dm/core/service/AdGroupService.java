package com.gr.dm.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.dm.core.dto.AdGroupDto;
import com.gr.dm.core.dto.AdGroupListDto;
import com.gr.dm.core.dto.AggregateDto;
import com.gr.dm.core.entity.AdGroup;
import com.gr.dm.core.entity.AdGroupAttribution;
import com.gr.dm.core.entity.AdGroupDetail;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.repository.AdGroupAttributionRepository;
import com.gr.dm.core.repository.AdGroupDetailRepository;
import com.gr.dm.core.repository.AdGroupRepository;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
@Service
public class AdGroupService {

	@Autowired
	AdGroupRepository adGroupRepository;

	@Autowired
	AdGroupDetailRepository adGroupDetailRepository;
	
	@Autowired
	AdGroupAttributionRepository adGroupAttributionRepository;

	public void saveAdGroup(List<AdGroup> adGroups) {
		for (AdGroup adGroup : adGroups) {
			AdGroup savedAdGroup = adGroupRepository.findByCampaignIdAndAdGroupSourceAndAdGroupId(
					adGroup.getCampaignId(), adGroup.getAdGroupSource(), adGroup.getAdGroupId());
			if (Util.isNull(savedAdGroup)) {
				saveAdGroup(adGroup);
			}
		}
	}

	public void saveAdGroup(AdGroup adGroup) {
		adGroupRepository.save(adGroup);
	}

	public void saveAdGroupDetail(List<AdGroupDetail> adGroupDetails) {
		for (AdGroupDetail adGroupDetail : adGroupDetails) {
			AdGroupDetail savedDetail = adGroupDetailRepository.getAdGroupDetail(adGroupDetail.getAdGroupId(),
					adGroupDetail.getStartDate(), adGroupDetail.getEndDate());
			if (Util.isNull(savedDetail)) {
				saveAdGroupDetail(adGroupDetail);
			} else {
				savedDetail.setClicks(adGroupDetail.getClicks());
				savedDetail.setCost(adGroupDetail.getCost());
				savedDetail.setImpressions(adGroupDetail.getImpressions());
				savedDetail.setRevenue(adGroupDetail.getRevenue());
				savedDetail.setTransactionCount(adGroupDetail.getTransactionCount());
				saveAdGroupDetail(savedDetail);
			}
		}
	}

	public void saveAdGroupDetail(AdGroupDetail adGroupDetail) {
		adGroupDetailRepository.save(adGroupDetail);
	}
	
	public void saveAdGroupAttribution(List<AdGroupAttribution> adGroupAttributions) {
		for (AdGroupAttribution adGroupAttribution : adGroupAttributions) {
			AdGroupAttribution savedAttribution = adGroupAttributionRepository.getAdGroupAttribution(
					adGroupAttribution.getAdGroupId(), adGroupAttribution.getStartDate(),
					adGroupAttribution.getEndDate());
			if (Util.isNull(savedAttribution)) {
				saveAdGroupAttribution(adGroupAttribution);
			} else {
				savedAttribution.setPurchases1DayView(adGroupAttribution.getPurchases1DayView());
				savedAttribution.setPurchases7DayView(adGroupAttribution.getPurchases7DayView());
				savedAttribution.setPurchases28DayView(adGroupAttribution.getPurchases28DayView());
				savedAttribution.setPurchases1DayClick(adGroupAttribution.getPurchases1DayClick());
				savedAttribution.setPurchases7DayClick(adGroupAttribution.getPurchases7DayClick());
				savedAttribution.setPurchases28DayClick(adGroupAttribution.getPurchases28DayClick());
				
				savedAttribution.setRevenue1DayView(adGroupAttribution.getRevenue1DayView());
				savedAttribution.setRevenue7DayView(adGroupAttribution.getRevenue7DayView());
				savedAttribution.setRevenue28DayView(adGroupAttribution.getRevenue28DayView());
				savedAttribution.setRevenue1DayClick(adGroupAttribution.getRevenue1DayClick());
				savedAttribution.setRevenue7DayClick(adGroupAttribution.getRevenue7DayClick());
				savedAttribution.setRevenue28DayClick(adGroupAttribution.getRevenue28DayClick());
				saveAdGroupAttribution(savedAttribution);
			}
		}
	}
	
	public void saveAdGroupAttribution(AdGroupAttribution adGroupAttribution) {
		adGroupAttributionRepository.save(adGroupAttribution);
	}
	
	public AdGroupListDto getAdGroups(String campaignId, Date startDate, Date endDate) {
		return getAdGroups(campaignId, startDate, endDate, null);
	}
	
	public AdGroupListDto getAdGroups(String campaignId, Date startDate, Date endDate, CampaignSource source) {
		AggregateDto aggregateDto = null;
		if(!Util.isNull(source) && CampaignSource.Facebook.equals(source)) {
			aggregateDto = adGroupRepository.getFacebookStats(campaignId, startDate, endDate);
		} else {
			aggregateDto = adGroupRepository.getStats(campaignId, startDate, endDate);
		}
		List<AdGroupDto> adGroups =  adGroupRepository.getAdGroups(campaignId, startDate, endDate);
		AdGroupListDto adGroupListDto = new AdGroupListDto();
		adGroupListDto.setAdGroupDto(adGroups);
		adGroupListDto.setAggregateDto(aggregateDto);
		return adGroupListDto;
	}

}
