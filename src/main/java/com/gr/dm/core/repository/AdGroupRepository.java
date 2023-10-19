package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.AdGroupDto;
import com.gr.dm.core.dto.AggregateDto;
import com.gr.dm.core.entity.AdGroup;
import com.gr.dm.core.entity.CampaignSource;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface AdGroupRepository extends CrudRepository<AdGroup, Long> {
	
	List<AdGroup> findByCampaignId(String campaignId);
	
	AdGroup findByCampaignIdAndAdGroupSourceAndAdGroupId(String campaignId, CampaignSource adGroupSource, String adGroupId);
	
	@Query("select new com.gr.dm.core.dto.AdGroupDto(ad.id, ad.name, ad.campaignId, ad.adGroupId, ad.adGroupSource, "
			+ " sum(gd.transactionCount),"
			+ " sum(gd.revenue),"
			+ " sum(gd.cost),"
			+ " max(gd.startDate), max(gd.endDate),"
			+ " sum(gd.clicks), sum(gd.impressions), "
			+ " (select sum(attr.purchases7DayView + attr.purchases7DayClick) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.purchases28DayView + attr.purchases28DayClick) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.revenue7DayView + attr.revenue7DayClick) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.revenue28DayView + attr.revenue28DayClick) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.purchases1DayView) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.purchases7DayClick) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.revenue1DayView) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId),"
			+ " (select sum(attr.revenue7DayClick) from AdGroupAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adGroupId = gd.adGroupId) )"
			+ " from AdGroup ad, AdGroupDetail gd where ad.adGroupId = gd.adGroupId and gd.startDate >= :startDate"
			+ " and gd.endDate <= :endDate and ad.campaignId = :campaignId group by ad.adGroupId")
	List<AdGroupDto> getAdGroups(@Param("campaignId") String campaignId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("select new com.gr.dm.core.dto.AggregateDto(sum(gd.transactionCount), "
			+ " sum(gd.revenue), "
			+ " sum(gd.clicks), "
			+ " sum(gd.impressions), "
			+ " sum(gd.cost), "
			+ " sum(attr.purchases7DayView + attr.purchases7DayClick),"
			+ " sum(attr.purchases28DayView + attr.purchases28DayClick),"
			+ " sum(attr.revenue7DayView + attr.revenue7DayClick),"
			+ " sum(attr.revenue28DayView + attr.revenue28DayClick),"
			+ " sum(attr.purchases1DayView), sum(attr.purchases7DayClick), sum(attr.revenue1DayView), sum(attr.revenue7DayClick) )"
			+ " from AdGroup ad, AdGroupDetail gd,  AdGroupAttribution attr where ad.adGroupId = gd.adGroupId and gd.startDate >= :startDate"
			+ " and gd.endDate <= :endDate and ad.campaignId = :campaignId and gd.adGroupId = attr.adGroupId and gd.startDate = attr.startDate and gd.endDate = attr.endDate")
	AggregateDto getFacebookStats(@Param("campaignId") String campaignId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("select new com.gr.dm.core.dto.AggregateDto(sum(gd.transactionCount), "
			+ " sum(gd.revenue), "
			+ " sum(gd.clicks), "
			+ " sum(gd.impressions), "
			+ " sum(gd.cost), "
			+ " cast (0 as long),"
			+ " cast (0 as long),"
			+ " 0.0,"
			+ " 0.0,"
			+ " cast (0 as long),"
			+ " cast (0 as long),"
			+ " 0.0,"
			+ " 0.0 )"
			+ " from AdGroup ad, AdGroupDetail gd where ad.adGroupId = gd.adGroupId and gd.startDate >= :startDate"
			+ " and gd.endDate <= :endDate and ad.campaignId = :campaignId")
	AggregateDto getStats(@Param("campaignId") String campaignId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
}