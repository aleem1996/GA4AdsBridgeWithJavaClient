package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import com.gr.dm.core.dto.report.CampaignPerformanceDto;
import com.gr.dm.core.dto.report.CampaignSummaryDto;
import com.gr.dm.core.dto.report.WeeklyCostDto;
import com.gr.dm.core.dto.report.WeeklyRevenueDto;
import com.gr.dm.core.util.NativeQueries;
import com.gr.dm.core.util.Util;

/**
 * @author Aleem Malik
 */
@Entity
@Table(name = "CampaignDetail")
@DynamicInsert
@DynamicUpdate

@SqlResultSetMappings({ @SqlResultSetMapping(name = "CampaignDetail.campaignPerformanceReportMapping", classes = {
		@ConstructorResult(targetClass = CampaignPerformanceDto.class, columns = {
				@ColumnResult(name = "Source", type = String.class),
				@ColumnResult(name = "Best Campaign", type = String.class),
				@ColumnResult(name = "Worst Campaign", type = String.class),
				@ColumnResult(name = "Best Campaign Conversion Count", type = Long.class),
				@ColumnResult(name = "Worst Campaign Conversion Count", type = Long.class),
				@ColumnResult(name = "Best Campaign Revenue", type = Double.class),
				@ColumnResult(name = "Worst Campaign Revenue", type = Double.class),
				@ColumnResult(name = "Best Campaign Cost", type = Double.class),
				@ColumnResult(name = "Worst Campaign Cost", type = Double.class), }) }),

		@SqlResultSetMapping(name = "CampaignDetail.weeklyCostMapping", classes = {
				@ConstructorResult(targetClass = WeeklyCostDto.class, columns = {
						@ColumnResult(name = "interval", type = String.class),
						@ColumnResult(name = "facebook_cost", type = Double.class),
						@ColumnResult(name = "adwords_cost", type = Double.class),
						@ColumnResult(name = "bing_cost", type = Double.class),
						@ColumnResult(name = "total_cost", type = Double.class), }) }),

		@SqlResultSetMapping(name = "CampaignDetail.weeklyRevenueMapping", classes = {
				@ConstructorResult(targetClass = WeeklyRevenueDto.class, columns = {
						@ColumnResult(name = "interval", type = String.class),
						@ColumnResult(name = "total_revenue", type = Double.class),
						@ColumnResult(name = "weekly_new_revenue", type = Double.class),
						@ColumnResult(name = "28_day_attribution", type = Double.class),
						@ColumnResult(name = "28_day_ga_bing_revenue", type = Double.class),
						@ColumnResult(name = "28_day_fb_attribution", type = Double.class),
						@ColumnResult(name = "ga_bing_assist_revenue", type = Double.class),
						@ColumnResult(name = "ga_bing_direct_revenue", type = Double.class),
						@ColumnResult(name = "fb_28_day_view_revenue", type = Double.class),
						@ColumnResult(name = "fb_28_day_click_revenue", type = Double.class),
						@ColumnResult(name = "ga_assist_revenue", type = Double.class),
						@ColumnResult(name = "ga_direct_revenue", type = Double.class),
						@ColumnResult(name = "bing_assist_revenue", type = Double.class),
						@ColumnResult(name = "bing_direct_revenue", type = Double.class),}) }),
		
		@SqlResultSetMapping(name = "CampaignDetail.campaignSummaryMapping", classes = {
				@ConstructorResult(targetClass = CampaignSummaryDto.class, columns = {
						@ColumnResult(name = "source", type = String.class),
						@ColumnResult(name = "cost", type = Double.class),
						@ColumnResult(name = "revenue", type = Double.class),
						@ColumnResult(name = "cpc", type = Double.class),
						@ColumnResult(name = "clicks", type = Long.class),
						@ColumnResult(name = "roi", type = Double.class),
						@ColumnResult(name = "impressions", type = Long.class),
						@ColumnResult(name = "total_campaigns", type = Long.class),
						@ColumnResult(name = "conversions", type = Long.class),
						@ColumnResult(name = "new_memberships", type = Long.class),
						@ColumnResult(name = "renewed_memberships", type = Long.class),
						@ColumnResult(name = "ti_count", type = Long.class),
						@ColumnResult(name = "device_count", type = Long.class),
						@ColumnResult(name = "new_revenue", type = Double.class),
						@ColumnResult(name = "renew_revenue", type = Double.class),
						@ColumnResult(name = "ti_revenue", type = Double.class),
						@ColumnResult(name = "assisted_conversions", type = Long.class),
						@ColumnResult(name = "assisted_revenue", type = Double.class),
						@ColumnResult(name = "unique_assisted_conversions", type = Long.class),
						@ColumnResult(name = "unique_assisted_revenue", type = Double.class)}) })})


@NamedNativeQueries({
		@NamedNativeQuery(name = "CampaignDetail.getCampaignPerformanceReport", query = NativeQueries.CAMPAIGN_PERFORMANCE_REPORT, resultSetMapping = "CampaignDetail.campaignPerformanceReportMapping"),

		@NamedNativeQuery(name = "CampaignDetail.getCostByWeek", query = NativeQueries.COST_BY_WEEK, resultSetMapping = "CampaignDetail.weeklyCostMapping"),

		@NamedNativeQuery(name = "CampaignDetail.getRevenueByWeek", query = NativeQueries.REVENUE_BY_WEEK, resultSetMapping = "CampaignDetail.weeklyRevenueMapping"),
		
		@NamedNativeQuery(name = "CampaignDetail.getCampaignSummary", query = NativeQueries.CAMPAIGN_SUMMARY, resultSetMapping = "CampaignDetail.campaignSummaryMapping"),
		
		@NamedNativeQuery(name = "CampaignDetail.getCampaignSummaryByClick", query = NativeQueries.CAMPAIGN_SUMMARY_BY_CLICK, resultSetMapping = "CampaignDetail.campaignSummaryMapping")})

