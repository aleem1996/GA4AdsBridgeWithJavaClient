package com.gr.dm.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.CampaignAttribution;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface CampaignAttributionRepository extends CrudRepository<CampaignAttribution, Long> {

	@Query("select cd from CampaignAttribution cd where cd.campaignId = :campaignId and cd.startDate = :startDate and cd.endDate = :endDate and cd.isManual = :isManual")
	CampaignAttribution getCampaignAttribution(@Param("campaignId") String campaignId,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("isManual")Boolean isManual);

}