/**
 * 
 */
package com.gr.dm.core.adapter.bing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gr.dm.core.adapter.Adapter;
import com.gr.dm.core.adapter.adwords.AdWordsMapper;
import com.gr.dm.core.entity.Ad;
import com.gr.dm.core.entity.AdDetail;
import com.gr.dm.core.entity.AdGroup;
import com.gr.dm.core.entity.AdGroupDetail;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.Keyword;
import com.gr.dm.core.entity.KeywordDetail;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;
import com.microsoft.bingads.ApiEnvironment;
import com.microsoft.bingads.AuthorizationData;
import com.microsoft.bingads.OAuthTokens;
import com.microsoft.bingads.OAuthWebAuthCodeGrant;
import com.microsoft.bingads.v13.reporting.AccountThroughAdGroupReportScope;
import com.microsoft.bingads.v13.reporting.AccountThroughCampaignReportScope;
import com.microsoft.bingads.v13.reporting.AdGroupPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.AdGroupPerformanceReportRequest;
import com.microsoft.bingads.v13.reporting.AdPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.AdPerformanceReportRequest;
import com.microsoft.bingads.v13.reporting.ArrayOfAdGroupPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.ArrayOfAdPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.ArrayOfCampaignPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.ArrayOfKeywordPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.ArrayOflong;
import com.microsoft.bingads.v13.reporting.CampaignPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.CampaignPerformanceReportRequest;
import com.microsoft.bingads.v13.reporting.Date;
import com.microsoft.bingads.v13.reporting.KeywordPerformanceReportColumn;
import com.microsoft.bingads.v13.reporting.KeywordPerformanceReportRequest;
import com.microsoft.bingads.v13.reporting.ReportAggregation;
import com.microsoft.bingads.v13.reporting.ReportFormat;
import com.microsoft.bingads.v13.reporting.ReportRequest;
import com.microsoft.bingads.v13.reporting.ReportTime;
import com.microsoft.bingads.v13.reporting.ReportingDownloadParameters;
import com.microsoft.bingads.v13.reporting.ReportingServiceManager;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Aleem Malik
 */
@Service
public class BingAdapter implements Adapter {

	
	private static BingAdapterSettings settings;
	private static ServletContext context;
	
	@Autowired
	public BingAdapter(BingAdapterSettings settings, ServletContext context) {
		BingAdapter.settings = settings;
		BingAdapter.context = context;
	}

	public void loadCampaignData(List<Campaign> campaignsList, List<CampaignDetail> campaignDetailList,
			String startDate, String endDate) throws Exception {
		ReportingServiceManager reportingServiceManager = ReportDownloaderClient.getReportServiceManagerInstance();

		List<CampaignPerformanceReportColumn> columnList = Arrays.asList(CampaignPerformanceReportColumn.CAMPAIGN_NAME,
				CampaignPerformanceReportColumn.CAMPAIGN_ID, CampaignPerformanceReportColumn.CONVERSIONS,
				CampaignPerformanceReportColumn.REVENUE, CampaignPerformanceReportColumn.SPEND,
				CampaignPerformanceReportColumn.IMPRESSIONS, CampaignPerformanceReportColumn.CLICKS, CampaignPerformanceReportColumn.CAMPAIGN_STATUS);

		ReportRequest reportRequest = ReportDownloaderClient
				.getCampaignPerformanceReportRequest(reportingServiceManager, columnList, startDate, endDate);
		ReportingDownloadParameters reportingDownloadParameters = new ReportingDownloadParameters();
		reportingDownloadParameters.setReportRequest(reportRequest);
		reportingDownloadParameters.setResultFileName("bing_campaign_performace_report");
		File reportFile = ReportDownloaderClient.downloadReportAsync(reportingDownloadParameters);
		if (!Util.isNull(reportFile)) {

			CSVReader reader = Util.getCSVReader(reportFile);

			AdWordsMapper.mapCampaignData(reader, startDate, endDate, campaignsList, campaignDetailList,
					CampaignSource.Bing, CampaignDetailSource.Bing);
		}
	}

