package com.gr.dm.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.AdGroupAttribution;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface AdGroupAttributionRepository extends CrudRepository<AdGroupAttribution, Long> {

	@Query("select cd from AdGroupAttribution cd where cd.adGroupId = :adGroupId and cd.startDate = :startDate and cd.endDate = :endDate")
	AdGroupAttribution getAdGroupAttribution(@Param("adGroupId") String adGroupId,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}