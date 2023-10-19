package com.gr.dm.core.adapter.gws;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsMapper;
import com.gr.dm.core.dto.crm.CrmCampaign;
import com.gr.dm.core.dto.crm.CrmCampaignCodeResponseDetail;
import com.gr.dm.core.dto.crm.CrmTransaction;
import com.gr.dm.core.dto.crm.CrmTransactionDetail;
import com.gr.dm.core.dto.crm.CrmTransactionResponseDetail;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignAttribution;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.CreatedFrom;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

public class CrmMapper {
	public static final Logger logger = Logger.getLogger(CrmMapper.class.getName());

	public static void mapCampaignData(CrmCampaignCodeResponseDetail activeMcCodeResponse, String fromDate, String toDate,
			List<Campaign> campaigns, List<CampaignDetail> campaignDetails) {

		List<CrmCampaign> crmCampaigns = activeMcCodeResponse.getData();

		if (Util.isNull(crmCampaigns)) {
			return;
		}
		
		for (CrmCampaign crmCampaign : crmCampaigns) {
			Campaign campaign = new Campaign();

			campaign.setCampaignId(crmCampaign.getCampaignId());
			campaign.setName(crmCampaign.getName());
			campaign.setCampaignSource(CampaignSource.Email);
			boolean isActive = true;
			campaign.setActive(isActive);
			campaigns.add(campaign);

			CampaignDetail campaignDetail = new CampaignDetail();
			campaignDetail.setCampaignId(campaign.getCampaignId());
			campaignDetail.setCost(crmCampaign.getCost());
			campaignDetail.setRevenue(crmCampaign.getRevenue());
			campaignDetail.setTransactionCount(crmCampaign.getTransactionCount());
			campaignDetail.setStartDate(DateUtil.getDate(fromDate));
			campaignDetail.setEndDate(DateUtil.getDate(toDate));
			campaignDetail.setCampaignDetailSource(CampaignDetailSource.Email);
			campaignDetail.setClicks(0);
			campaignDetail.setImpressions(0);
			campaignDetail.setIsDataSynced(Boolean.TRUE);
			campaignDetail.setLastUpdated(new Date());
			campaignDetails.add(campaignDetail);
		}
	}

	public static void mapTransactionData(CrmTransactionResponseDetail crmTransactionResponse, String fromDate,
			String toDate, List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails,
			List<CampaignAttribution> campaignAttributions, List<CampaignDetail> campaignDetails) {
		List<CrmTransaction> transactions = crmTransactionResponse.getTransactions();

		if (Util.isNull(transactions)) {
			return;
		}
		
		for (CrmTransaction crmTransaction : transactions) {

			for(CrmCampaign crmCampaign: crmTransaction.getCampaigns()) {
				CampaignDetailSource campaignDetailSource = null;
				String campaignSource = crmCampaign.getCampaignSource();
				if("Email Campaign".equals(campaignSource)) {
					campaignDetailSource = CampaignDetailSource.Email;
				} else if("Google".equals(campaignSource)) {
					campaignDetailSource = CampaignDetailSource.Adwords;
				} else {
					campaignDetailSource = CampaignDetailSource.fromValue(campaignSource);
				}
				CampaignTransaction transaction = new CampaignTransaction();
				transaction.setStartDate(DateUtil.getDate(fromDate));
				transaction.setEndDate(DateUtil.getDate(toDate));
				transaction.setCampaignId(crmCampaign.getCampaignId());
				transaction.setTransactionId(crmTransaction.getTransactionId());
				transaction.setTransactionRevenue(crmTransaction.getTransactionRevenue());
				transaction.setTransactionSource(campaignDetailSource);
				transaction.setCreatedFrom(CampaignDetailSource.Email.equals(transaction.getTransactionSource())
						? CreatedFrom.Email : CreatedFrom.SS);
				GoogleAnalyticsMapper.extractInfoFromTransactionId(transaction.getTransactionId(), transaction);
				mapTransactionDetails(transactionDetails, crmTransaction, transaction);

				transaction.setServerDate(crmTransaction.getServerDateTime());

				mapCampaignDetail(campaignDetails, transaction);
				
				mapAttributionData(campaignAttributions, transaction);

				
				campaignTransactions.add(transaction);
			}
		}
	}