	public void loadAdGroupData(List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList, String startDate,
			String endDate) throws Exception {
		ReportingServiceManager reportingServiceManager = ReportDownloaderClient
				.getReportServiceManagerInstance();
		ReportRequest reportRequest = ReportDownloaderClient.getAdGroupPerformanceReportRequest(reportingServiceManager,
				startDate, endDate);
		ReportingDownloadParameters reportingDownloadParameters = new ReportingDownloadParameters();
		reportingDownloadParameters.setReportRequest(reportRequest);
		reportingDownloadParameters.setResultFileName("bing_adgroup_performance_report");
		File reportFile = ReportDownloaderClient.downloadReportAsync(reportingDownloadParameters);
		if (!Util.isNull(reportFile)) {

			CSVReader reader = Util.getCSVReader(reportFile);

			AdWordsMapper.mapAdGroupData(reader, startDate, endDate, adGroupList, adGroupDetailList,
					CampaignSource.Bing);
		}
	}

	public void loadAdData(List<Ad> adList, List<AdDetail> adDetailList, String startDate, String endDate)
			throws Exception {
		ReportingServiceManager reportingServiceManager = ReportDownloaderClient
				.getReportServiceManagerInstance();
		ReportRequest reportRequest = ReportDownloaderClient.getAdPerformanceReportRequest(reportingServiceManager,
				startDate, endDate);
		ReportingDownloadParameters reportingDownloadParameters = new ReportingDownloadParameters();
		reportingDownloadParameters.setReportRequest(reportRequest);
		reportingDownloadParameters.setResultFileName("bing_ad_performace_report");
		File reportFile = ReportDownloaderClient.downloadReportAsync(reportingDownloadParameters);
		if (!Util.isNull(reportFile)) {

			CSVReader reader = Util.getCSVReader(reportFile);

			AdWordsMapper.mapAdData(reader, startDate, endDate, adList, adDetailList, CampaignSource.Bing);
		}
	}

	public void loadKeywordData(List<Keyword> keywordList, List<KeywordDetail> keywordDetailList, String startDate,
			String endDate) throws Exception {
		ReportingServiceManager reportingServiceManager = ReportDownloaderClient
				.getReportServiceManagerInstance();
		ReportRequest reportRequest = ReportDownloaderClient.getKeywordPerformanceReportRequest(reportingServiceManager,
				startDate, endDate);
		ReportingDownloadParameters reportingDownloadParameters = new ReportingDownloadParameters();
		reportingDownloadParameters.setReportRequest(reportRequest);
		reportingDownloadParameters.setResultFileName("bing_keyword_performace_report");
		File reportFile = ReportDownloaderClient.downloadReportAsync(reportingDownloadParameters);
		if (!Util.isNull(reportFile)) {

			CSVReader reader = Util.getCSVReader(reportFile);

			AdWordsMapper.mapKeywordData(reader, startDate, endDate, keywordList, keywordDetailList,
					CampaignSource.Bing);
		}
	}
	
	public Long getTransactionCount(String startDate, String endDate) throws Exception {
		ReportingServiceManager reportingServiceManager = ReportDownloaderClient.getReportServiceManagerInstance();
		List<CampaignPerformanceReportColumn> columnList = Arrays.asList(CampaignPerformanceReportColumn.CAMPAIGN_ID, CampaignPerformanceReportColumn.CONVERSIONS);
		ReportRequest reportRequest = ReportDownloaderClient
				.getCampaignPerformanceReportRequest(reportingServiceManager, columnList, startDate, endDate);
		ReportingDownloadParameters reportingDownloadParameters = new ReportingDownloadParameters();
		reportingDownloadParameters.setReportRequest(reportRequest);
		reportingDownloadParameters.setResultFileName("bing_transaction_count_report");
		File reportFile = ReportDownloaderClient.downloadReportAsync(reportingDownloadParameters);
		if (!Util.isNull(reportFile)) {

			CSVReader reader = Util.getCSVReader(reportFile);

			return AdWordsMapper.getTransactionCount(reader);
		}
		return 0L;
	}

	static class ReportDownloaderClient {

		private static AuthorizationData authorizationData = null;

		private static ReportingServiceManager INSTANCE = null;

		private static ReportFormat reportFileFormat = ReportFormat.CSV;

		private static final int TimeoutInMilliseconds = 3600000;
		
		private static final String baseFolderPath = "reports" + File.separator + "bing";

		private ReportDownloaderClient() {
		}

