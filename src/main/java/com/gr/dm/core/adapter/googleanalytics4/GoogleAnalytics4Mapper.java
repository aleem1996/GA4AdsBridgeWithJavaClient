package com.gr.dm.core.adapter.googleanalytics4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.analytics.data.v1beta.DimensionHeader;
import com.google.analytics.data.v1beta.DimensionValue;
import com.google.analytics.data.v1beta.MetricHeader;
import com.google.analytics.data.v1beta.MetricValue;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsMapper;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.CreatedFrom;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
public class GoogleAnalytics4Mapper {
	
	private static final SimpleDateFormat analyticsDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	public static final Logger logger = Logger.getLogger(GoogleAnalyticsMapper.class.getName());

	
	public static void mapCampaignData(RunReportResponse reportResponse, String startDate, String endDate,
			List<Campaign> campaignList, List<CampaignDetail> campaignDetailList) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get("sessionCampaignId").size(); i++) {
				Campaign campaign = new Campaign();
				CampaignDetail campaignDetail = new CampaignDetail();
				campaign.setCampaignId(reportMap.get("sessionCampaignId").get(i));
				campaign.setName(reportMap.get("sessionCampaignName").get(i));
				
				LocalDate comparisonDate = LocalDate.of(2023, 7, 1);
		        LocalDate eDate = LocalDate.parse(endDate);
		        
		        if (eDate.isBefore(comparisonDate)) {
		        	campaign.setCampaignSource(CampaignSource.GoogleGA4);
		        	campaignDetail.setCampaignDetailSource(CampaignDetailSource.GA4);
		        } else {
					campaign.setCampaignSource(CampaignSource.Google);
					campaignDetail.setCampaignDetailSource(CampaignDetailSource.Analytics);
		        }
				campaignList.add(campaign);

				campaignDetail.setIsDataSynced(Boolean.TRUE);
				campaignDetail.setLastUpdated(new Date());
				campaignDetail.setCampaignId(campaign.getCampaignId());
				campaignDetail.setCost(Double.valueOf(reportMap.get("advertiserAdCost").get(i)));
				campaignDetail.setRevenue(Double.valueOf(reportMap.get("purchaseRevenue").get(i)));
				campaignDetail.setTransactionCount(Integer.valueOf(reportMap.get("transactions").get(i)));
				campaignDetail.setStartDate(DateUtil.getDate(startDate));
				campaignDetail.setEndDate(DateUtil.getDate(endDate));
				campaignDetail.setClicks(Integer.valueOf(reportMap.get("advertiserAdClicks").get(i)));
				campaignDetail.setImpressions(Integer.valueOf(reportMap.get("advertiserAdImpressions").get(i)));
				campaignDetailList.add(campaignDetail);
			}
		}
	}
	
	public static void mapTransactionData(RunReportResponse reportResponse, String startDate, String endDate,
			List<CampaignTransaction> campaignTransactionsList, CampaignDetailSource transactionSource) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get("transactionId").size(); i++) {
				
				CampaignTransaction transaction = new CampaignTransaction();
				transaction.setStartDate(DateUtil.getDate(startDate));
				transaction.setEndDate(DateUtil.getDate(endDate));
				transaction.setCampaignId(reportMap.get("sessionCampaignId").get(i));
				transaction.setTransactionId(reportMap.get("transactionId").get(i));
				transaction.setCampaignName(reportMap.get("sessionCampaignName").get(i));
				transaction.setCountry(reportMap.get("country").get(i));
				transaction.setBrowser(reportMap.get("browser").get(i));
				transaction.setKeywords(reportMap.get("googleAdsKeyword").get(i));
				transaction.setTransactionRevenue(Double.valueOf(reportMap.get("purchaseRevenue").get(i)));
				transaction.setTransactionSource(transactionSource);
				transaction.setCreatedFrom(
						Util.isNull(transactionSource) ? null : CreatedFrom.fromValue(transactionSource.getValue()));
				extractInfoFromTransactionId(transaction.getTransactionId(), transaction);
				
				try {
					transaction.setServerDate(analyticsDateFormat.parse(reportMap.get("dateHour").get(i)));
				} catch (ParseException ex) {
					logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
				}
				campaignTransactionsList.add(transaction);
			}
		}
	}
	
	
	public static void mapTransactionItemsData(RunReportResponse reportResponse, String startDate, String endDate,
			List<CampaignTransaction> campaignTransactionsList, List<TransactionDetail> transactionDetails, CampaignDetailSource transactionSource) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get("transactionId").size(); i++) {
				
				String productCategory = reportMap.get("itemCategory").get(i);
				String transactionId = reportMap.get("transactionId").get(i);
				CampaignTransaction ct = new CampaignTransaction();
				ct.setTransactionId(transactionId);
				ct.setTransactionSource(transactionSource);
				int transactionIndex = campaignTransactionsList.indexOf(ct);
				// update existing transaction object
				if(transactionIndex != -1) {
					CampaignTransaction transaction = campaignTransactionsList.get(transactionIndex);
					extractInfoFromProductCategory(productCategory, transaction, Integer.valueOf(reportMap.get("itemPurchaseQuantity").get(i)));
				}
				setTransactionDetails(transactionId, transactionDetails, reportMap, i);
			}
		}
	}
	
	public static Map<String, List<String>> transformReportResponseToMap(RunReportResponse reportResponse) {
		Map<String, List<String>> reportMap = new HashMap<String, List<String>>();

		List<DimensionHeader> dimensionHeaders = reportResponse.getDimensionHeadersList();
		List<MetricHeader> metricHeaders = reportResponse.getMetricHeadersList();
		List<Row> rows = reportResponse.getRowsList();

		if (rows == null) {
			System.out.println("No data found!");
			return reportMap;
		}

		for (Row row : rows) {
			List<DimensionValue> dimensions = row.getDimensionValuesList();
			List<MetricValue> metrics = row.getMetricValuesList();

			for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
				String dimensionHeader = dimensionHeaders.get(i).getName();
				String dimensionValue = dimensions.get(i).getValue();
				updateMap(reportMap, dimensionHeader, dimensionValue);
			}

			for (int j = 0; j < metrics.size(); j++) {
				String metricHeader = metricHeaders.get(j).getName();
				String metricValue = metrics.get(j).getValue();
				updateMap(reportMap, metricHeader, metricValue);
			}
		}
		return reportMap;
	}
	
	private static void updateMap(Map<String, List<String>> maptoUpdate, String key, String value) {
		if (maptoUpdate.get(key) == null) {
			List<String> list = new ArrayList<String>();
			list.add(value);
			maptoUpdate.put(key, list);
		} else {
			List<String> list = maptoUpdate.get(key);
			list.add(value);
			maptoUpdate.put(key, list);
		}
	}
	
	public static void extractInfoFromTransactionId(String transactionId, CampaignTransaction campaignTransaction) {
		if (Util.isNullOrEmpty(transactionId)) {
			return;
		}

		try {
			String[] id = transactionId.split(Constants.DATA_SPLITTER);
			String mcCodeSeperator = "MC:";
			// TODO: remove this if condition
			if (id.length >= 3) {
				campaignTransaction.setMemberId(id[0]);
				campaignTransaction.setPackageCode(id[1]);
				campaignTransaction.setClientDate(new Date(Long.valueOf(id[2])));
				
				if (id.length >= 4) {
					String code = id[3];
					if (code.indexOf(mcCodeSeperator) != -1) {
						campaignTransaction.setMcCode(code.split(mcCodeSeperator)[1]);
					} else {
						campaignTransaction.setRpCode(code);
					}
				}
				
				if (id.length >= 5) {
					campaignTransaction.setMcCode(id[4].split(mcCodeSeperator)[1]);
				}
			} else {
				// For old data:
				id = id[0].split(" ");
				if(id.length == 2) {
					campaignTransaction.setMemberId(id[0]);
					campaignTransaction.setClientDate(new Date(Long.valueOf(id[1])));
				}
			}
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
		}
	}
	
	public static void updateTransactionData(RunReportResponse reportResponse, String startDate, String endDate,
			List<CampaignTransaction> campaignTransactionsList, CampaignDetailSource transactionSource) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get("transactionId").size(); i++) {
				
				CampaignTransaction ct = new CampaignTransaction();
				ct.setTransactionId(reportMap.get("transactionId").get(i));
				ct.setTransactionSource(transactionSource);
				int transactionIndex = campaignTransactionsList.indexOf(ct);
				// update existing transaction object
				if(transactionIndex != -1) {
					CampaignTransaction transaction = campaignTransactionsList.get(transactionIndex);
					transaction.setSource(reportMap.get("sessionSource").get(i));
					transaction.setDevice(reportMap.get("deviceCategory").get(i));
				}
			}
		}
	}
	
	public static void extractInfoFromProductCategory(String productCategory,
			CampaignTransaction campaignTransaction, Integer quantity) {
		if (Util.isNullOrEmpty(productCategory) || "(not set)".equals(productCategory)) {
			return;
		}
		
		if(productCategory.contains(Constants.DATA_SPLITTER)) {
			String[] categories = productCategory.split(Constants.DATA_SPLITTER);
			
			for (String category : categories) {
				if(Constants.TRANSACTION_NEW_MEMBERSHIP.equals(category)) {
					campaignTransaction.setIsNewMembership(true);
					campaignTransaction.setIsRenewedMembership(false);
					
				} else if(Constants.TRANSACTION_RENEW_MEMBERSHIP.equals(category)) {
					campaignTransaction.setIsNewMembership(false);
					campaignTransaction.setIsRenewedMembership(true);
				}
				
				else if(Constants.TRANSACTION_TRAVEL_INSURANCE.equals(category)) {
					campaignTransaction.setHasTI(true);
				}
				
				else if(Constants.TRANSACTION_MEDICAL_DEVICE.equals(category)) {
					campaignTransaction.setHasMedicalDevice(true);
					campaignTransaction.setDeviceCount(quantity);
				}
			}
		} else {
			if(Constants.TRANSACTION_NEW_MEMBERSHIP.equals(productCategory)) {
				campaignTransaction.setIsNewMembership(true);
				campaignTransaction.setIsRenewedMembership(false);
				
			} else if(Constants.TRANSACTION_RENEW_MEMBERSHIP.equals(productCategory)) {
				campaignTransaction.setIsNewMembership(false);
				campaignTransaction.setIsRenewedMembership(true);
			}
			
			else if(Constants.TRANSACTION_TRAVEL_INSURANCE.equals(productCategory)) {
				campaignTransaction.setHasTI(true);
			}
			
			else if(Constants.TRANSACTION_MEDICAL_DEVICE.equals(productCategory)) {
				campaignTransaction.setHasMedicalDevice(true);
				campaignTransaction.setDeviceCount(quantity);
			}
		}
	}
	
	private static void setTransactionDetails(String transactionId, List<TransactionDetail> transactionDetails,
			Map<String, List<String>> reportMap, int rowIndex) {
		TransactionDetail transactionDetail = new TransactionDetail();
		transactionDetail.setTransactionId(transactionId);
		transactionDetail.setCost(Double.valueOf(reportMap.get("itemRevenue").get(rowIndex)));
		transactionDetail.setPackageGuid(reportMap.get("itemId").get(rowIndex));
		transactionDetail.setProductCategory(reportMap.get("itemCategory").get(rowIndex));
		transactionDetail.setProductName(reportMap.get("itemName").get(rowIndex));
		transactionDetail.setQuantity(Integer.valueOf(reportMap.get("itemPurchaseQuantity").get(rowIndex)));
		transactionDetails.add(transactionDetail);
	}

	public static Long getTransactionCount(RunReportResponse reportResponse) {
		Long transactionCount = 0L;
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get("sessionCampaignId").size(); i++) {
				transactionCount += Long.valueOf(reportMap.get("transactions").get(i));
			}
		}
		return transactionCount;
	}
}
