package com.gr.dm.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.AdGroupDetail;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface AdGroupDetailRepository extends CrudRepository<AdGroupDetail, Long> {

	@Query("select ad from AdGroupDetail ad where ad.adGroupId = :adGroupId and ad.startDate = :startDate and ad.endDate = :endDate")
	AdGroupDetail getAdGroupDetail(@Param("adGroupId") String adGroupId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

}