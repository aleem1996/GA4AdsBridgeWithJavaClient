package com.gr.dm.core.adapter.googleanalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Mcf.Get;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.McfData;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.SearchUserActivityRequest;
import com.google.api.services.analyticsreporting.v4.model.SearchUserActivityResponse;
import com.google.api.services.analyticsreporting.v4.model.User;
import com.gr.dm.core.adapter.Adapter;
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
public class GoogleAnalyticsAdapter implements Adapter {
	
	public static final Logger logger = Logger.getLogger(GoogleAnalyticsAdapter.class.getName());

	@Autowired
	private GoogleAnalyticsAdapterSettings settings;
	
	@Autowired
	CampaignService campaignService;

	private final AnalyticsClient analyticsClient = new AnalyticsClient();

	public void loadCampaignData(List<Campaign> campaigns, List<CampaignDetail> campaignDetails, String startDate, String endDate) throws Exception {
		
		List<Metric> metricsList = getMetricsList(
				new String[] { Constants.GA_TRANSACTIONS, Constants.GA_TRANSACTION_REVENUE, Constants.GA_AD_COST,
						Constants.GA_IMPRESSIONS, Constants.GA_AD_CLICKS });
		List<Dimension> dimensionsList = getDimensionsList(new String[] { Constants.GA_CAMPAIGN, Constants.GA_ADWORDS_CAMPAIGN_ID });

		String filter = Constants.GA_ADWORDS_CAMPAIGN_ID + "!=(not set);" + Constants.GA_CAMPAIGN + "!=(not set)";

		GetReportsResponse response = analyticsClient.getReport(startDate, endDate, metricsList, dimensionsList, filter);
		GoogleAnalyticsMapper.mapCampaignData(response, startDate, endDate, campaigns, campaignDetails);
	}
	
	private void loadCampaignTransactionsData(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate, CampaignDetailSource source, List<String> transactions) throws Exception {
		
		List<Metric> metricsList = getMetricsList(new String[] { Constants.GA_TRANSACTION_REVENUE });

		List<Dimension> dimensionsList = getDimensionsList(new String[] { Constants.GA_ADWORDS_CAMPAIGN_ID,
				Constants.GA_TRANSACTION_ID, Constants.GA_CAMPAIGN, Constants.GA_COUNTRY, Constants.GA_BROWSER,
				Constants.GA_KEYWORD, Constants.GA_DATE_HOUR_MINIUTE });

		String filter = "";

		if (Util.isNull(source)) {
			filter = getTransactionIdsFilter(transactions);
		} else if (CampaignDetailSource.Analytics.equals(source)) {
			filter = Constants.GA_ADWORDS_CAMPAIGN_ID + "!=(not set)";
		} else {
			filter = Constants.GA_SOURCE_MEDIUM + "=~^bing / cpc";
		}

		GetReportsResponse response = analyticsClient.getReport(startDate, endDate, metricsList, dimensionsList,
				filter);
		GoogleAnalyticsMapper.mapTransactionData(response, startDate, endDate, campaignTransactions, source);

		if (Util.isNotNull(campaignTransactions) && campaignTransactions.size() > 0) {
			Thread.sleep(1000);
			dimensionsList = getDimensionsList(
					new String[] { Constants.GA_TRANSACTION_ID, Constants.GA_SOURCE, Constants.GA_DEVICE_CATEGORY });
			response = analyticsClient.getReport(startDate, endDate, metricsList, dimensionsList, filter);
			GoogleAnalyticsMapper.updateTransactionData(response, startDate, endDate, campaignTransactions, source);
			
			Thread.sleep(1000);
			loadTransactionItems(campaignTransactions, transactionDetails, startDate, endDate, source, transactions);
		}

	}

	private String getTransactionIdsFilter(List<String> transactions) {
		String transactionIds = "";
		for (String transaction : transactions) {
			transactionIds += Constants.GA_TRANSACTION_ID + "=~^" + transaction + ",";
		}
		transactionIds = transactionIds.replaceAll(",$", "");
		return transactionIds;
	}
	
	public void loadAssistedTransactionsData(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate) throws Exception {
		String metrics = Constants.GA_MCF_ASSISTED_CONVERSIONS + ", " + Constants.GA_MCF_LAST_INTERACTION_CONVERSIONS;
		String dimensions = Constants.GA_MCF_CAMPAIGN_NAME + ", " + Constants.GA_MCF_SOURCE_MEDIUM +  ", " + Constants.GA_MCF_TRANSACTION_ID;
		String filterExpressions = Constants.GA_MCF_CONVERSION_TYPE + "=~^Transaction;" + Constants.GA_MCF_BASIC_CHANNEL_GROUPING +  "=~^Paid Search";
		McfData mcfData = analyticsClient.getMcfReport(startDate, endDate, metrics, dimensions, filterExpressions);
		Map<String, List<String>> mappedMcfData = GoogleAnalyticsMapper.transformMcfData(mcfData);
		
		populateTransactionData(campaignTransactions, transactionDetails, startDate, endDate, mappedMcfData);
	}

