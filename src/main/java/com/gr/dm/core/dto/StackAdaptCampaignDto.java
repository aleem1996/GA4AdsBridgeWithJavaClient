package com.gr.dm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StackAdaptCampaignDto {
	private Integer id;
	
	private Integer total_campaigns;

	private StackAdaptDataDto[] data;
}
