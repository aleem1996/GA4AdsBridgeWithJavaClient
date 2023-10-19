package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.CampaignTransactionDto;
import com.gr.dm.core.dto.report.MembershipStatsDto;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.CreatedFrom;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface CampaignTransactionRepository extends CrudRepository<CampaignTransaction, Long> {

	List<CampaignTransaction> findByCampaignId(String id);
	
	@Query("select ct from CampaignTransaction ct where ct.campaignId = :campaignId and ct.startDate >= :startDate and ct.endDate <= :endDate "
			+ " and ct.transactionSource = :source and ct.isDefaultTransaction = 1 and ct.createdFrom = case when ct.transactionSource = 'Facebook' then ct.createdFrom else :source end order by ct.clientDate desc")
	List<CampaignTransaction> getCampaignTransactionsBetweenDateRange(@Param("campaignId") String campaignId,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("source") CampaignDetailSource campaignDetailSource);

	@Query("select ct from CampaignTransaction ct where ct.transactionId = :transactionId and ct.transactionSource = :source and ct.createdFrom = :createdFrom and ct.campaignId = :campaignId")
	CampaignTransaction getCampaignTransaction(@Param("source") CampaignDetailSource campaignDetailSource, @Param("transactionId") String transactionId, @Param("createdFrom") CreatedFrom createdFrom, @Param("campaignId") String campaignId);
	
	CampaignTransaction findByTransactionId(@Param("transactionId") String transactionId);
	
	@Query("select new com.gr.dm.core.dto.report.MembershipStatsDto(transactionSource, sum(case when isNewMembership = 1 then 1 else 0 end),"
			+ " sum(case when isRenewedMembership = 1 then 1 else 0 end),"
			+ " round(sum(case when isNewMembership = 1 then transactionRevenue else 0 end), 2),"
			+ " round(sum(case when isRenewedMembership = 1 then transactionRevenue else 0 end), 2)) "
			+ " from CampaignTransaction ct"
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate"
			+ " and isDefaultTransaction = 1"
			+ " and createdFrom = case when transactionSource = 'Facebook' then createdFrom else transactionSource end"
			+ " group by ct.transactionSource")
	List<MembershipStatsDto> getMembershipStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	List<CampaignTransactionDto> getDuplicateTransactions(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}