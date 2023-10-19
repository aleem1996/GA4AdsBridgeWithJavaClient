package com.gr.dm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FbCampaignStatsDto {

	private Long conversions;

	private Double revenue;

	private Long clicks;

	private Long impressions;

	private Double cost;

	private Long newMembershipCount;

	private Long renewdMembershipCount;

	private Long tiCount;

	private Long deviceCount;

	private Long purchases1Day;

	private Long purchases7Day;

	private Long purchases28Day;

	private Double revenue1Day;

	private Double revenue7Day;

	private Double revenue28Day;
	
	private Long purchases1DayView;
	
	private Long purchases7DayClick;
	
	private Double revenue1DayView;
	
	private Double revenue7DayClick;
}
