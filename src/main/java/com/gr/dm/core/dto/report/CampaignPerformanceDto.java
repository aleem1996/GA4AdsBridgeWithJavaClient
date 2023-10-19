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
public class CampaignPerformanceDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String source;

	private String bestCampaign;

	private String worstCampaign;

	private Long bestCampaignConversions;

	private Long worstCampaignConversions;

	private Double bestCampaignRevenue;

	private Double worstCampaignRevenue;

	private Double bestCampaignCost;

	private Double worstCampaignCost;

}
