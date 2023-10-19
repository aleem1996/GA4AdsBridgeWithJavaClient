package com.gr.dm.core.dto.report;

import com.gr.dm.core.entity.CampaignDetailSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SourceCostDto {
	private CampaignDetailSource source;
	private Double cost;
}
