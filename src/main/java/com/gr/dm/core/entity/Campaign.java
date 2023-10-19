package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.gr.dm.core.util.Util;

/**
 * @author Aleem Malik
 */
@Entity
@Table(name = "Campaign")
@DynamicInsert
@DynamicUpdate
public class Campaign implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private String campaignId;

	@Enumerated(EnumType.STRING)
	private CampaignSource campaignSource;
	
	private Boolean active = Boolean.TRUE;
	
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<CampaignHistory> campaignHistory = new ArrayList<CampaignHistory>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (Util.isNotNull(name)) {
			this.name = name;
		}
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public CampaignSource getCampaignSource() {
		return campaignSource;
	}

	public void setCampaignSource(CampaignSource campaignSource) {
		this.campaignSource = campaignSource;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		if (Util.isNotNull(active)) {
			this.active = active;
		}
	}

	public List<CampaignHistory> getCampaignHistory() {
		return campaignHistory;
	}

	public void setCampaignHistory(List<CampaignHistory> campaignHistory) {
		this.campaignHistory = campaignHistory;
	}

	@Override
	public String toString() {
		return "Campaign [id=" + id + ", name=" + name + ", campaignId=" + campaignId + "]";
	}	
}