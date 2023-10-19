package com.gr.dm.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.dm.core.dto.AdDto;
import com.gr.dm.core.dto.AdListDto;
import com.gr.dm.core.dto.AggregateDto;
import com.gr.dm.core.entity.Ad;
import com.gr.dm.core.entity.AdAttribution;
import com.gr.dm.core.entity.AdDetail;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.repository.AdAttributionRepository;
import com.gr.dm.core.repository.AdDetailRepository;
import com.gr.dm.core.repository.AdRepository;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
@Service
public class AdService {

	@Autowired
	AdRepository adRepository;

	@Autowired
	AdDetailRepository adDetailRepository;
	
	@Autowired
	AdAttributionRepository adAttributionRepository;

	public void saveAd(List<Ad> ads) {
		for (Ad ad : ads) {
			Ad savedAd = adRepository.findByAdGroupIdAndAdId(ad.getAdGroupId(), ad.getAdId());
			if (Util.isNull(savedAd)) {
				saveAd(ad);
			} else {
				savedAd.setImageUrl(ad.getImageUrl());
				saveAd(savedAd);
			}
		}
	}

	public void saveAd(Ad ad) {
		adRepository.save(ad);
	}

	public void saveAdDetail(List<AdDetail> adDetails) {
		for (AdDetail adDetail : adDetails) {
			AdDetail savedDetail = adDetailRepository.getAdDetail(adDetail.getAdId(), adDetail.getStartDate(),
					adDetail.getEndDate());
			if (Util.isNull(savedDetail)) {
				saveAdDetail(adDetail);
			} else {
				savedDetail.setClicks(adDetail.getClicks());
				savedDetail.setCost(adDetail.getCost());
				savedDetail.setImpressions(adDetail.getImpressions());
				savedDetail.setRevenue(adDetail.getRevenue());
				savedDetail.setTransactionCount(adDetail.getTransactionCount());
				saveAdDetail(savedDetail);
			}
		}
	}

	public void saveAdDetail(AdDetail adDetail) {
		adDetailRepository.save(adDetail);
	}
	
	public void saveAdAttribution(List<AdAttribution> adAttributions) {
		for (AdAttribution adAttribution : adAttributions) {
			AdAttribution savedAttribution = adAttributionRepository.getAdAttribution(adAttribution.getAdId(),
					adAttribution.getStartDate(), adAttribution.getEndDate());
			if (Util.isNull(savedAttribution)) {
				saveAdAttribution(adAttribution);
			} else {
				savedAttribution.setPurchases1DayView(adAttribution.getPurchases1DayView());
				savedAttribution.setPurchases7DayView(adAttribution.getPurchases7DayView());
				savedAttribution.setPurchases28DayView(adAttribution.getPurchases28DayView());
				savedAttribution.setPurchases1DayClick(adAttribution.getPurchases1DayClick());
				savedAttribution.setPurchases7DayClick(adAttribution.getPurchases7DayClick());
				savedAttribution.setPurchases28DayClick(adAttribution.getPurchases28DayClick());
				
				savedAttribution.setRevenue1DayView(adAttribution.getRevenue1DayView());
				savedAttribution.setRevenue7DayView(adAttribution.getRevenue7DayView());
				savedAttribution.setRevenue28DayView(adAttribution.getRevenue28DayView());
				savedAttribution.setRevenue1DayClick(adAttribution.getRevenue1DayClick());
				savedAttribution.setRevenue7DayClick(adAttribution.getRevenue7DayClick());
				savedAttribution.setRevenue28DayClick(adAttribution.getRevenue28DayClick());
				saveAdAttribution(savedAttribution);
			}
		}
	}
	
	public void saveAdAttribution(AdAttribution adAttribution) {
		adAttributionRepository.save(adAttribution);
	}
	
	public AdListDto getAds(String adGroupId, Date startDate, Date endDate) {
		return getAds(adGroupId, startDate, endDate, null);
	}
	
	public AdListDto getAds(String adGroupId, Date startDate, Date endDate, CampaignSource source) {
		AggregateDto aggregateDto = null;
		if(!Util.isNull(source) && CampaignSource.Facebook.equals(source)) {
			aggregateDto = adRepository.getFacebookStats(adGroupId, startDate, endDate);
		} else {
			aggregateDto = adRepository.getStats(adGroupId, startDate, endDate);
		}
		List<AdDto> adGroups =  adRepository.getAds(adGroupId, startDate, endDate);
		
		AdListDto adGroupListDto = new AdListDto();
		adGroupListDto.setAdDto(adGroups);
		adGroupListDto.setAggregateDto(aggregateDto);
		return adGroupListDto;
	}
	
	public Ad getAd(String groupId, String adId) {
		return adRepository.findByAdGroupIdAndAdId(groupId, adId);
	}

}