	private void populateTransactionData(List<CampaignTransaction> campaignTransactions,
			List<TransactionDetail> transactionDetails, String startDate, String endDate,
			Map<String, List<String>> mappedMcfData)
			throws Exception {
		if(Util.isNotNull(mappedMcfData) && mappedMcfData.size() > 0) {
			
			List<String> transactions = mappedMcfData.get(Constants.GA_MCF_TRANSACTION_ID);
			
			this.loadCampaignTransactionsData(campaignTransactions, transactionDetails, startDate, endDate, null, transactions);
			Set<String> transactionIdsSet = new HashSet<String>();
			
			for (int i = 0; i < mappedMcfData.get(Constants.GA_MCF_TRANSACTION_ID).size(); i++) {
				
				String transactionId = mappedMcfData.get(Constants.GA_MCF_TRANSACTION_ID).get(i);
				String sourceMedium = mappedMcfData.get(Constants.GA_MCF_SOURCE_MEDIUM).get(i);
				String campaignName = mappedMcfData.get(Constants.GA_MCF_CAMPAIGN_NAME).get(i);
				campaignName = URLDecoder.decode(campaignName, "UTF-8");
				
				LocalDate comparisonDate = LocalDate.of(2023, 6, 30);
		        LocalDate sDate = LocalDate.parse(startDate);
		        CampaignSource campaignSource = null;
		        if (sDate.isAfter(comparisonDate)) {
		        	campaignSource = "google / cpc".equals(sourceMedium) ? CampaignSource.GoogleUA : CampaignSource.Bing;
		        } else {
		        	campaignSource = "google / cpc".equals(sourceMedium) ? CampaignSource.Google : CampaignSource.Bing;

		        }
				Campaign campaign = campaignService.findCampaignExcludingWhiteSpace(campaignName, campaignSource);
				if(Util.isNull(campaign)) {
					//TODO: Send an email and remove this transaction from list
					logger.log(Level.WARNING, "Campaign not found with name: " + campaignName + " for " + campaignSource.getValue());
					continue;
				}
				
//				CampaignDetailSource transactionSource = CampaignSource.Google
//						.equals(campaign.getCampaignSource()) ? CampaignDetailSource.Analytics
//								: CampaignDetailSource.fromValue(campaign.getCampaignSource().toString());
				
				CampaignDetailSource transactionSource = null;
				if (CampaignSource.Google.equals(campaign.getCampaignSource())) {
					transactionSource = CampaignDetailSource.Analytics;
				} else if (CampaignSource.GoogleUA.equals(campaign.getCampaignSource())) {
					transactionSource = CampaignDetailSource.UA;
				} else {
					transactionSource = CampaignDetailSource.fromValue(campaign.getCampaignSource().toString());
				}
				
				boolean isAssisted = Integer.valueOf(mappedMcfData.get(Constants.GA_MCF_ASSISTED_CONVERSIONS).get(i)) == 1;
				boolean isDirect = Integer.valueOf(mappedMcfData.get(Constants.GA_MCF_LAST_INTERACTION_CONVERSIONS).get(i)) == 1;
				
				CampaignTransaction campaignTransaction = findCampaignTransaction(campaignTransactions, transactionId);
				if (Util.isNull(campaignTransaction)) {
					continue;
				}
				
				if(transactionIdsSet.add(transactionId)) {
					
					campaignTransaction.setCampaignId(campaign.getCampaignId());
					campaignTransaction.setTransactionSource(transactionSource);
					campaignTransaction.setCreatedFrom(CreatedFrom.fromValue(transactionSource.toString()));
					
					campaignTransaction.setIsAssisted(isAssisted);
					campaignTransaction.setIsDirect(isDirect);
					campaignTransaction.setIsDefaultAssist(isAssisted);
					
					
				} else {
					//duplicate transaction id found. create a clone of CampaignTransaction, update fields and add it to campaignTransactions:
					ObjectMapper objectMapper = new ObjectMapper();
					CampaignTransaction newCampaignTransaction = objectMapper
							.readValue(objectMapper.writeValueAsString(campaignTransaction), CampaignTransaction.class);
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
	 
	private void loadTransactionItems(List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails, String startDate, String endDate, CampaignDetailSource source, List<String> transactions) throws Exception {
		
		List<Metric> metricsList = getMetricsList(new String[] { Constants.GA_ITEM_REVENUE, Constants.GA_ITEM_QUANTITY });

		List<Dimension> dimensionsList = getDimensionsList(new String[] { 
				Constants.GA_TRANSACTION_ID, Constants.GA_PRODUCT_SKU, Constants.GA_PRODUCT_NAME, Constants.GA_PRODUCT_CATEGORY, });

		String filter = "";
		
		if (Util.isNull(source)) {
			filter = getTransactionIdsFilter(transactions);
		} else if (CampaignDetailSource.Analytics.equals(source)) {
			filter = Constants.GA_ADWORDS_CAMPAIGN_ID + "!=(not set)";
		} else {
			filter = Constants.GA_SOURCE_MEDIUM + "=~^bing / cpc";
		}

		GetReportsResponse response = analyticsClient.getReport(startDate, endDate, metricsList, dimensionsList, filter);
		GoogleAnalyticsMapper.mapTransactionItemsData(response, startDate, endDate, campaignTransactions, transactionDetails, source);
	}
	
	
	public Long getTransactionCount(String startDate, String endDate) throws Exception {
		List<Metric> metricsList = getMetricsList(
				new String[] { Constants.GA_TRANSACTIONS });
		List<Dimension> dimensionsList = getDimensionsList(new String[] { Constants.GA_ADWORDS_CAMPAIGN_ID });

		String filter = Constants.GA_ADWORDS_CAMPAIGN_ID + "!=(not set)";

		GetReportsResponse response = analyticsClient.getReport(startDate, endDate, metricsList, dimensionsList, filter);
		return GoogleAnalyticsMapper.getTransactionCount(response);
	}
	
	public GetReportsResponse getCustomFilteredData(String startDate, String endDate, String[] metrics, String[] dimensions, String filtersExpression) throws IOException, GeneralSecurityException {
		List<Metric> metricsList = getMetricsList(metrics);
		List<Dimension> dimensionsList = getDimensionsList(dimensions);
		return analyticsClient.getReport(startDate, endDate, metricsList, dimensionsList, filtersExpression);
	}
	
	public SearchUserActivityResponse getUserActivity(String userId, String startDate, String endDate) throws GeneralSecurityException, IOException {
		return analyticsClient.getUserActivity(userId, startDate, endDate);
	}

	// Utility methods
	private List<Dimension> getDimensionsList(String[] dimensions) {
		List<Dimension> dimensionsList = new ArrayList<Dimension>();
		for (String dim : dimensions) {
			Dimension dimension = new Dimension().setName(dim);
			dimensionsList.add(dimension);
		}
		return dimensionsList;
	}

	private List<Metric> getMetricsList(String[] metrics) {
		List<Metric> metricsList = new ArrayList<Metric>();
		for (String met : metrics) {
			Metric metric = new Metric().setExpression(met);
			metricsList.add(metric);
		}
		return metricsList;
	}

	// Google analytics client
	private class AnalyticsClient {

		private static final String APPLICATION_NAME = "GR - Analytics Reporting";
		private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

		private AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException {

			File configFile = new ClassPathResource(settings.getConfigFileName()).getFile();
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(configFile))
					.createScoped(AnalyticsReportingScopes.all());

			// Construct the Analytics Reporting service object.
			return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
		}
		
		private Analytics initializeAnalytics() throws GeneralSecurityException, IOException {

			File configFile = new ClassPathResource(settings.getConfigFileName()).getFile();
		    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		    GoogleCredential credential = GoogleCredential
		        .fromStream(new FileInputStream(configFile))
		        .createScoped(AnalyticsScopes.all());

		    // Construct the Analytics service object.
		    return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
		        .setApplicationName(APPLICATION_NAME).build();
		  }
		
		private McfData getMcfReport(String startDate, String endDate, String metrics, String dimensions,
				String filterExpressions) throws IOException, GeneralSecurityException {
			Analytics analytics = initializeAnalytics();
			Get apiQuery = analytics.data().mcf().get("ga:" + settings.getViewId(), startDate, endDate, metrics)
					.setDimensions(dimensions).setFilters(filterExpressions);
			McfData mcfData = apiQuery.execute();
			return mcfData;
		}

		private GetReportsResponse getReport(String startDate, String endDate, List<Metric> metrics,
				List<Dimension> dimensions, String filterExpressions) throws IOException, GeneralSecurityException {

			AnalyticsReporting service = initializeAnalyticsReporting();

			// Create the DateRange object.
			DateRange dateRange = new DateRange();
			dateRange.setStartDate(startDate);
			dateRange.setEndDate(endDate);

			// Create the ReportRequest object.
			ReportRequest request = new ReportRequest().setViewId(settings.getViewId())
					.setDateRanges(Arrays.asList(dateRange)).setMetrics(metrics).setDimensions(dimensions)
					.setFiltersExpression(filterExpressions);

			ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
			requests.add(request);

			// Create the GetReportsRequest object.
			GetReportsRequest getReport = new GetReportsRequest().setReportRequests(requests);

			// Call the batchGet method.
			GetReportsResponse response = service.reports().batchGet(getReport).execute();

			// Return the response.
			return response;
		}
		
		private SearchUserActivityResponse getUserActivity(String userId, String startDate, String endDate) throws GeneralSecurityException, IOException {
			
			AnalyticsReporting service = initializeAnalyticsReporting();
			SearchUserActivityRequest searchUserActivityRequest = new SearchUserActivityRequest()
					.setDateRange(new DateRange().setStartDate(startDate).setEndDate(endDate))
					.setViewId(settings.getViewId())
					.setUser(new User().setUserId(userId));
			SearchUserActivityResponse response = service.userActivity().search(searchUserActivityRequest).execute();
			return response;
		}
	}

}
