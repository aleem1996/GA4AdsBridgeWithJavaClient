package com.gr.dm.core.adapter.googleanalytics4;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Filter;
import com.google.analytics.data.v1beta.FilterExpression;
import com.google.analytics.data.v1beta.FilterExpression.Builder;
import com.google.analytics.data.v1beta.FilterExpressionList;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.gr.dm.core.adapter.Adapter;
import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsAdapter;
import com.gr.dm.core.adapter.googleanalytics.GoogleAnalyticsMapper;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.CreatedFrom;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.service.CampaignService;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.Util;
/**
 * 
 * @author Aleem Malik
 *
 */
@Service
public class GoogleAnalytics4Adapter implements Adapter {

	@Autowired
	GoogleAnalytics4AdapterSettings settings;
	
	@Autowired
	CampaignService campaignService;
	
	public static final Logger logger = Logger.getLogger(GoogleAnalyticsAdapter.class.getName());

	
	public void loadCampaignData(List<Campaign> campaigns, List<CampaignDetail> campaignDetails, String startDate, String endDate) throws Exception {

		List<Metric> metricsList = getMetricsList(new String[] {"transactions", "advertiserAdCost", "purchaseRevenue", "advertiserAdClicks", "advertiserAdImpressions"});
		List<Dimension> dimensionsList = getDimensionsList(new String[] {"sessionCampaignName", "sessionCampaignId" });

		Builder filterExpression = FilterExpression.newBuilder()
                .setAndGroup(
                        FilterExpressionList.newBuilder()
                            .addExpressions(
                                FilterExpression.newBuilder()
                                .setNotExpression(
                                        FilterExpression.newBuilder()
                                        .setFilter(
                                            Filter.newBuilder()
                                                .setFieldName("sessionCampaignName")
                                                .setStringFilter(
                                                    Filter.StringFilter.newBuilder()
                                                        .setValue("(not set)")))))
                            .addExpressions(
                                    FilterExpression.newBuilder()
                                    .setNotExpression(
                                            FilterExpression.newBuilder()
                                            .setFilter(
                                                Filter.newBuilder()
                                                    .setFieldName("sessionCampaignId")
                                                    .setStringFilter(
                                                        Filter.StringFilter.newBuilder()
                                                            .setValue("(not set)"))))));
		
		RunReportResponse response = getReport(startDate, endDate, metricsList, dimensionsList, filterExpression);
		GoogleAnalytics4Mapper.mapCampaignData(response, startDate, endDate, campaigns, campaignDetails);
	}
	
	private void loadCampaignTransactionsData(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate, CampaignDetailSource source, List<String> transactions) throws Exception {
		List<Metric> metricsList = getMetricsList(new String[] { "purchaseRevenue" });

		List<Dimension> dimensionsList = getDimensionsList(new String[] { "sessionCampaignId", "transactionId", "sessionCampaignName", "country", "browser" , "googleAdsKeyword"
				, "dateHour" });

//		String filter = "";
//
//		if (Util.isNull(source)) {
//			filter = getTransactionIdsFilter(transactions);
//		} else if (CampaignDetailSource.Analytics.equals(source)) {
//			filter = Constants.GA_ADWORDS_CAMPAIGN_ID + "!=(not set)";
//		} else {
//			filter = Constants.GA_SOURCE_MEDIUM + "=~^bing / cpc";
//		}
		
//      Since we always get source as null therefore not applied else checks like above code
		Builder filterExpression = FilterExpression.newBuilder();
		com.google.analytics.data.v1beta.FilterExpressionList.Builder filterExpressionList = FilterExpressionList.newBuilder();
		Builder filter = null;
		for (String transaction : transactions) {
			filter = FilterExpression.newBuilder()
			        .setFilter(
			            Filter.newBuilder()
			                .setFieldName("transactionId")
			                .setStringFilter(Filter.StringFilter.newBuilder().setValue(transaction)));
			
			filterExpressionList.addExpressions(filter);
		}
		filterExpression.setOrGroup(filterExpressionList);


		RunReportResponse response = getReport(startDate, endDate, metricsList, dimensionsList, filterExpression);
		GoogleAnalytics4Mapper.mapTransactionData(response, startDate, endDate, campaignTransactions, source);

		if (Util.isNotNull(campaignTransactions) && campaignTransactions.size() > 0) {
			Thread.sleep(1000);
			dimensionsList = getDimensionsList(new String[] { "transactionId", "sessionSource", "deviceCategory" });
			response = getReport(startDate, endDate, metricsList, dimensionsList, filterExpression);
			GoogleAnalytics4Mapper.updateTransactionData(response, startDate, endDate, campaignTransactions, source);

			Thread.sleep(1000);
			loadTransactionItems(campaignTransactions, transactionDetails, startDate, endDate, source, transactions);
		}

	}
	
