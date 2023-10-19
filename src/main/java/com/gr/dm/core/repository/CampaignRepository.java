package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.BingCampaignDto;
import com.gr.dm.core.dto.BingCampaignStatsDto;
import com.gr.dm.core.dto.FacebookCampaignDto;
import com.gr.dm.core.dto.FbCampaignStatsDto;
import com.gr.dm.core.dto.GoogleCampaignDto;
import com.gr.dm.core.dto.StatsDto;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignSource;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface CampaignRepository extends CrudRepository<Campaign, Long> {
	
	Campaign findByCampaignIdAndCampaignSource(String campaignId, CampaignSource campaignSource);
	
	Campaign findByNameAndCampaignSource(String name, CampaignSource campaignSource);
	
	@Query(value = "select cmp.* from Campaign cmp where replace(cmp.name, ' ', '') = :name and cmp.campaignSource = :source", nativeQuery = true)
	Campaign findCampaignExcludingWhiteSpace(@Param("name") String name, @Param("source") String campaignSource);
	
	Campaign findByCampaignId(String campaignId);
	
	List<Campaign> findByCampaignSource(CampaignSource campaignSource);
	
	@Query("select new com.gr.dm.core.dto.GoogleCampaignDto(cm.id, cm.name, cm.campaignId, "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then case when 'partial' = :view then cd.transactionCount else case when 'unique' = :view then (cd.uniqueAssistedConversionCount + cd.directConversionCount) "
			+ " else (cd.assistedConversionCount + cd.directConversionCount) end end end),"
			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.transactionCount else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then case when 'partial' = :view then cd.revenue else case when 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue)"
			+ " else (cd.assistedConversionRevenue + cd.directConversionRevenue) end end end),"
			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.revenue else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then cd.cost else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.cost else 0 end), "
			+ " max(cd.startDate), max(cd.endDate), sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), "
			+ " sum(cd.totalDeviceCount), sum(case when cd.campaignDetailSource = 'Analytics' then cd.clicks else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then cd.impressions else 0 end) )"
			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = 'Google' group by cm.campaignId")
	List<GoogleCampaignDto> getGoogleCampaigns(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view);
	