		static ReportingServiceManager getReportServiceManagerInstance() throws Exception {

			if (Util.isNull(INSTANCE)) {
				synchronized (ReportingServiceManager.class) {

					if (Util.isNull(INSTANCE)) {

						INSTANCE = new ReportingServiceManager(getAuthorizationDataInstance(),
								ApiEnvironment.PRODUCTION);
						INSTANCE.setStatusPollIntervalInMilliseconds(5000);
					}
				}
			}
			return INSTANCE;
		}

		private static AuthorizationData getAuthorizationDataInstance()
				throws MalformedURLException {
			if (Util.isNull(authorizationData)) {
				authorizationData = new AuthorizationData();
				authorizationData.setDeveloperToken(settings.getDeveloperToken());
				authorizationData
						.setAuthentication(new OAuthWebAuthCodeGrant(settings.getClientId(), settings.getClientSecret(),
								new URL(settings.getRedirectionUrl()), new OAuthTokens(settings.getAccessToken(),
										settings.getTokenExpiresIn(), settings.getRefreshToken())));
				authorizationData.setCustomerId(settings.getCustomerId());
				authorizationData.setAccountId(settings.getAccountId());
			}
			return authorizationData;
		}

		static File downloadReportAsync(ReportingDownloadParameters reportingDownloadParameters) throws ExecutionException, InterruptedException, TimeoutException, IOException {

			reportingDownloadParameters.setOverwriteResultFile(true);
			File reportFolder = new File(context.getRealPath(baseFolderPath));
			if(!reportFolder.exists()) {
				reportFolder.mkdirs();
			}
			reportingDownloadParameters.setResultFileDirectory(reportFolder);
			File reportFile = INSTANCE.downloadFileAsync(reportingDownloadParameters, null).get(TimeoutInMilliseconds,
					TimeUnit.MILLISECONDS);
			return reportFile;
		}

		static ReportRequest getCampaignPerformanceReportRequest(ReportingServiceManager reportingServiceManager, List<CampaignPerformanceReportColumn> columnsList,
				String startDate, String endDate) {
			CampaignPerformanceReportRequest report = new CampaignPerformanceReportRequest();

			report.setFormat(reportFileFormat);
			report.setReportName("Bing - Campaign Performance Report");
			report.setAggregation(ReportAggregation.DAILY);
			report.setExcludeColumnHeaders(false);
			report.setExcludeReportFooter(true);
			report.setExcludeReportHeader(true);

			ArrayOflong accountIds = new ArrayOflong();
			accountIds.getLongs().add(reportingServiceManager.getAuthorizationData().getAccountId());

			report.setScope(new AccountThroughCampaignReportScope());
			report.getScope().setAccountIds(accountIds);

			report.setTime(new ReportTime());
			Date bingStartDate = DateUtil.getBingDate(DateUtil.getDate(startDate));
			Date bingEndDate = DateUtil.getBingDate(DateUtil.getDate(endDate));
			report.getTime().setCustomDateRangeStart(bingStartDate);
			report.getTime().setCustomDateRangeEnd(bingEndDate);

			ArrayOfCampaignPerformanceReportColumn campaignPerformanceReportColumns = new ArrayOfCampaignPerformanceReportColumn();
			campaignPerformanceReportColumns.getCampaignPerformanceReportColumns()
					.addAll(columnsList);
			report.setColumns(campaignPerformanceReportColumns);

			return report;
		}

