package com.gr.dm.core.dto.crm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmCampaign implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String campaignId;
	private Integer transactionCount;
	private Double revenue;
	private Double cost;
	private String campaignSource;
}