@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(name = "updateAssistedConversionsData",
                                procedureName = "update_assisted_conversions_data")
})
public class CampaignDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String campaignId;

	private Integer transactionCount = 0;

	private Double revenue = 0.0;

	private Double cost = 0.0;

	private Date startDate;

	private Date endDate;

	private Integer clicks = 0;

	private Integer impressions = 0;

	@ColumnDefault("'0'")
	private Integer newMembershipCount = 0;

	@ColumnDefault("'0'")
	private Integer renewedMembershipCount = 0;

	@ColumnDefault("'0'")
	private Integer tiCount = 0;

	@ColumnDefault("'0'")
	private Integer totalDeviceCount = 0;

	@Enumerated(EnumType.STRING)
	private CampaignDetailSource campaignDetailSource;
	
	private Integer grTransactionCount = 0;
	
	private Double grRevenue = 0.0;
	
	private Boolean isDataSynced = false;
	
	private Date lastUpdated;
	
	private Integer assistedConversionCount = 0;
	
	private Integer directConversionCount = 0;
	
	private Double assistedConversionRevenue = 0.0;
	
	private Double directConversionRevenue = 0.0;
	
	private Integer uniqueAssistedConversionCount = 0;
	
	private Double uniqueAssistedConversionRevenue = 0.0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(Integer transactionCount) {
		if (Util.isNotNull(transactionCount)) {
			this.transactionCount = transactionCount;
		}
	}

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		if (Util.isNotNull(revenue)) {
			this.revenue = revenue;
		}
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		if (Util.isNotNull(cost)) {
			this.cost = cost;
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getNewMembershipCount() {
		return newMembershipCount;
	}

	public void setNewMembershipCount(Integer newMembershipCount) {
		this.newMembershipCount = newMembershipCount;
	}

	public Integer getRenewedMembershipCount() {
		return renewedMembershipCount;
	}

	public void setRenewedMembershipCount(Integer renewedMembershipCount) {
		this.renewedMembershipCount = renewedMembershipCount;
	}

	public Integer getTiCount() {
		return tiCount;
	}

	public void setTiCount(Integer tiCount) {
		this.tiCount = tiCount;
	}

	public Integer getTotalDeviceCount() {
		return totalDeviceCount;
	}

	public void setTotalDeviceCount(Integer totalDeviceCount) {
		this.totalDeviceCount = totalDeviceCount;
	}

	public CampaignDetailSource getCampaignDetailSource() {
		return campaignDetailSource;
	}

	public void setCampaignDetailSource(CampaignDetailSource campaignDetailSource) {
		this.campaignDetailSource = campaignDetailSource;
	}

	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		if (Util.isNotNull(clicks)) {
			this.clicks = clicks;
		}
	}

	public Integer getImpressions() {
		return impressions;
	}

	public void setImpressions(Integer impressions) {
		if (Util.isNotNull(impressions)) {
			this.impressions = impressions;
		}
	}

	public Integer getGrTransactionCount() {
		return grTransactionCount;
	}

	public void setGrTransactionCount(Integer grTransactionCount) {
		if (Util.isNotNull(grTransactionCount)) {
			this.grTransactionCount = grTransactionCount;
		}
	}

	public Double getGrRevenue() {
		return grRevenue;
	}

	public void setGrRevenue(Double grRevenue) {
		if (Util.isNotNull(grRevenue)) {
			this.grRevenue = grRevenue;
		}
	}

	public Boolean getIsDataSynced() {
		return isDataSynced;
	}

	public void setIsDataSynced(Boolean isDataSynced) {
		if (Util.isNotNull(isDataSynced)) {
			this.isDataSynced = isDataSynced;
		}
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		if (Util.isNotNull(lastUpdated)) {
			this.lastUpdated = lastUpdated;
		}
	}

	public Integer getAssistedConversionCount() {
		return assistedConversionCount;
	}

	public void setAssistedConversionCount(Integer assistedConversionCount) {
		this.assistedConversionCount = assistedConversionCount;
	}

	public Integer getDirectConversionCount() {
		return directConversionCount;
	}

	public void setDirectConversionCount(Integer directConversionCount) {
		this.directConversionCount = directConversionCount;
	}

	public Double getAssistedConversionRevenue() {
		return assistedConversionRevenue;
	}

	public void setAssistedConversionRevenue(Double assistedConversionRevenue) {
		this.assistedConversionRevenue = assistedConversionRevenue;
	}

	public Double getDirectConversionRevenue() {
		return directConversionRevenue;
	}

	public void setDirectConversionRevenue(Double directConversionRevenue) {
		this.directConversionRevenue = directConversionRevenue;
	}

	public Integer getUniqueAssistedConversionCount() {
		return uniqueAssistedConversionCount;
	}

	public void setUniqueAssistedConversionCount(Integer uniqueAssistedConversionCount) {
		this.uniqueAssistedConversionCount = uniqueAssistedConversionCount;
	}

	public Double getUniqueAssistedConversionRevenue() {
		return uniqueAssistedConversionRevenue;
	}

	public void setUniqueAssistedConversionRevenue(Double uniqueAssistedConversionRevenue) {
		this.uniqueAssistedConversionRevenue = uniqueAssistedConversionRevenue;
	}

}