package com.gr.dm.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.dto.report.CampaignPerformanceDto;
import com.gr.dm.core.dto.report.CampaignSummaryDto;
import com.gr.dm.core.dto.report.LatestDateDto;
import com.gr.dm.core.dto.report.CostVsRevenueReportDto;
import com.gr.dm.core.dto.report.GenericReportDto;
import com.gr.dm.core.dto.report.SourceCostDto;
import com.gr.dm.core.dto.report.WeeklyCostDto;
import com.gr.dm.core.dto.report.WeeklyRevenueDto;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface CampaignDetailRepository extends CrudRepository<CampaignDetail, Long> {

	@Query("select cd from CampaignDetail cd where cd.campaignId = :campaignId and cd.campaignDetailSource = :campaignDetailSource and cd.startDate = :startDate and cd.endDate = :endDate")
	CampaignDetail getCampaignDetail(@Param("campaignId") String campaignId, @Param("campaignDetailSource") CampaignDetailSource campaignDetailSource, 
			@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	List<CampaignDetail> findByCampaignId(String campaignId);
	
	@Query(value = "select cd from CampaignDetail cd where cd.campaignId = :campaignId and cd.startDate >= :startDate and cd.endDate <= :endDate", nativeQuery=true)
	List<CampaignDetail> getCampaignDetailsBetweenDateRange(@Param("campaignId") String campaignId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query("select new com.gr.dm.core.dto.report.LatestDateDto(date_format(max(cd.endDate), '%m/%d/%Y'), date_format(max(cd.lastUpdated), '%m/%d/%Y %h:%i %p')) from CampaignDetail cd where cd.campaignDetailSource = :campaignDetailSource and cd.isDataSynced = true")
	LatestDateDto getDateOfLastFetchedData(@Param("campaignDetailSource") CampaignDetailSource campaignDetailSource);
	
	@Query(nativeQuery = true)
	List<CampaignSummaryDto> getCampaignSummary(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("statType") String statType);
	
	List<CampaignSummaryDto> getCampaignSummaryByClick(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Query(nativeQuery = true)
	List<CampaignPerformanceDto> getCampaignPerformanceReport(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("view") String view);
	
	List<WeeklyCostDto> getCostByWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	List<WeeklyRevenueDto> getRevenueByWeek(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
	
	@Procedure(name = "updateAssistedConversionsData")
	void updateAssistedConversionsData();
	
	@Query("SELECT new com.gr.dm.core.dto.report.SourceCostDto(cd.campaignDetailSource, sum(cd.cost))"
			+ " FROM CampaignDetail cd WHERE"
			+ " cd.campaignDetailSource is not null"
			+ " AND cd.startDate >= :startDate"
			+ " AND cd.endDate <= :endDate"
			+ " AND cd.campaignDetailSource IN :campaignDetailSources"
			+ " GROUP BY cd.campaignDetailSource")
	public List<SourceCostDto> getCost(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources")CampaignDetailSource... campaignDetailSources);
	
	@Query("SELECT new com.gr.dm.core.dto.report.CostVsRevenueReportDto(cd.campaignDetailSource as source, round(sum(cd.cost),2) as cost, "
			+ "round(sum(case "
			+ "when :statType IN ('purchase_centric', 'weekly_stats') "
			+ "and cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) "
			+ "else case when 'partial' = :view  then cd.revenue "
			+ "else case when cd.campaignDetailSource IN ('Analytics', 'Bing', 'StackAdapt') "
			+ "then case when :statType IN ('purchase_centric', 'weekly_stats') or 'unique' = :view "
			+ "then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) "
			+ "else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end), 2) as revenue "
			+ ", DATE_FORMAT(cd.startDate, '%b-%y') as label) "
			+ "FROM CampaignDetail cd LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId "
			+ "and cd.startDate = ca.startDate and cd.endDate = ca.endDate "
			+ "and ca.isManual = false where cd.startDate >= :startDate  and cd.endDate <= :endDate  "
			+ "and cd.campaignDetailSource IN :campaignDetailSources "
			+ "group by cd.campaignDetailSource, year(cd.startDate), month(cd.startDate) "
			+ "order by cd.campaignDetailSource")
	public List<CostVsRevenueReportDto> getCostVsRevenueMonthly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<CampaignDetailSource> campaignDetailSources);
	
	@Query(value ="SELECT new com.gr.dm.core.dto.report.CostVsRevenueReportDto(cd.campaignDetailSource as source, "
			+ "round(sum(cd.cost),2) as cost, "
			+ "round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and "
			+ "cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) "
			+ "else case when 'partial' = :view  then cd.revenue else case when cd.campaignDetailSource IN "
			+ "('Analytics', 'Bing', 'StackAdapt') then "
			+ "case when :statType IN ('purchase_centric', 'weekly_stats') or 'unique' = :view then "
			+ "(cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) else "
			+ "(cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end), 2) as revenue, "
		    + "CONCAT(DATE_FORMAT(CASE WHEN :startDate > ADDDATE(ADDDATE(cd.startDate, -WEEKDAY(cd.startDate) - 1), 1) THEN :startDate ELSE ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 1) END, '%m/%d/%Y'), '-', DATE_FORMAT(CASE WHEN :endDate < ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 7) THEN :endDate ELSE ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 7 ) END,'%m/%d/%Y')) as label)"
		    + " FROM CampaignDetail cd "
			+ "LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and "
			+ "cd.startDate = ca.startDate and cd.endDate = ca.endDate "
			+ "where cd.startDate >= :startDate  and cd.endDate <= :endDate AND "
			+ "cd.campaignDetailSource IN :campaignDetailSources group by cd.campaignDetailSource, label "
			+ "order by cd.startDate, cd.campaignDetailSource")
	public List<CostVsRevenueReportDto> getCostVsRevenueWeekly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<CampaignDetailSource> campaignDetailSources);
	
	@Query("SELECT new com.gr.dm.core.dto.report.CostVsRevenueReportDto(cd.campaignDetailSource, "
			+ "round(sum(cd.cost),2) as cost, "
			+ "round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and cd.campaignDetailSource = 'Facebook' then "
			+ "(ca.revenue1DayView + ca.revenue28DayClick) else case when 'partial' = :view  "
			+ "then cd.revenue else case when cd.campaignDetailSource IN "
			+ "('Analytics', 'Bing', 'StackAdapt') then case when :statType IN "
			+ "('purchase_centric', 'weekly_stats') or 'unique' = :view then "
			+ "(cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) "
			+ "else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end), 2) as revenue, "
			+ "DATE_FORMAT(cd.startDate, '%Y') as label ) "
			+ "FROM CampaignDetail cd "
			+ "LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and cd.startDate = ca.startDate and "
			+ "cd.endDate = ca.endDate and ca.isManual = false where cd.startDate >= :startDate  and cd.endDate <= :endDate  and "
			+ "cd.campaignDetailSource IN :campaignDetailSources "
			+ "group by cd.campaignDetailSource, year(cd.startDate) "
			+ "order by cd.campaignDetailSource")
	public List<CostVsRevenueReportDto> getCostVsRevenueYearly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<CampaignDetailSource> campaignDetailSources);

	@Query(value = "SELECT ifNull(cd.campaignDetailSource, 'All') as source, " + 
			"round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') " + 
			"and cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) " + 
			"else case when 'partial' = :view  then cd.revenue else case when cd.campaignDetailSource " + 
			"IN ('Analytics', 'Bing', 'StackAdapt') then case when :statType IN ('purchase_centric', 'weekly_stats') " + 
			"or 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) " + 
			"else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end) - sum(cd.cost), 2) as val, " + 
			"DATE_FORMAT(cd.startDate, '%b-%y') as label " + 
			"from CampaignDetail cd " + 
			"LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and " + 
			"cd.startDate = ca.startDate and cd.endDate = ca.endDate and ca.isManual = false " + 
			"where cd.startDate >= :startDate and cd.endDate <= :endDate and cd.campaignDetailSource IN :campaignDetailSources " + 
			"group by year(cd.startDate), month(cd.startDate), label, cd.campaignDetailSource with ROLLUP "
			+ "having label is not null;", nativeQuery=true)
	public List<GenericReportDto> getProfitLossMonthly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<String> campaignDetailSources);
	
	@Query(value = "SELECT ifNull(cd.campaignDetailSource, 'All') as source, " + 
			"round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') " + 
			"and cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) " + 
			"else case when 'partial' = :view  then cd.revenue else case when cd.campaignDetailSource " + 
			"IN ('Analytics', 'Bing', 'StackAdapt') then case when :statType IN ('purchase_centric', 'weekly_stats') " + 
			"or 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) " + 
			"else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end) - sum(cd.cost), 2) as val, " + 
		    "CONCAT(DATE_FORMAT(CASE WHEN :startDate > ADDDATE(ADDDATE(cd.startDate, -WEEKDAY(cd.startDate) - 1), 1) THEN :startDate ELSE ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 1) END, '%m/%d/%Y'), '-', DATE_FORMAT(CASE WHEN :endDate < ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 7) THEN :endDate ELSE ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 7 ) END,'%m/%d/%Y')) as label" +
			" from CampaignDetail cd " + 
			"LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and " + 
			"cd.startDate = ca.startDate and cd.endDate = ca.endDate and ca.isManual = false " + 
			"where cd.startDate >= :startDate and cd.endDate <= :endDate and cd.campaignDetailSource IN :campaignDetailSources " + 
			"group by label, cd.campaignDetailSource with ROLLUP "
			+ "having label is not null;", nativeQuery=true)
	public List<GenericReportDto> getProfitLossWeekly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<String> campaignDetailSources);
	
	@Query(value = "SELECT ifNull(cd.campaignDetailSource, 'All') as source, " + 
			"round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') " + 
			"and cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) " + 
			"else case when 'partial' = :view  then cd.revenue else case when cd.campaignDetailSource " + 
			"IN ('Analytics', 'Bing', 'StackAdapt') then case when :statType IN ('purchase_centric', 'weekly_stats') " + 
			"or 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) " + 
			"else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end) - sum(cd.cost), 2) as val, " + 
			"DATE_FORMAT(cd.startDate, '%Y') as label " + 
			"from CampaignDetail cd " + 
			"LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and " + 
			"cd.startDate = ca.startDate and cd.endDate = ca.endDate and ca.isManual = false " + 
			"where cd.startDate >= :startDate and cd.endDate <= :endDate and cd.campaignDetailSource IN :campaignDetailSources " + 
			"group by year(cd.startDate), label, cd.campaignDetailSource with ROLLUP "
			+ "having label is not null;", nativeQuery=true)
	public List<GenericReportDto> getProfitLossYearly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<String> campaignDetailSources);

	@Query(value = "SELECT ifNull(cd.campaignDetailSource, 'All') as source, ifNull(round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and "
			+ "cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) "
			+ "else case when 'partial' = :view  then cd.revenue else case when "
			+ "cd.campaignDetailSource IN ('Analytics', 'Bing', 'StackAdapt') then case when :statType IN ('purchase_centric', 'weekly_stats') "
			+ "or 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) "
			+ "else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end) / sum(cd.cost), 2), 0) as val, "
			+ "DATE_FORMAT(cd.startDate, '%b-%y') as label "
			+ "from CampaignDetail cd "
			+ "LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and cd.startDate = ca.startDate "
			+ "and cd.endDate = ca.endDate and ca.isManual = false where cd.startDate >= :startDate and cd.endDate <= :endDate "
			+ "and cd.campaignDetailSource IN :campaignDetailSources "
			+ "group by year(cd.startDate), month(cd.startDate), label, cd.campaignDetailSource with ROLLUP "
			+ "having label is not null;", nativeQuery = true)
	public List<GenericReportDto> getRoiMonthly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<String> campaignDetailSources);
	
	@Query(value = "SELECT ifNull(cd.campaignDetailSource, 'All') as source, ifNull(round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and "
			+ "cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) "
			+ "else case when 'partial' = :view  then cd.revenue else case when "
			+ "cd.campaignDetailSource IN ('Analytics', 'Bing', 'StackAdapt') then case when :statType IN ('purchase_centric', 'weekly_stats') "
			+ "or 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) "
			+ "else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end) / sum(cd.cost), 2), 0) as val, "
		    + "CONCAT(DATE_FORMAT(CASE WHEN :startDate > ADDDATE(ADDDATE(cd.startDate, -WEEKDAY(cd.startDate) - 1), 1) THEN :startDate ELSE ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 1) END, '%m/%d/%Y'), '-', DATE_FORMAT(CASE WHEN :endDate < ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 7) THEN :endDate ELSE ADDDATE(ADDDATE(cd.startDate, - WEEKDAY(cd.startDate) - 1), 7 ) END,'%m/%d/%Y')) as label "
			+ "from CampaignDetail cd "
			+ "LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and cd.startDate = ca.startDate "
			+ "and cd.endDate = ca.endDate and ca.isManual = false where cd.startDate >= :startDate and cd.endDate <= :endDate "
			+ "and cd.campaignDetailSource IN :campaignDetailSources "
			+ "group by label, cd.campaignDetailSource with ROLLUP "
			+ "having label is not null;", nativeQuery = true)
	public List<GenericReportDto> getRoiWeekly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<String> campaignDetailSources);
	
	@Query(value = "SELECT ifNull(cd.campaignDetailSource, 'All') as source, ifNull(round(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and "
			+ "cd.campaignDetailSource = 'Facebook' then (ca.revenue1DayView + ca.revenue28DayClick) "
			+ "else case when 'partial' = :view  then cd.revenue else case when "
			+ "cd.campaignDetailSource IN ('Analytics', 'Bing', 'StackAdapt') then case when :statType IN ('purchase_centric', 'weekly_stats') "
			+ "or 'unique' = :view then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) "
			+ "else (cd.assistedConversionRevenue + cd.directConversionRevenue) end else cd.revenue end end end) / sum(cd.cost), 2), 0) as val, "
			+ "DATE_FORMAT(cd.startDate, '%Y') as label "
			+ "from CampaignDetail cd "
			+ "LEFT JOIN CampaignAttribution ca on cd.campaignId = ca.campaignId and cd.startDate = ca.startDate "
			+ "and cd.endDate = ca.endDate and ca.isManual = false where cd.startDate >= :startDate and cd.endDate <= :endDate "
			+ "and cd.campaignDetailSource IN :campaignDetailSources "
			+ "group by year(cd.startDate), label, cd.campaignDetailSource with ROLLUP "
			+ "having label is not null;", nativeQuery=true)
	public List<GenericReportDto> getRoiYearly(@Param("statType") String statType, @Param("view") String view, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("campaignDetailSources") List<String> campaignDetailSources);

}