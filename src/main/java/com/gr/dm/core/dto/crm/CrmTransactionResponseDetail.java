package com.gr.dm.core.dto.crm;

import java.io.Serializable;
import java.util.List;

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
public class CrmTransactionResponseDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<CrmTransaction> transactions = null;
}