	private void loadTransactionItems(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate, CampaignDetailSource source, List<String> transactions) throws Exception {
		
		List<Metric> metricsList = getMetricsList(new String[] { "itemRevenue", "itemPurchaseQuantity" });

		List<Dimension> dimensionsList = getDimensionsList(new String[] {
				"transactionId", "itemId", "itemName", "itemCategory"});

//		String filter = "";
//		
//		if (Util.isNull(source)) {
//			filter = getTransactionIdsFilter(transactions);
//		} else if (CampaignDetailSource.Analytics.equals(source)) {
//			filter = Constants.GA_ADWORDS_CAMPAIGN_ID + "!=(not set)";
//		} else {
//			filter = Constants.GA_SOURCE_MEDIUM + "=~^bing / cpc";
//		}
		
//      Since we always get source as null therefore not applied else checks like above code

		Builder filterExpression = FilterExpression.newBuilder();
		com.google.analytics.data.v1beta.FilterExpressionList.Builder filterExpressionList = FilterExpressionList.newBuilder();
		Builder filter = null;
		for (String transaction : transactions) {
			filter = FilterExpression.newBuilder()
			        .setFilter(
			            Filter.newBuilder()
			                .setFieldName("transactionId")
			                .setStringFilter(Filter.StringFilter.newBuilder().setValue(transaction)));
			
			filterExpressionList.addExpressions(filter);
		}
		filterExpression.setOrGroup(filterExpressionList);

		RunReportResponse response = getReport(startDate, endDate, metricsList, dimensionsList, filterExpression);
		GoogleAnalytics4Mapper.mapTransactionItemsData(response, startDate, endDate, campaignTransactions, transactionDetails, source);
	}

	public void loadAssistedTransactionsData(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate) throws Exception {
		
		List<Metric> metricsList = getMetricsList(
				new String[] {"conversions"});
		List<Dimension> dimensionsList = getDimensionsList(new String[] {"isConversionEvent", "sessionDefaultChannelGroup", "transactionId", "sessionCampaignName", "sessionSourceMedium"});
		Builder filterExpression = FilterExpression.newBuilder()
                .setFilter(
                    Filter.newBuilder()
                        .setFieldName("sessionDefaultChannelGroup")
                        .setStringFilter(
                            Filter.StringFilter.newBuilder().setValue("Paid Search")));
		
		RunReportResponse response = getReport(startDate, endDate, metricsList, dimensionsList, filterExpression);
		Map<String, List<String>> reportMap = GoogleAnalytics4Mapper.transformReportResponseToMap(response);

		populateTransactionData(campaignTransactions, transactionDetails, startDate, endDate, reportMap);
	}
	
