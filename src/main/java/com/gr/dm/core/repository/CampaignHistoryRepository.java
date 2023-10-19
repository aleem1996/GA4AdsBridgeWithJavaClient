package com.gr.dm.core.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.CampaignHistory;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface CampaignHistoryRepository extends CrudRepository<CampaignHistory, Long> {
	
	@Query(value = "select ch.* from CampaignHistory ch where replace(ch.oldName, ' ', '') = :name limit 1", nativeQuery = true)
	CampaignHistory findCampaignExcludingWhiteSpace(@Param("name") String name);
	
}