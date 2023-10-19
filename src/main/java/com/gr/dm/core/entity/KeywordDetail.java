package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedNativeQuery;

import com.gr.dm.core.dto.report.KeywordPerformanceDto;
import com.gr.dm.core.util.NativeQueries;
import com.gr.dm.core.util.Util;

@Entity
@Table(name = "KeywordDetail")
@DynamicInsert
@DynamicUpdate
@SqlResultSetMapping(name = "KeywordDetail.keywordPerformanceReportMapping", classes = {
		@ConstructorResult(targetClass = KeywordPerformanceDto.class, columns = {
				@ColumnResult(name = "Source", type = String.class),
				@ColumnResult(name = "Best Keyword", type = String.class),
				@ColumnResult(name = "Worst Keyword", type = String.class),
				@ColumnResult(name = "Best Keyword Conversion Count", type = Long.class),
				@ColumnResult(name = "Worst Keyword Conversion Count", type = Long.class),
				@ColumnResult(name = "Best Keyword Revenue", type = Double.class),
				@ColumnResult(name = "Worst Keyword Revenue", type = Double.class),
				@ColumnResult(name = "Best Keyword Cost", type = Double.class),
				@ColumnResult(name = "Worst Keyword Cost", type = Double.class), }) })
@NamedNativeQuery(name = "KeywordDetail.getKeywordPerformanceReport", query = NativeQueries.KEYWORD_PERFORMANCE_REPORT, resultSetMapping = "KeywordDetail.keywordPerformanceReportMapping")
public class KeywordDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String keywordId;

	private Integer transactionCount = 0;

	private Double revenue = 0.0;

	private Double cost = 0.0;

	private Date startDate;

	private Date endDate;

	private Integer clicks = 0;

	private Integer impressions = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKeywordId() {
		return keywordId;
	}

	public void setKeywordId(String keywordId) {
		this.keywordId = keywordId;
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

}