//	@Query("select new com.gr.dm.core.dto.GoogleCampaignDto(cm.id, cm.name, cm.campaignId, "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then case when 'partial' = :view then cd.transactionCount else case when 'unique' = :view then (cd.uniqueAssistedConversionCount + cd.directConversionCount) "
//			+ " else (cd.assistedConversionCount + cd.directConversionCount) end end end),"
//			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.transactionCount else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then case when 'partial' = :view then cd.revenue else case when 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue)"
//			+ " else (cd.assistedConversionRevenue + cd.directConversionRevenue) end end end),"
//			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.revenue else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then cd.cost else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.cost else 0 end), "
//			+ " max(cd.startDate), max(cd.endDate), sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), "
//			+ " sum(cd.totalDeviceCount), sum(case when cd.campaignDetailSource = 'GA4' then cd.clicks else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then cd.impressions else 0 end) )"
//			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
//			+ " and cd.endDate <= :endDate and cm.campaignSource = :source group by cm.campaignId")
//	List<GoogleCampaignDto> getGoogleGA4Campaigns(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view,  @Param("source") CampaignSource source);
	
	@Query("select new com.gr.dm.core.dto.FacebookCampaignDto(cm.id, cm.name, cm.campaignId,"
			+ " sum(cd.transactionCount),"
			+ " sum(cd.revenue),"
			+ " sum(cd.cost),"
			+ " max(cd.startDate), max(cd.endDate), sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount),"
			+ " sum(cd.totalDeviceCount), sum(cd.clicks), sum(cd.impressions),"
			+ " sum(ca.purchases1DayView + ca.purchases1DayClick), sum(ca.purchases7DayView + ca.purchases7DayClick), sum(ca.purchases28DayView + ca.purchases28DayClick),"
			+ " sum(ca.revenue1DayView + ca.revenue1DayClick), sum(ca.revenue7DayView + ca.revenue7DayClick), sum(ca.revenue28DayView + ca.revenue28DayClick), "
			+ " sum(ca.purchases1DayView), sum(ca.purchases7DayClick), sum(ca.revenue1DayView), sum(ca.revenue7DayClick) )"
			+ " from Campaign cm, CampaignDetail cd, CampaignAttribution ca where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = :source and cd.campaignId = ca.campaignId and cd.startDate = ca.startDate and cd.endDate=ca.endDate and ca.isManual = false group by cm.campaignId")
	List<FacebookCampaignDto> getFacebookCampaigns(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("source") CampaignSource campaignSource);
	
	@Query("select new com.gr.dm.core.dto.BingCampaignDto(cm.id, cm.name, cm.campaignId,"
			+ " sum(case when 'partial' = :view then cd.transactionCount else case when 'unique' = :view then (cd.uniqueAssistedConversionCount + cd.directConversionCount)"
			+ " else (cd.assistedConversionCount + cd.directConversionCount) end end),"
			+ " sum(case when 'partial' = :view then cd.revenue else case when 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue)"
			+ " else (cd.assistedConversionRevenue + cd.directConversionRevenue) end end),"
			+ " sum(cd.cost),"
			+ " max(cd.startDate), max(cd.endDate), sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount),"
			+ " sum(cd.totalDeviceCount), sum(cd.clicks), sum(cd.impressions) )"
			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = 'Bing' group by cm.campaignId")
	List<BingCampaignDto> getBingCampaigns(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view);
	
	@Query("select new com.gr.dm.core.dto.BingCampaignDto(cm.id, cm.name, cm.campaignId,"
			+ " sum(cd.transactionCount),"
			+ " sum(cd.revenue),"
			+ " sum(cd.cost),"
			+ " max(cd.startDate), max(cd.endDate), sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount),"
			+ " sum(cd.totalDeviceCount), sum(cd.clicks), sum(cd.impressions) )"
			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = 'stackadapt' group by cm.campaignId")
	List<BingCampaignDto> getStackAdaptCampaigns(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	
	@Query("select new com.gr.dm.core.dto.StatsDto("
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then case when 'partial' = :view then cd.transactionCount else case when 'unique' = :view then (cd.uniqueAssistedConversionCount + cd.directConversionCount)" 
			+ " else (cd.assistedConversionCount + cd.directConversionCount) end end end),"
			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.transactionCount else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics'  then case when 'partial' = :view then cd.revenue else case when 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue)" 
			+ " else (cd.assistedConversionRevenue + cd.directConversionRevenue) end end end),"
			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.revenue else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then cd.clicks else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then cd.impressions else 0 end), "
			+ " sum(case when cd.campaignDetailSource = 'Analytics' then cd.cost else 0 end), "
			+ " sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), sum(cd.totalDeviceCount),"
			+ " sum(cd.assistedConversionCount + cd.directConversionCount), sum(cd.assistedConversionRevenue + directConversionRevenue) ) "
			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = 'Google' ")
	StatsDto getGoogleStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view);
	
//	@Query("select new com.gr.dm.core.dto.StatsDto("
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then case when 'partial' = :view then cd.transactionCount else case when 'unique' = :view then (cd.uniqueAssistedConversionCount + cd.directConversionCount)" 
//			+ " else (cd.assistedConversionCount + cd.directConversionCount) end end end),"
//			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.transactionCount else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4'  then case when 'partial' = :view then cd.revenue else case when 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue)" 
//			+ " else (cd.assistedConversionRevenue + cd.directConversionRevenue) end end end),"
//			+ " sum(case when cd.campaignDetailSource = 'AdWords' then cd.revenue else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then cd.clicks else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then cd.impressions else 0 end), "
//			+ " sum(case when cd.campaignDetailSource = 'GA4' then cd.cost else 0 end), "
//			+ " sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), sum(cd.totalDeviceCount),"
//			+ " sum(cd.assistedConversionCount + cd.directConversionCount), sum(cd.assistedConversionRevenue + directConversionRevenue) ) "
//			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
//			+ " and cd.endDate <= :endDate and cm.campaignSource = 'GoogleGA4' ")
//	StatsDto getGoogleGA4Stats(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view);
	
	@Query("select new com.gr.dm.core.dto.BingCampaignStatsDto("
			+ " sum(case when 'partial' = :view then cd.transactionCount else case when 'unique' = :view then (cd.uniqueAssistedConversionCount + cd.directConversionCount)"
			+ "else (cd.assistedConversionCount + cd.directConversionCount) end end), "
			+ " sum(case when 'partial' = :view then cd.revenue else case when 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue)"
			+ " else (cd.assistedConversionRevenue + cd.directConversionRevenue) end end), "
			+ " sum(cd.clicks), "
			+ " sum(cd.impressions), "
			+ " sum(cd.cost), "
			+ " sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), sum(cd.totalDeviceCount),"
			+ " sum(cd.assistedConversionCount + cd.directConversionCount), sum(cd.assistedConversionRevenue + directConversionRevenue) ) "
			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = 'Bing' ")
	BingCampaignStatsDto getBingStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view);
	
	@Query("select new com.gr.dm.core.dto.BingCampaignStatsDto("
			+ " sum(cd.transactionCount), "
			+ " sum(cd.revenue), "
			+ " sum(cd.clicks), "
			+ " sum(cd.impressions), "
			+ " sum(cd.cost), "
			+ " sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), sum(cd.totalDeviceCount),"
			+ " sum(cd.assistedConversionCount + cd.directConversionCount), sum(cd.assistedConversionRevenue + directConversionRevenue) ) "
			+ " from Campaign cm, CampaignDetail cd where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = 'stackadapt' ")
	BingCampaignStatsDto getStackAdaptStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("select new com.gr.dm.core.dto.FbCampaignStatsDto(sum(cd.transactionCount),"
			+ " sum(cd.revenue), "
			+ " sum(cd.clicks), "
			+ " sum(cd.impressions), "
			+ " sum(cd.cost), "
			+ " sum(cd.newMembershipCount), sum(cd.renewedMembershipCount), sum(cd.tiCount), sum(cd.totalDeviceCount), "
			+ " sum(ca.purchases1DayView + ca.purchases1DayClick), sum(ca.purchases7DayView + ca.purchases7DayClick), sum(ca.purchases28DayView + ca.purchases28DayClick),"
			+ " sum(ca.revenue1DayView + ca.revenue1DayClick), sum(ca.revenue7DayView + ca.revenue7DayClick), sum(ca.revenue28DayView + ca.revenue28DayClick),"
			+ " sum(ca.purchases1DayView), sum(ca.purchases7DayClick), sum(ca.revenue1DayView), sum(ca.revenue7DayClick) )"
			+ " from Campaign cm, CampaignDetail cd, CampaignAttribution ca where cm.campaignId = cd.campaignId and cd.startDate >= :startDate"
			+ " and cd.endDate <= :endDate and cm.campaignSource = :source and cd.campaignId = ca.campaignId and cd.startDate = ca.startDate and cd.endDate=ca.endDate and ca.isManual = false")
	FbCampaignStatsDto getFbStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("source") CampaignSource campaignSource);
	
}