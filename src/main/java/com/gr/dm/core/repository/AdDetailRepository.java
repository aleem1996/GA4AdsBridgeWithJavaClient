package com.gr.dm.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.AdDetail;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface AdDetailRepository extends CrudRepository<AdDetail, Long> {

	@Query("select ad from AdDetail ad where ad.adId = :adId and ad.startDate = :startDate and ad.endDate = :endDate")
	AdDetail getAdDetail(@Param("adId") String adId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

}