	private void populateTransactionData(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate,
			Map<String, List<String>> reportMap) throws Exception {

		if (Util.isNotNull(reportMap) && reportMap.size() > 0) {

			List<String> transactions = reportMap.get("transactionId");

			this.loadCampaignTransactionsData(campaignTransactions, transactionDetails, startDate, endDate, null, transactions);
			Set<String> transactionIdsSet = new HashSet<String>();

			for (int i = 0; i < reportMap.get("transactionId").size(); i++) {

				String transactionId = reportMap.get("transactionId").get(i);
				String sourceMedium = reportMap.get("sessionSourceMedium").get(i);
				String campaignName = reportMap.get("sessionCampaignName").get(i);
				campaignName = URLDecoder.decode(campaignName, "UTF-8");
				
//				CampaignSource campaignSource = "google / cpc".equals(sourceMedium) ? CampaignSource.GoogleGA4 : CampaignSource.Bing;
				LocalDate comparisonDate = LocalDate.of(2023, 6, 30);
		        LocalDate sDate = LocalDate.parse(startDate);
		        
		        CampaignSource campaignSource = null;
		        if (sDate.isAfter(comparisonDate)) {
		        	campaignSource = "google / cpc".equals(sourceMedium) ? CampaignSource.Google : CampaignSource.Bing;
		        } else {
		        	campaignSource = "google / cpc".equals(sourceMedium) ? CampaignSource.GoogleGA4 : CampaignSource.Bing;

		        }
				Campaign campaign = campaignService.findCampaignExcludingWhiteSpace(campaignName, campaignSource);
				if (Util.isNull(campaign)) {
					// TODO: Send an email and remove this transaction from list
					logger.log(Level.WARNING, "Campaign not found with name: " + campaignName + " for " + campaignSource.getValue());
					continue;
				}
				
				CampaignDetailSource transactionSource = null;
				if (CampaignSource.GoogleGA4.equals(campaign.getCampaignSource())) {
					transactionSource = CampaignDetailSource.GA4;
				} else if (CampaignSource.Google.equals(campaign.getCampaignSource())) {
					transactionSource = CampaignDetailSource.Analytics;
				} else {
					transactionSource = CampaignDetailSource.fromValue(campaign.getCampaignSource().toString());
				}
//				boolean isAssisted = Integer.valueOf(mappedMcfData.get(Constants.GA_MCF_ASSISTED_CONVERSIONS).get(i)) == 1;
//				boolean isDirect = Integer.valueOf(reportMap.get("conversions").get(i)) == 1;
				boolean isDirect = Boolean.TRUE; // for now
				boolean isAssisted = Boolean.FALSE;

				CampaignTransaction campaignTransaction = findCampaignTransaction(campaignTransactions, transactionId);
				if (Util.isNull(campaignTransaction)) {
					continue;
				}

				if (transactionIdsSet.add(transactionId)) {

					campaignTransaction.setCampaignId(campaign.getCampaignId());
					campaignTransaction.setTransactionSource(transactionSource);
					campaignTransaction.setCreatedFrom(CreatedFrom.fromValue(transactionSource.toString()));

					campaignTransaction.setIsAssisted(isAssisted);
					campaignTransaction.setIsDirect(isDirect);
					campaignTransaction.setIsDefaultAssist(isAssisted);

				} else {
					// duplicate transaction id found. create a clone of CampaignTransaction, update
					// fields and add it to campaignTransactions:
					ObjectMapper objectMapper = new ObjectMapper();
					CampaignTransaction newCampaignTransaction = objectMapper.readValue(objectMapper.writeValueAsString(campaignTransaction), CampaignTransaction.class);
					newCampaignTransaction.setCampaignId(campaign.getCampaignId());
					newCampaignTransaction.setTransactionSource(transactionSource);
					newCampaignTransaction.setCreatedFrom(CreatedFrom.fromValue(transactionSource.toString()));
					newCampaignTransaction.setSource(CampaignDetailSource.Analytics.equals(transactionSource) ? "google" : "bing");

					newCampaignTransaction.setIsAssisted(isAssisted);
					newCampaignTransaction.setIsDirect(isDirect);
					if (isDirect) {
						campaignTransaction.setIsDefaultTransaction(Boolean.FALSE);
						newCampaignTransaction.setIsDefaultTransaction(Boolean.TRUE);
					} else {
						newCampaignTransaction.setIsDefaultTransaction(Boolean.FALSE);
					}
					newCampaignTransaction.setIsDefaultAssist(isAssisted && !campaignTransaction.getIsDefaultAssist());
					campaignTransactions.add(newCampaignTransaction);
				}
			}
		}
	}
	
