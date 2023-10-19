package com.gr.dm.core.dto.crm;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CrmTransaction implements Serializable {

	private static final long serialVersionUID = 1L;
	private String transactionId;
	private Double transactionRevenue;
	private List<CrmTransactionDetail> transactionDetails = null;
	private List<CrmCampaign> campaigns;
	@JsonFormat(pattern = "MM/dd/yyyy", timezone = "US/Eastern")
	private Date serverDateTime;

}