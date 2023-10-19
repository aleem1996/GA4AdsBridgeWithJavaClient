package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.report.KeywordPerformanceDto;
import com.gr.dm.core.entity.KeywordDetail;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface KeywordDetailRepository extends CrudRepository<KeywordDetail, Long> {

	@Query("select kd from KeywordDetail kd where kd.keywordId = :keywordId and kd.startDate = :startDate and kd.endDate = :endDate")
	KeywordDetail getKeywordDetail(@Param("keywordId") String keywordId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);
	
	@Query(nativeQuery = true)
	List<KeywordPerformanceDto> getKeywordPerformanceReport(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}