package com.gr.dm.core.dto.report;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSummaryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String campaignDetailSource;

	private Double cost;

	private Double revenue;

	private Double cpc;

	private Long clicks;

	private Double roi;

	private Long impressions;

	private Long totalCampaigns;

	private Long conversions;

	private Long newMembershipCount;

	private Long renewedMembershipCount;

	private Long tiCount;

	private Long totalDeviceCount;

	private Double newMembershipRevenue;

	private Double renewedMembershipRevenue;

	private Double tiRevenue;

	private Long assistedConversions;

	private Double assistedConversionRevenue;

	private Long uniqueAssistedConversions;

	private Double uniqueAssistedConversionRevenue;

}
