package com.gr.dm.core.dto;

import java.util.ArrayList;
import java.util.List;

import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.McCode;
import com.gr.dm.core.entity.TransactionDetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrTransaction {

	private List<McCode> campaignCodes = new ArrayList<>();

	private CampaignTransaction campaignTransaction;

	private List<TransactionDetail> transactionDetails;

}
