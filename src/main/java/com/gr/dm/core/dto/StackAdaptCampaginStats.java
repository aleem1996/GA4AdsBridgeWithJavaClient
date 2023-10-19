package com.gr.dm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StackAdaptCampaginStats {
	
	private String campaignDetailSource;
	
	//private String campaign;

	//private String campaign_id;
	
	private Integer conv;

	private Double conv_rev; //revenue

	private Integer click;

	private Integer imp;

	private Double cost;
	
	private Double roas; //roi
	
	private Double ecpc; //cpc
	
	
}
