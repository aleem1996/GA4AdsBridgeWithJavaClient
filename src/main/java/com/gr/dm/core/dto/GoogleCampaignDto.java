package com.gr.dm.core.dto;

import java.util.Date;

import com.gr.dm.core.util.Util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Aleem Malik
 */
@Getter
@Setter
@NoArgsConstructor
public class GoogleCampaignDto {

	private Integer id;

	private String name;

	private String campaignId;

	private Long gaTransactionCount;

	private Long adWordsTransactionCount;

	private Double gaRevenue;

	private Double adWordsRevenue;

	private Double gaCost;

	private Double adWordsCost;

	private Date startDate;

	private Date endDate;

	private Long newMembershipCount;

	private Long renewedMembershipCount;

	private Long tiCount;

	private Long totalDeviceCount;

	private Long clicks;

	private Long impressions;
	
	public GoogleCampaignDto(Integer id, String name, String campaignId, Long gaTransactionCount, Long adWordsTransactionCount, Double gaRevenue, Double adWordsRevenue,
			Double gaCost, Double adWordsCost, Date startDate, Date endDate, Long newMembershipCount, Long renewedMembershipCount, Long tiCount, Long totalDeviceCount, Long clicks,
			Long impressions) {
		super();
		this.id = id;
		this.name = name;
		this.campaignId = campaignId;
		this.gaTransactionCount = Util.isNull(gaTransactionCount) ? 0L : gaTransactionCount;
		this.adWordsTransactionCount = adWordsTransactionCount;
		this.gaRevenue = Util.isNull(gaRevenue) ? 0.0d : gaRevenue;
		this.adWordsRevenue = adWordsRevenue;
		this.gaCost = gaCost;
		this.adWordsCost = adWordsCost;
		this.startDate = startDate;
		this.endDate = endDate;
		this.newMembershipCount = newMembershipCount;
		this.renewedMembershipCount = renewedMembershipCount;
		this.tiCount = tiCount;
		this.totalDeviceCount = totalDeviceCount;
		this.clicks = clicks;
		this.impressions = impressions;
	}

}
