/**
 * 
 */
package com.gr.dm.core.adapter.adwords;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.api.ads.adwords.axis.factory.AdWordsServices;
import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.adwords.lib.client.reporting.ReportingConfiguration;
import com.google.api.ads.adwords.lib.factory.AdWordsServicesInterface;
import com.google.api.ads.adwords.lib.jaxb.v201809.DateRange;
import com.google.api.ads.adwords.lib.jaxb.v201809.DownloadFormat;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinition;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinitionDateRangeType;
import com.google.api.ads.adwords.lib.jaxb.v201809.ReportDefinitionReportType;
import com.google.api.ads.adwords.lib.jaxb.v201809.Selector;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponse;
import com.google.api.ads.adwords.lib.utils.ReportDownloadResponseException;
import com.google.api.ads.adwords.lib.utils.ReportException;
import com.google.api.ads.adwords.lib.utils.v201809.ReportDownloaderInterface;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.conf.ConfigurationLoadException;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.client.auth.oauth2.Credential;
import com.gr.dm.core.adapter.Adapter;
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
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.Util;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author Aleem Malik
 */
@Service
public class AdWordsAdapter implements Adapter {

	@Autowired
	private AdWordsAdapterSettings settings;

	public void loadCampaignData(List<Campaign> campaignsList, List<CampaignDetail> campaignDetailList, String startDate, String endDate) throws Exception {
		ReportDownloaderInterface reportDownloader = ReportDownloaderClient.getReportDownloaderInstance(settings);
		ReportDefinition reportDefinition = getReportDefination(startDate, endDate, Constants.ADWORDS_CAMPAIGN_REPORT_COLUMNS, ReportDefinitionReportType.CAMPAIGN_PERFORMANCE_REPORT);
		ReportDownloadResponse reportResponse = reportDownloader.downloadReport(reportDefinition);
		CSVReader reader = Util.getCSVReader(reportResponse.getAsString());
		AdWordsMapper.mapCampaignData(reader, startDate, endDate, campaignsList, campaignDetailList, CampaignSource.Google, CampaignDetailSource.Adwords);
	}
	
	public void loadAdGroupData(List<AdGroup> adGroupList, List<AdGroupDetail> adGroupDetailList, String startDate, String endDate) throws Exception {
		ReportDownloaderInterface reportDownloader = ReportDownloaderClient.getReportDownloaderInstance(settings);
		ReportDefinition reportDefinition = getReportDefination(startDate, endDate, Constants.ADWORDS_ADGROUP_REPORT_COLUMNS, ReportDefinitionReportType.ADGROUP_PERFORMANCE_REPORT);
		ReportDownloadResponse reportResponse = reportDownloader.downloadReport(reportDefinition);
		CSVReader reader = Util.getCSVReader(reportResponse.getAsString());
		AdWordsMapper.mapAdGroupData(reader, startDate, endDate, adGroupList, adGroupDetailList, CampaignSource.Google);
	}
	
	public void loadAdData(List<Ad> adList, List<AdDetail> adDetailList, String startDate, String endDate) throws Exception {
		ReportDownloaderInterface reportDownloader = ReportDownloaderClient.getReportDownloaderInstance(settings);
		ReportDefinition reportDefinition = getReportDefination(startDate, endDate, Constants.ADWORDS_AD_REPORT_COLUMNS, ReportDefinitionReportType.AD_PERFORMANCE_REPORT);
		ReportDownloadResponse reportResponse = reportDownloader.downloadReport(reportDefinition);
		CSVReader reader = Util.getCSVReader(reportResponse.getAsString());
		AdWordsMapper.mapAdData(reader, startDate, endDate, adList, adDetailList, CampaignSource.Google);
	}
	
	public void loadKeywordData(List<Keyword> keywordList, List<KeywordDetail> keywordDetailList, String startDate, String endDate) throws Exception {
		ReportDownloaderInterface reportDownloader = ReportDownloaderClient.getReportDownloaderInstance(settings);
		ReportDefinition reportDefinition = getReportDefination(startDate, endDate, Constants.ADWORDS_KEYWORD_REPORT_COLUMNS, ReportDefinitionReportType.KEYWORDS_PERFORMANCE_REPORT);
		ReportDownloadResponse reportResponse = reportDownloader.downloadReport(reportDefinition);
		CSVReader reader = Util.getCSVReader(reportResponse.getAsString());
		AdWordsMapper.mapKeywordData(reader, startDate, endDate, keywordList, keywordDetailList, CampaignSource.Google);
	}
	
	public Long getTransactionCount(String startDate, String endDate) throws Exception {
		ReportDownloaderInterface reportDownloader = ReportDownloaderClient.getReportDownloaderInstance(settings);
		ReportDefinition reportDefinition = getReportDefination(startDate, endDate, Arrays.asList(Constants.ADWORDS_CAMPAIGN_REPORT_COLUMNS.get(1), Constants.ADWORDS_CAMPAIGN_REPORT_COLUMNS.get(2)), ReportDefinitionReportType.KEYWORDS_PERFORMANCE_REPORT);
		ReportDownloadResponse reportResponse = reportDownloader.downloadReport(reportDefinition);
		CSVReader reader = Util.getCSVReader(reportResponse.getAsString());
		return AdWordsMapper.getTransactionCount(reader);
	}

	private ReportDefinition getReportDefination(String startDate, String endDate, List<String> columns, ReportDefinitionReportType reportDefinitionReportType) {
		Selector selector = new Selector();
		selector.getFields().addAll(columns);
		DateRange dateRange = new DateRange();
		dateRange.setMin(startDate);
		dateRange.setMax(endDate);
		selector.setDateRange(dateRange);

		// Create report definition.
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setReportName("DigitalMedia-" + System.currentTimeMillis());
		reportDefinition.setDateRangeType(ReportDefinitionDateRangeType.CUSTOM_DATE);
		reportDefinition.setReportType(reportDefinitionReportType);
		reportDefinition.setDownloadFormat(DownloadFormat.CSV);
		reportDefinition.setSelector(selector);
		return reportDefinition;
	}

	public static void runExample(AdWordsServicesInterface adWordsServices, AdWordsSession session, String reportFile)
			throws ReportDownloadResponseException, ReportException, IOException {

	}

	static class ReportDownloaderClient {

		private static ReportDownloaderInterface INSTANCE = null;

		private ReportDownloaderClient() {
		}

		static ReportDownloaderInterface getReportDownloaderInstance(AdWordsAdapterSettings settings)
				throws IOException, OAuthException, ValidationException, ConfigurationLoadException {

			if (Util.isNull(INSTANCE)) {
				synchronized (ReportDownloaderInterface.class) {

					if (Util.isNull(INSTANCE)) {
						File configFile = new ClassPathResource(settings.getConfigFileName()).getFile();
						Credential oAuth2Credential = new OfflineCredentials.Builder().forApi(Api.ADWORDS).fromFile(configFile).build().generateCredential();
						AdWordsSession session = new AdWordsSession.Builder().fromFile(configFile).withOAuth2Credential(oAuth2Credential).build();

						AdWordsServicesInterface adWordsServices = AdWordsServices.getInstance();

						ReportingConfiguration reportingConfiguration = new ReportingConfiguration.Builder()
								.skipReportHeader(true).skipColumnHeader(false).skipReportSummary(true)
								.includeZeroImpressions(false).build();

						session.setReportingConfiguration(reportingConfiguration);
						INSTANCE = adWordsServices.getUtility(session, ReportDownloaderInterface.class);
					}
				}
			}
			return INSTANCE;
		}
	}
}
