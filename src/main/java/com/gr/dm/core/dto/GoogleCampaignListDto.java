package com.gr.dm.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Aleem Malik
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCampaignListDto {

	private List<GoogleCampaignDto> campaigns;

	private StatsDto stats;

}
