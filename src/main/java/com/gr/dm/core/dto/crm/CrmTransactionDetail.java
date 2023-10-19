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
public class CrmTransactionDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	private String transactionId;
	private String packageGuid;
	private String productName;
	private String productCategory;
	private Integer quantity;
	private Double cost;

}