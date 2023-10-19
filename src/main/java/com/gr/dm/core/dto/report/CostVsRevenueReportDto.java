package com.gr.dm.core.dto.report;

import com.gr.dm.core.entity.CampaignDetailSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CostVsRevenueReportDto {

	private CampaignDetailSource source;
	private Double cost;
	private Double revenue;
	private String label;
}
