package com.gr.dm.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsMapper;
import com.gr.dm.core.dto.GrTransaction;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignAttribution;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.CreatedFrom;
import com.gr.dm.core.entity.McCode;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

@Service
public class GrService {

	@Autowired
	TransactionService transactionService;

	@Autowired
	CampaignService campaignService;

	public void saveTransaction(GrTransaction grTransaction) {
		CampaignTransaction campaignTransaction = grTransaction.getCampaignTransaction();
		List<TransactionDetail> transactionDetailList = grTransaction.getTransactionDetails();
		List<McCode> mcCodesList = grTransaction.getCampaignCodes();
		boolean shouldSaveTransactionDetails = false;

		for (TransactionDetail transactionDetail : transactionDetailList) {
			GoogleAnalyticsMapper.extractInfoFromProductCategory(transactionDetail.getProductCategory(),
					campaignTransaction, transactionDetail.getQuantity());
		}

		GoogleAnalyticsMapper.extractInfoFromTransactionId(campaignTransaction.getTransactionId(), campaignTransaction);
		campaignTransaction.setCreatedFrom(CreatedFrom.SS);

		for (McCode mcCode : mcCodesList) {
			Date creationDate = mcCode.getLastUpdated();
			//remove the time part from date:
			creationDate = DateUtil.trimDate(creationDate);
			Long attributionDays = DateUtil.getDaysBetween(creationDate, new Date());

			if (attributionDays > mcCode.getExpiryDays()) {
				continue;
			}

			Campaign campaign = campaignService.findCampaign(mcCode.getCode());
			if (Util.isNotNull(campaign)) {

				//TODO: remove this condition later:
				if(!CampaignSource.Facebook.equals(campaign.getCampaignSource())) {
					continue;
				}
				
				CampaignDetailSource campaignDetailSource = null;
				if (CampaignSource.Google.equals(campaign.getCampaignSource())) {
					campaignDetailSource = CampaignDetailSource.Analytics;
				} else {
					campaignDetailSource = CampaignDetailSource.fromValue(campaign.getCampaignSource().getValue());
				}
				
				insertDummyCampaignDetail(campaign.getCampaignId(), campaignDetailSource, creationDate);

				campaignTransaction.setTransactionSource(campaignDetailSource);
				campaignTransaction.setCampaignId(campaign.getCampaignId());
				campaignTransaction.setStartDate(creationDate);
				campaignTransaction.setEndDate(creationDate);

				// Check if transaction is new or is stand-alone TI:
				if (campaignTransaction.getIsNewMembership()
						|| (!campaignTransaction.getIsRenewedMembership() && campaignTransaction.getHasTI())) {
					
					transactionService.saveTransaction(campaignTransaction);
					transactionService.saveTransactionDetail(transactionDetailList);
					
				} else {
					// TODO: update this condition
					boolean attributionDataSaved = true /*saveAttributionData(attributionDays, campaignTransaction.getTransactionRevenue(),
							campaign.getCampaignId(), campaignDetailSource, creationDate)*/;
					if(attributionDataSaved) {
						transactionService.saveTransaction(campaignTransaction);
						shouldSaveTransactionDetails = true;
					}
				}
			}
		}

		if (shouldSaveTransactionDetails) {
			transactionService.saveTransactionDetail(transactionDetailList);
		}
	}

	/**
	 * This method is added to insert record in campaign detail with zero
	 * values. In case a record for Campaign Detail does not exist for a
	 * particular date, trigger on DB won't be able to update the new/renew
	 * count.
	 */
	private void insertDummyCampaignDetail(String campaignId, CampaignDetailSource campaignDetailSource, Date creationDate) {
		CampaignDetail campaignDetail = new CampaignDetail();
		campaignDetail.setStartDate(creationDate);
		campaignDetail.setEndDate(creationDate);
		campaignDetail.setCampaignId(campaignId);
		campaignDetail.setCampaignDetailSource(campaignDetailSource);
		campaignService.saveGrCampaignDetail(campaignDetail);
	}

	private boolean saveAttributionData(Long attributionDays, Double revenue, String campaignId,
			CampaignDetailSource campaignDetailSource, Date creationDate) {

		CampaignAttribution campaignAttribution = new CampaignAttribution();
		campaignAttribution.setIsManual(Boolean.TRUE);
		campaignAttribution.setCampaignId(campaignId);
		campaignAttribution.setEndDate(creationDate);
		campaignAttribution.setStartDate(creationDate);

		boolean isValidAttribution = Util.calculateAttributionData(attributionDays, campaignAttribution, revenue, 1);
		
		if(isValidAttribution && attributionDays >= 2 && attributionDays <= 7) {
			saveCampaignDetail(campaignId, campaignDetailSource, creationDate, revenue);
		}

		campaignService.saveGrCampaignAttribution(campaignAttribution);
		return isValidAttribution;
	}

	private void saveCampaignDetail(String campaignId, CampaignDetailSource campaignDetailSource, Date creationDate,
			Double revenue) {
		CampaignDetail campaignDetail = new CampaignDetail();
		campaignDetail.setCampaignDetailSource(campaignDetailSource);
		campaignDetail.setCampaignId(campaignId);
		campaignDetail.setEndDate(creationDate);
		campaignDetail.setStartDate(creationDate);
		campaignDetail.setGrRevenue(revenue);
		campaignDetail.setGrTransactionCount(1);
		campaignService.saveGrCampaignDetail(campaignDetail);
	}
}