		static ReportRequest getAdGroupPerformanceReportRequest(ReportingServiceManager reportingServiceManager,
				String startDate, String endDate) {
			AdGroupPerformanceReportRequest report = new AdGroupPerformanceReportRequest();

			report.setFormat(reportFileFormat);
			report.setReportName("Bing - AdGroup Performance Report");
			report.setAggregation(ReportAggregation.DAILY);
			report.setExcludeColumnHeaders(false);
			report.setExcludeReportFooter(true);
			report.setExcludeReportHeader(true);

			ArrayOflong accountIds = new ArrayOflong();
			accountIds.getLongs().add(reportingServiceManager.getAuthorizationData().getAccountId());

			report.setScope(new AccountThroughAdGroupReportScope());
			report.getScope().setAccountIds(accountIds);

			report.setTime(new ReportTime());
			Date bingStartDate = DateUtil.getBingDate(DateUtil.getDate(startDate));
			Date bingEndDate = DateUtil.getBingDate(DateUtil.getDate(endDate));
			report.getTime().setCustomDateRangeStart(bingStartDate);
			report.getTime().setCustomDateRangeEnd(bingEndDate);

			ArrayOfAdGroupPerformanceReportColumn adGroupPerformanceReportColumns = new ArrayOfAdGroupPerformanceReportColumn();
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.CAMPAIGN_ID);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.AD_GROUP_ID);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.AD_GROUP_NAME);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.CONVERSIONS);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.REVENUE);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.SPEND);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.IMPRESSIONS);
			adGroupPerformanceReportColumns.getAdGroupPerformanceReportColumns()
					.add(AdGroupPerformanceReportColumn.CLICKS);
			report.setColumns(adGroupPerformanceReportColumns);

			return report;
		}

		static ReportRequest getAdPerformanceReportRequest(ReportingServiceManager reportingServiceManager,
				String startDate, String endDate) {
			AdPerformanceReportRequest report = new AdPerformanceReportRequest();

			report.setFormat(reportFileFormat);
			report.setReportName("Bing - Ad Performance Report");
			report.setAggregation(ReportAggregation.DAILY);
			report.setExcludeColumnHeaders(false);
			report.setExcludeReportFooter(true);
			report.setExcludeReportHeader(true);

			ArrayOflong accountIds = new ArrayOflong();
			accountIds.getLongs().add(reportingServiceManager.getAuthorizationData().getAccountId());

			report.setScope(new AccountThroughAdGroupReportScope());
			report.getScope().setAccountIds(accountIds);

			report.setTime(new ReportTime());
			Date bingStartDate = DateUtil.getBingDate(DateUtil.getDate(startDate));
			Date bingEndDate = DateUtil.getBingDate(DateUtil.getDate(endDate));
			report.getTime().setCustomDateRangeStart(bingStartDate);
			report.getTime().setCustomDateRangeEnd(bingEndDate);

			ArrayOfAdPerformanceReportColumn adGroupPerformanceReportColumns = new ArrayOfAdPerformanceReportColumn();
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.AD_ID);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.AD_GROUP_ID);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.TITLE_PART_1);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.TITLE_PART_2);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.CONVERSIONS);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.REVENUE);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.SPEND);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.IMPRESSIONS);
			adGroupPerformanceReportColumns.getAdPerformanceReportColumns().add(AdPerformanceReportColumn.CLICKS);
			report.setColumns(adGroupPerformanceReportColumns);

			return report;
		}

		static ReportRequest getKeywordPerformanceReportRequest(ReportingServiceManager reportingServiceManager,
				String startDate, String endDate) {
			KeywordPerformanceReportRequest report = new KeywordPerformanceReportRequest();

			report.setFormat(reportFileFormat);
			report.setReportName("Bing - Keyword Performance Report");
			report.setAggregation(ReportAggregation.DAILY);
			report.setExcludeColumnHeaders(false);
			report.setExcludeReportFooter(true);
			report.setExcludeReportHeader(true);

			ArrayOflong accountIds = new ArrayOflong();
			accountIds.getLongs().add(reportingServiceManager.getAuthorizationData().getAccountId());

			report.setScope(new AccountThroughAdGroupReportScope());
			report.getScope().setAccountIds(accountIds);

			report.setTime(new ReportTime());
			Date bingStartDate = DateUtil.getBingDate(DateUtil.getDate(startDate));
			Date bingEndDate = DateUtil.getBingDate(DateUtil.getDate(endDate));
			report.getTime().setCustomDateRangeStart(bingStartDate);
			report.getTime().setCustomDateRangeEnd(bingEndDate);

			ArrayOfKeywordPerformanceReportColumn adGroupPerformanceReportColumns = new ArrayOfKeywordPerformanceReportColumn();
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.KEYWORD_ID);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.AD_GROUP_ID);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.KEYWORD);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.CONVERSIONS);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.REVENUE);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.SPEND);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.IMPRESSIONS);
			adGroupPerformanceReportColumns.getKeywordPerformanceReportColumns()
					.add(KeywordPerformanceReportColumn.CLICKS);
			report.setColumns(adGroupPerformanceReportColumns);

			return report;
		}
	}
}