	private CampaignTransaction findCampaignTransaction(List<CampaignTransaction> campaignTransactions,
			String transactionId) {
		for (CampaignTransaction campaignTransaction : campaignTransactions) {
			if (transactionId.equals(campaignTransaction.getTransactionId())) {
				return campaignTransaction;
			}
		}
		return null;
	}
	
	public Long getTransactionCount(String startDate, String endDate) throws Exception {
		List<Metric> metricsList = getMetricsList(
				new String[] {"transactions"});
		List<Dimension> dimensionsList = getDimensionsList(new String[] {"sessionCampaignId"});

		Builder filterExpression = FilterExpression.newBuilder()
                .setNotExpression(
                        FilterExpression.newBuilder()
                        .setFilter(
                            Filter.newBuilder()
                                .setFieldName("sessionCampaignId")
                                .setStringFilter(
                                    Filter.StringFilter.newBuilder()
                                        .setValue("(not set)"))));

		RunReportResponse response = getReport(startDate, endDate, metricsList, dimensionsList, filterExpression);
		return GoogleAnalytics4Mapper.getTransactionCount(response);
	}
	
	public RunReportResponse getReport(String startDate, String endDate, List<Metric> metricsList, List<Dimension> dimensionsList, Builder filterExpression) throws IOException {
		RunReportRequest request = null;
		// if filterExpression is set to null it gives error
		if(Util.isNotNull(filterExpression)) {
		       request =
		              RunReportRequest.newBuilder()
		                  .setProperty("properties/" + "335258428")
		                  .addAllDimensions(dimensionsList)
		                  .addAllMetrics(metricsList)
		                  .setDimensionFilter(filterExpression)
		                  .addDateRanges(DateRange.newBuilder().setStartDate(startDate).setEndDate(endDate))
		                  .build();
		} else {
		       request =
		              RunReportRequest.newBuilder()
		                  .setProperty("properties/" + "335258428")
		                  .addAllDimensions(dimensionsList)
		                  .addAllMetrics(metricsList)
		                  .addDateRanges(DateRange.newBuilder().setStartDate(startDate).setEndDate(endDate))
		                  .build();
		}
	      BetaAnalyticsDataClient ga4Client = AnalyticsDataClient.getBetaAnalyticsData(settings);
	      return ga4Client.runReport(request);
		
	}
	
	private List<Metric> getMetricsList(String[] metrics) {
		List<Metric> metricsList = new ArrayList<Metric>();
		for (String met : metrics) {
			Metric metric = Metric.newBuilder().setName(met).build();
			metricsList.add(metric);
		}
		return metricsList;
	}

	// Utility methods
	private List<Dimension> getDimensionsList(String[] dimensions) {
		List<Dimension> dimensionsList = new ArrayList<Dimension>();
		for (String dim : dimensions) {
			Dimension dimension = Dimension.newBuilder().setName(dim).build();
			dimensionsList.add(dimension);
		}
		return dimensionsList;
	}

	static class AnalyticsDataClient {

		private static BetaAnalyticsDataClient ga4Client = null;

		private AnalyticsDataClient() {
		}

		static BetaAnalyticsDataClient getBetaAnalyticsData(GoogleAnalytics4AdapterSettings settings) throws IOException {

			if (Util.isNull(ga4Client)) {
				synchronized (AnalyticsDataClient.class) {
					if (Util.isNull(ga4Client)) {
						String credentionalsJsonPath = new ClassPathResource(settings.getConfigFileName()).getFile().getAbsolutePath();
						GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentionalsJsonPath));
						BetaAnalyticsDataSettings betaAnalyticsDataSettings = BetaAnalyticsDataSettings.newBuilder()
								.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
						ga4Client = BetaAnalyticsDataClient.create(betaAnalyticsDataSettings);

					}
				}
			}
			return ga4Client;
		}
	}

}
