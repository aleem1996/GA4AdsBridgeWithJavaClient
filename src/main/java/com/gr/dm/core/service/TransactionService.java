package com.gr.dm.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.dm.core.dto.CampaignTransactionDto;
import com.gr.dm.core.dto.report.MembershipStatsDto;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.repository.CampaignTransactionRepository;
import com.gr.dm.core.repository.TransactionDetailRepository;
import com.gr.dm.core.util.Util;

/**
 * @author ufarooq
 */
@Service
public class TransactionService {

	@Autowired
	CampaignTransactionRepository campaignTransactionRepository;
	
	@Autowired
	TransactionDetailRepository transactionDetailRepository;
	
	@Autowired
	CampaignService campaignService;

	public CampaignTransaction getSavedTransaction(CampaignTransaction campaignTransaction) {
		return campaignTransactionRepository.getCampaignTransaction(campaignTransaction.getTransactionSource(), campaignTransaction.getTransactionId(), campaignTransaction.getCreatedFrom(), campaignTransaction.getCampaignId());
	}
	
	public void saveTransaction(List<CampaignTransaction> campaignTransactions) {
		for (CampaignTransaction campaignTransaction : campaignTransactions) {
			saveTransaction(campaignTransaction);
		}
	}
	
	public void saveTransaction(CampaignTransaction campaignTransaction) {
		CampaignTransaction transaction = getSavedTransaction(campaignTransaction);
		if (Util.isNull(transaction)) {
			campaignService.insertDummyRecordInCampaignDetail(campaignTransaction.getCampaignId(),
					campaignTransaction.getTransactionSource(), campaignTransaction.getStartDate(),
					campaignTransaction.getEndDate());
			campaignTransactionRepository.save(campaignTransaction);
		}
	}
	
	public void saveTransactionDetail(List<TransactionDetail> transactionDetails) {
		for (TransactionDetail transactionDetail : transactionDetails) {
			TransactionDetail detail = transactionDetailRepository.getTransactionDetail(transactionDetail.getTransactionId(), transactionDetail.getProductCategory());
			if(Util.isNull(detail)) {
				saveTransactionDetail(transactionDetail);
			}
		}
	}
	
	public List<MembershipStatsDto> getMembershipStats(Date startDate, Date endDate) {
		return campaignTransactionRepository.getMembershipStats(startDate, endDate);
	}
	
	public void saveTransactionDetail(TransactionDetail transactionDetail) {
		transactionDetailRepository.save(transactionDetail);
	}

	public void deleteTransactionsData() {
		campaignTransactionRepository.deleteAll();
		transactionDetailRepository.deleteAll();
	}

	public List<TransactionDetail> getTransactionsDetail(String transactionId) {
		return transactionDetailRepository.getTransactionDetailByTransactionId(transactionId);
	}
	
	public List<CampaignTransactionDto> getDuplicateTransactions(Date startDate, Date endDate) {
		return campaignTransactionRepository.getDuplicateTransactions(startDate, endDate);
	}
}
