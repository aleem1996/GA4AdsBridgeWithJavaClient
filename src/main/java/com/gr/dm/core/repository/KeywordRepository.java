package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.AggregateDto;
import com.gr.dm.core.dto.KeywordDto;
import com.gr.dm.core.entity.Keyword;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface KeywordRepository extends CrudRepository<Keyword, Long> {

	List<Keyword> findByAdGroupId(String adGroupId);

	Keyword findByAdGroupIdAndKeywordId(String adGroupId, String keywordId);

	@Query("select new com.gr.dm.core.dto.KeywordDto(kw.id, kw.adGroupId, kw.keywordId, kw.name, "
			+ " sum(kd.transactionCount),"
			+ " sum(kd.revenue),"
			+ " sum(kd.cost),"
			+ " max(kd.startDate), max(kd.endDate),"
			+ " sum(kd.clicks), sum(kd.impressions) )"
			+ " from Keyword kw, KeywordDetail kd where kw.keywordId = kd.keywordId and kd.startDate >= :startDate"
			+ " and kd.endDate <= :endDate and kw.adGroupId = :adGroupId group by kw.keywordId")
	List<KeywordDto> getKeywords(@Param("adGroupId") String adGroupId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("select new com.gr.dm.core.dto.AggregateDto(sum(kd.transactionCount), "
			+ " sum(kd.revenue), "
			+ " sum(kd.clicks), "
			+ " sum(kd.impressions), "
			+ " sum(kd.cost) ) "
			+ " from Keyword kw, KeywordDetail kd where kw.keywordId = kd.keywordId and kd.startDate >= :startDate"
			+ " and kd.endDate <= :endDate and kw.adGroupId = :adGroupId")
	AggregateDto getStats(@Param("adGroupId") String adGroupId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}