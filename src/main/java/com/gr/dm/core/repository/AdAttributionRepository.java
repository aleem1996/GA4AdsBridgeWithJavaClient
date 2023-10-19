package com.gr.dm.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.AdAttribution;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface AdAttributionRepository extends CrudRepository<AdAttribution, Long> {

	@Query("select cd from AdAttribution cd where cd.adId = :adId and cd.startDate = :startDate and cd.endDate = :endDate")
	AdAttribution getAdAttribution(@Param("adId") String adId, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

}