	private static void mapCampaignDetail(List<CampaignDetail> campaignDetails,
			CampaignTransaction transaction) {
		CampaignDetail campaignDetail = getCampaignDetail(campaignDetails, transaction.getCampaignId(), transaction.getTransactionSource());
		if (Util.isNull(campaignDetail)) {
			campaignDetail = new CampaignDetail();
			campaignDetail.setCampaignDetailSource(transaction.getTransactionSource());
			campaignDetail.setCampaignId(transaction.getCampaignId());
			campaignDetail.setEndDate(transaction.getStartDate());
			campaignDetail.setGrRevenue(campaignDetail.getGrRevenue() + transaction.getTransactionRevenue());
			campaignDetail.setGrTransactionCount(campaignDetail.getGrTransactionCount() + 1);
			campaignDetail.setStartDate(transaction.getEndDate());
			campaignDetails.add(campaignDetail);
		} else {
			campaignDetail.setGrRevenue(campaignDetail.getGrRevenue() + transaction.getTransactionRevenue());
			campaignDetail.setGrTransactionCount(campaignDetail.getGrTransactionCount() + 1);
		}
	}

	private static void mapAttributionData(List<CampaignAttribution> campaignAttributions,
			CampaignTransaction transaction) {
		Long attributionDays = DateUtil.getDaysBetween(transaction.getStartDate(), transaction.getClientDate());
		CampaignAttribution campaignAttribution = getCampaignAttribution(campaignAttributions, transaction.getCampaignId());
		if(Util.isNull(campaignAttribution)) {
			campaignAttribution = new CampaignAttribution();
			campaignAttribution.setCampaignId(transaction.getCampaignId());
			campaignAttribution.setEndDate(transaction.getEndDate());
			campaignAttribution.setStartDate(transaction.getStartDate());
			campaignAttribution.setIsManual(!CampaignDetailSource.Email.equals(transaction.getTransactionSource()));
			boolean isValidAttribution = Util.calculateAttributionData(attributionDays, campaignAttribution, transaction.getTransactionRevenue(), 1);
			if(isValidAttribution) {
				campaignAttributions.add(campaignAttribution);
			}
		} else {
			Util.calculateAttributionData(attributionDays, campaignAttribution, transaction.getTransactionRevenue(), 1);
		}
	}

	private static void mapTransactionDetails(List<TransactionDetail> transactionDetails, CrmTransaction crmTransaction,
			CampaignTransaction transaction) {
		List<CrmTransactionDetail> crmTransactionDetails = crmTransaction.getTransactionDetails();

		if(Util.isNotNull(crmTransactionDetails)) {
			for (CrmTransactionDetail crmTransactionDetail : crmTransactionDetails) {

				TransactionDetail transactionDetail = new TransactionDetail();
				transactionDetail.setCost(crmTransactionDetail.getCost());
				transactionDetail.setPackageGuid(crmTransactionDetail.getPackageGuid());
				transactionDetail.setProductCategory(crmTransactionDetail.getProductCategory());
				transactionDetail.setQuantity(crmTransactionDetail.getQuantity());
				transactionDetail.setTransactionId(crmTransactionDetail.getTransactionId());
				transactionDetail.setProductName(crmTransactionDetail.getProductName());
				GoogleAnalyticsMapper.extractInfoFromProductCategory(crmTransactionDetail.getProductCategory(),
						transaction, crmTransactionDetail.getQuantity());
				transactionDetails.add(transactionDetail);
			}
		}
	}
	
	private static CampaignAttribution getCampaignAttribution(List<CampaignAttribution> campaignAttributions, String campaignId) {
		for (CampaignAttribution campaignAttribution : campaignAttributions) {
			if(campaignId.equals(campaignAttribution.getCampaignId())) {
				return campaignAttribution;
			}
		}
		return null;
	}
	
	private static CampaignDetail getCampaignDetail(List<CampaignDetail> campaignDetails, String campaignId, CampaignDetailSource campaignDetailSource) {
		for (CampaignDetail campaignDetail : campaignDetails) {
			if(campaignId.equals(campaignDetail.getCampaignId()) && campaignDetail.getCampaignDetailSource().equals(campaignDetailSource)) {
				return campaignDetail;
			}
		}
		return null;
	}
}