package com.gr.dm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BingCampaignStatsDto {

	private Long conversions;

	private Double revenue;

	private Long clicks;

	private Long impressions;

	private Double cost;

	private Long newMembershipCount;

	private Long renewdMembershipCount;

	private Long tiCount;

	private Long deviceCount;

	private Long assistedConversions;

	private Double assistedConversionRevenue;

}
