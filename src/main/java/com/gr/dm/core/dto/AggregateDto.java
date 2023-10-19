package com.gr.dm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AggregateDto {

	public AggregateDto(Long conversions, Double revenue, Long clicks, Long impressions, Double cost) {
		super();
		this.conversions = conversions;
		this.revenue = revenue;
		this.clicks = clicks;
		this.impressions = impressions;
		this.cost = cost;
	}

	private Long conversions;

	private Double revenue;

	private Long clicks;

	private Long impressions;

	private Double cost;

	private Long purchases7Day;

	private Long purchases28Day;

	private Double revenue7Day;

	private Double revenue28Day;
	
	private Long purchases1DayView;
	
	private Long purchases7DayClick;
	
	private Double revenue1DayView;
	
	private Double revenue7DayClick;
}
