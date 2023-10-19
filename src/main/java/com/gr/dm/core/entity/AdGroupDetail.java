package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.gr.dm.core.util.Util;

@Entity
@Table(name = "AdGroupDetail")
@DynamicInsert
@DynamicUpdate
public class AdGroupDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String adGroupId;

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

	public String getAdGroupId() {
		return adGroupId;
	}

	public void setAdGroupId(String adGroupId) {
		this.adGroupId = adGroupId;
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
