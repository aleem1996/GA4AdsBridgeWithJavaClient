package com.gr.dm.core.adapter.googleanalytics;

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

import com.google.api.services.analytics.model.McfData;
import com.google.api.services.analytics.model.McfData.ColumnHeaders;
import com.google.api.services.analyticsreporting.v4.model.ColumnHeader;
import com.google.api.services.analyticsreporting.v4.model.DateRangeValues;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.MetricHeaderEntry;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.api.services.analyticsreporting.v4.model.ReportRow;
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

public class GoogleAnalyticsMapper {
	
	public static final Logger logger = Logger.getLogger(GoogleAnalyticsMapper.class.getName());
	
	private static final SimpleDateFormat analyticsDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	
	public static void mapCampaignData(GetReportsResponse reportResponse, String startDate, String endDate,
			List<Campaign> campaignList, List<CampaignDetail> campaignDetailList) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get(Constants.GA_ADWORDS_CAMPAIGN_ID).size(); i++) {
				Campaign campaign = new Campaign();
				CampaignDetail campaignDetail = new CampaignDetail();
				campaign.setCampaignId(reportMap.get(Constants.GA_ADWORDS_CAMPAIGN_ID).get(i));
				campaign.setName(reportMap.get(Constants.GA_CAMPAIGN).get(i));
				LocalDate comparisonDate = LocalDate.of(2023, 6, 30);
		        LocalDate sDate = LocalDate.parse(startDate);
		        
		        if (sDate.isAfter(comparisonDate)) {
		        	campaign.setCampaignSource(CampaignSource.GoogleUA);
		        	campaignDetail.setCampaignDetailSource(CampaignDetailSource.UA);
		        } else {
					campaign.setCampaignSource(CampaignSource.Google);
					campaignDetail.setCampaignDetailSource(CampaignDetailSource.Analytics);
		        }
				campaignList.add(campaign);

				campaignDetail.setIsDataSynced(Boolean.TRUE);
				campaignDetail.setLastUpdated(new Date());
				campaignDetail.setCampaignId(campaign.getCampaignId());
				campaignDetail.setCost(Double.valueOf(reportMap.get(Constants.GA_AD_COST).get(i)));
				campaignDetail.setRevenue(Double.valueOf(reportMap.get(Constants.GA_TRANSACTION_REVENUE).get(i)));
				campaignDetail.setTransactionCount(Integer.valueOf(reportMap.get(Constants.GA_TRANSACTIONS).get(i)));
				campaignDetail.setStartDate(DateUtil.getDate(startDate));
				campaignDetail.setEndDate(DateUtil.getDate(endDate));
				campaignDetail.setClicks(Integer.valueOf(reportMap.get(Constants.GA_AD_CLICKS).get(i)));
				campaignDetail.setImpressions(Integer.valueOf(reportMap.get(Constants.GA_IMPRESSIONS).get(i)));
				campaignDetailList.add(campaignDetail);
			}
		}
	}
	
	public static void mapTransactionData(GetReportsResponse reportResponse, String startDate, String endDate,
			List<CampaignTransaction> campaignTransactionsList, CampaignDetailSource transactionSource) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get(Constants.GA_TRANSACTION_ID).size(); i++) {
				
				CampaignTransaction transaction = new CampaignTransaction();
				transaction.setStartDate(DateUtil.getDate(startDate));
				transaction.setEndDate(DateUtil.getDate(endDate));
				transaction.setCampaignId(reportMap.get(Constants.GA_ADWORDS_CAMPAIGN_ID).get(i));
				transaction.setTransactionId(reportMap.get(Constants.GA_TRANSACTION_ID).get(i));
				transaction.setCampaignName(reportMap.get(Constants.GA_CAMPAIGN).get(i));
				transaction.setCountry(reportMap.get(Constants.GA_COUNTRY).get(i));
				transaction.setBrowser(reportMap.get(Constants.GA_BROWSER).get(i));
				transaction.setKeywords(reportMap.get(Constants.GA_KEYWORD).get(i));
				transaction.setTransactionRevenue(Double.valueOf(reportMap.get(Constants.GA_TRANSACTION_REVENUE).get(i)));
				transaction.setTransactionSource(transactionSource);
				transaction.setCreatedFrom(
						Util.isNull(transactionSource) ? null : CreatedFrom.fromValue(transactionSource.getValue()));
				extractInfoFromTransactionId(transaction.getTransactionId(), transaction);
				
				try {
					transaction.setServerDate(analyticsDateFormat.parse(reportMap.get(Constants.GA_DATE_HOUR_MINIUTE).get(i)));
				} catch (ParseException ex) {
					logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
				}
				campaignTransactionsList.add(transaction);
			}
		}
	}
	
	public static void updateTransactionData(GetReportsResponse reportResponse, String startDate, String endDate,
			List<CampaignTransaction> campaignTransactionsList, CampaignDetailSource transactionSource) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get(Constants.GA_TRANSACTION_ID).size(); i++) {
				
				CampaignTransaction ct = new CampaignTransaction();
				ct.setTransactionId(reportMap.get(Constants.GA_TRANSACTION_ID).get(i));
				ct.setTransactionSource(transactionSource);
				int transactionIndex = campaignTransactionsList.indexOf(ct);
				// update existing transaction object
				if(transactionIndex != -1) {
					CampaignTransaction transaction = campaignTransactionsList.get(transactionIndex);
//					transaction.setSource("Google UA");
					transaction.setSource(reportMap.get(Constants.GA_SOURCE).get(i));
					transaction.setDevice(reportMap.get(Constants.GA_DEVICE_CATEGORY).get(i));
				}
			}
		}
	}
	
	public static void mapTransactionItemsData(GetReportsResponse reportResponse, String startDate, String endDate,
			List<CampaignTransaction> campaignTransactionsList, List<TransactionDetail> transactionDetails, CampaignDetailSource transactionSource) {
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get(Constants.GA_TRANSACTION_ID).size(); i++) {
				
				String productCategory = reportMap.get(Constants.GA_PRODUCT_CATEGORY).get(i);
				String transactionId = reportMap.get(Constants.GA_TRANSACTION_ID).get(i);
				CampaignTransaction ct = new CampaignTransaction();
				ct.setTransactionId(transactionId);
				ct.setTransactionSource(transactionSource);
				int transactionIndex = campaignTransactionsList.indexOf(ct);
				// update existing transaction object
				if(transactionIndex != -1) {
					CampaignTransaction transaction = campaignTransactionsList.get(transactionIndex);
					extractInfoFromProductCategory(productCategory, transaction, Integer.valueOf(reportMap.get(Constants.GA_ITEM_QUANTITY).get(i)));
				}
				setTransactionDetails(transactionId, transactionDetails, reportMap, i);
			}
		}
	}
	
	private static void setTransactionDetails(String transactionId, List<TransactionDetail> transactionDetails,
			Map<String, List<String>> reportMap, int rowIndex) {
		TransactionDetail transactionDetail = new TransactionDetail();
		transactionDetail.setTransactionId(transactionId);
		transactionDetail.setCost(Double.valueOf(reportMap.get(Constants.GA_ITEM_REVENUE).get(rowIndex)));
		transactionDetail.setPackageGuid(reportMap.get(Constants.GA_PRODUCT_SKU).get(rowIndex));
		transactionDetail.setProductCategory(reportMap.get(Constants.GA_PRODUCT_CATEGORY).get(rowIndex));
		transactionDetail.setProductName(reportMap.get(Constants.GA_PRODUCT_NAME).get(rowIndex));
		transactionDetail.setQuantity(Integer.valueOf(reportMap.get(Constants.GA_ITEM_QUANTITY).get(rowIndex)));
		transactionDetails.add(transactionDetail);
	}
	
	public static Long getTransactionCount(GetReportsResponse reportResponse) {
		Long transactionCount = 0L;
		Map<String, List<String>> reportMap = transformReportResponseToMap(reportResponse);
		if (reportMap.size() > 0) {
			for (int i = 0; i < reportMap.get(Constants.GA_ADWORDS_CAMPAIGN_ID).size(); i++) {
				transactionCount += Long.valueOf(reportMap.get(Constants.GA_TRANSACTIONS).get(i));
			}
		}
		return transactionCount;
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

	private static Map<String, List<String>> transformReportResponseToMap(GetReportsResponse reportResponse) {
		Map<String, List<String>> reportMap = new HashMap<String, List<String>>();
		for (Report report : reportResponse.getReports()) {
			ColumnHeader header = report.getColumnHeader();
			List<String> dimensionHeaders = header.getDimensions();
			List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
			List<ReportRow> rows = report.getData().getRows();

			if (rows == null) {
				System.out.println("No data found!");
				return reportMap;
			}

			for (ReportRow row : rows) {
				List<String> dimensions = row.getDimensions();
				List<DateRangeValues> metrics = row.getMetrics();

				for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
					String dimensionHeader = dimensionHeaders.get(i);
					String dimensionValue = dimensions.get(i);

					updateMap(reportMap, dimensionHeader, dimensionValue);
				}

				for (int j = 0; j < metrics.size(); j++) {
					DateRangeValues values = metrics.get(j);
					for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
						String metricHeader = metricHeaders.get(k).getName();
						String metricValue = values.getValues().get(k);
						updateMap(reportMap, metricHeader, metricValue);
					}
				}
			}
		}
		return reportMap;
	}

	public static Map<String, Double> transformReportResponseTotalsToMap(GetReportsResponse reportResponse) {
		Map<String, Double> result = new HashMap<>();
		for (Report report : reportResponse.getReports()) {
			ColumnHeader header = report.getColumnHeader();
			List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
			List<String> totals = report.getData().getTotals().get(0).getValues();
			for (int i = 0; i < metricHeaders.size(); i++) {
				result.put(metricHeaders.get(i).getName().replaceFirst("ga:", ""), Double.parseDouble(totals.get(i)));
			}
		}
		return result;
	}
	
	public static List<Map<String, Object>> transformReportResponseToEvents(GetReportsResponse reportResponse){
		List<Map<String, Object>> events = new ArrayList<>();
		for (Report report : reportResponse.getReports()) {
			ColumnHeader header = report.getColumnHeader();
			List<String> dimensionHeaders = header.getDimensions();
			List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
			List<ReportRow> rows = report.getData().getRows();
			if (rows == null) {
				logger.log(Level.WARNING, "No Nows Returned For Report");
				return events;
			}
			for (ReportRow row : rows) {
				Map<String, Object> event = new HashMap<>();
				List<String> dimensions = row.getDimensions();
				 List<String> metricValues = row.getMetrics().get(0).getValues();
				for (int i = 0; i < dimensions.size(); i++) {
					event.put(dimensionHeaders.get(i).replaceAll("ga:", ""), dimensions.get(i));
				}
				for (int i = 0; i < metricValues.size(); i++) {
					event.put(metricHeaders.get(i).getName().replaceAll("ga:", ""), metricValues.get(i));
				}
				events.add(event);
			}
		}
		return events;
	}
	
	public static Map<String, List<String>> transformMcfData(McfData mcfData) {
		Map<String, List<String>> reportMap = new HashMap<String, List<String>>();
		if (mcfData.getTotalResults() > 0) {
			// Print the column names.
			List<ColumnHeaders> headers = mcfData.getColumnHeaders();
			for (ColumnHeaders header : headers) {
				reportMap.put(header.getName(), new ArrayList<String>());
			}

			// Print the rows of data.
			for (List<McfData.Rows> row : mcfData.getRows()) {
				for (int columnIndex = 0; columnIndex < row.size(); ++columnIndex) {
					ColumnHeaders header = headers.get(columnIndex);
					McfData.Rows cell = row.get(columnIndex);
					if (header.getDataType().equals("MCF_SEQUENCE")) {
						reportMap.get(header.getName()).add(getStringFromMcfSequence(cell.getConversionPathValue()));
					} else {
						reportMap.get(header.getName()).add(cell.getPrimitiveValue());
					}
				}
			}
		} else {
			System.out.println("No rows found");
		}
		return reportMap;
	}
	
	private static String getStringFromMcfSequence(List<McfData.Rows.ConversionPathValue> path) {
		StringBuilder stringBuilder = new StringBuilder();
		for (McfData.Rows.ConversionPathValue pathElement : path) {
			if (stringBuilder.length() > 0)
				stringBuilder.append(" > ");
			stringBuilder.append(pathElement.getNodeValue());
		}
		return stringBuilder.toString();
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
}
