package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.AdDto;
import com.gr.dm.core.dto.AggregateDto;
import com.gr.dm.core.entity.Ad;
/**
 * 
 * @author Aleem Malik
 *
 */
public interface AdRepository extends CrudRepository<Ad, Long> {

	List<Ad> findByAdGroupId(String adGroupId);

	Ad findByAdGroupIdAndAdId(String adGroupId, String adId);
	
	@Query("select new com.gr.dm.core.dto.AdDto(ad.id, ad.adGroupId, ad.adId, ad.headLine1 ,ad.headLine2, "
			+ " sum(gd.transactionCount),"
			+ " sum(gd.revenue),"
			+ " sum(gd.cost),"
			+ " max(gd.startDate), max(gd.endDate),"
			+ " sum(gd.clicks), sum(gd.impressions), ad.imageUrl, "
			+ " (select sum(attr.purchases7DayView + attr.purchases7DayClick) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ " (select sum(attr.purchases28DayView + attr.purchases28DayClick) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ " (select sum(attr.revenue7DayView + attr.revenue7DayClick) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ " (select sum(attr.revenue28DayView + attr.revenue28DayClick) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ "	(select sum(attr.purchases1DayView) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ " (select sum(attr.purchases7DayClick) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ " (select sum(attr.revenue1DayView) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId),"
			+ " (select sum(attr.revenue7DayClick) from AdAttribution attr where attr.startDate >= :startDate and attr.endDate <= :endDate and attr.adId = gd.adId) )"
			+ " from Ad ad, AdDetail gd where ad.adId = gd.adId and gd.startDate >= :startDate"
			+ " and gd.endDate <= :endDate and ad.adGroupId = :adGroupId group by ad.adId")
	List<AdDto> getAds(@Param("adGroupId") String adGroupId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
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
			+ " from Ad ad, AdDetail gd, AdAttribution attr where ad.adId = gd.adId and gd.startDate >= :startDate"
			+ " and gd.endDate <= :endDate and ad.adGroupId = :adGroupId and gd.adId = attr.adId and gd.startDate = attr.startDate and gd.endDate = attr.endDate")
	AggregateDto getFacebookStats(@Param("adGroupId") String adGroupId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
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
			+ " from Ad ad, AdDetail gd where ad.adId = gd.adId and gd.startDate >= :startDate"
			+ " and gd.endDate <= :endDate and ad.adGroupId = :adGroupId")
	AggregateDto getStats(@Param("adGroupId") String adGroupId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}