package com.gr.dm.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "AdGroup")
@DynamicInsert
@DynamicUpdate
public class AdGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String adGroupId;

	private String name;

	private String campaignId;

	@Enumerated(EnumType.STRING)
	private CampaignSource adGroupSource;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public CampaignSource getAdGroupSource() {
		return adGroupSource;
	}

	public void setAdGroupSource(CampaignSource adGroupSource) {
		this.adGroupSource = adGroupSource;
	}
}
