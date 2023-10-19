package com.gr.dm.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gr.dm.core.dto.BingCampaignStatsDto;
import com.gr.dm.core.dto.FbCampaignStatsDto;
import com.gr.dm.core.dto.StatsDto;
import com.gr.dm.core.dto.report.CampaignPerformanceDto;
import com.gr.dm.core.dto.report.CampaignSummaryDto;
import com.gr.dm.core.dto.report.KeywordPerformanceDto;
import com.gr.dm.core.dto.report.MembershipStatsDto;
import com.gr.dm.core.dto.report.StackedReportDto;
import com.gr.dm.core.dto.report.WeeklyCostDto;
import com.gr.dm.core.dto.report.WeeklyRevenueDto;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.service.CampaignService;
import com.gr.dm.core.service.KeywordService;
import com.gr.dm.core.service.ReportSummaryService;
import com.gr.dm.core.service.TransactionService;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.ExcelExportUtil;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "api/v2/summary")
public class ReportSummaryResource {

	@Autowired
	ReportSummaryService reportSummaryService;
	
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	KeywordService keywordService;
	
	@Autowired
	TransactionService transactionService;
	
	private final List<String> frequencies = Arrays.asList("monthly", "yearly", "weekly");

	@RequestMapping(value = "/costvsrevenue", method = RequestMethod.GET)
	public Map<String, StackedReportDto> getCostVsRevenueReport(
			@RequestParam(value = "source", required = false) CampaignDetailSource source,
			@RequestParam(value = "frequency", required = false , defaultValue = "month") String frequency,
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view,
			@RequestParam(value = "stattype", required = false , defaultValue = Constants.DEFAULT_STAT_TYPE) String statType) {
		
		if("click_centric".equals(statType)) {
			return frequencies.parallelStream()
					.collect(Collectors.toMap(_frequency -> _frequency, _frequency -> reportSummaryService
							.getCostVsRevenueReportByClick(source, _frequency, startDate, endDate)));
		} else {
			return frequencies.parallelStream()
					.collect(Collectors.toMap(_frequency -> _frequency, _frequency -> reportSummaryService
							.getCostVsRevenueReport(source, _frequency, startDate, endDate,  view, statType)));
		}
	}
	
	@RequestMapping(value = "/productsales", method = RequestMethod.GET)
	public StackedReportDto getProductSalesReport(
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "stattype", required = false , defaultValue = Constants.DEFAULT_STAT_TYPE) String statType) {

		StackedReportDto reportDto =  reportSummaryService.getProductSalesReport(startDate, endDate, statType);
		return reportDto;
	}
	
	@RequestMapping(value = "/campaignsummary", method = RequestMethod.GET)
	public List<CampaignSummaryDto> getCampaignSummaryReport(
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "stattype", required = false , defaultValue = Constants.DEFAULT_STAT_TYPE) String statType) {

		return campaignService.getCampaignSummary(startDate, endDate, statType);
	}
	
	@RequestMapping(value = "/roi", method = RequestMethod.GET)
	public Map<String, StackedReportDto> getRoiSummaryReport(
			@RequestParam(value = "source",  required = false) String[] sourceArray,
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view,
			@RequestParam(value = "stattype", required = false , defaultValue = Constants.DEFAULT_STAT_TYPE) String statType) {
		
		
		return frequencies.parallelStream()
				.collect(Collectors.toMap(frequency -> frequency,
						frequency -> reportSummaryService.getRoiReport(frequency, startDate, endDate,
								(Util.isNull(sourceArray) || sourceArray.length == 0) ? getAllSources() : sourceArray,
								view, statType)));

	}

	
	@RequestMapping(value = "/campaignperformance", method = RequestMethod.GET)
	public List<CampaignPerformanceDto> getCampaignPerformanceReport(
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view) {

		return campaignService.getCampaignPerformanceReport(startDate, endDate, view);
	}
	
	@RequestMapping(value = "/keywordperformance", method = RequestMethod.GET)
	public List<KeywordPerformanceDto> getKeywordPerformanceReport(
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) {

		return keywordService.getKeywordPerformanceReport(startDate, endDate);
	}
	
	@RequestMapping(value = "/exportstats", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> exportStats(
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "stattype", required = false , defaultValue = Constants.DEFAULT_STAT_TYPE) String statType,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view,
			@RequestParam(value = "source",  required = false) List<CampaignDetailSource> sources) throws IOException {

		FbCampaignStatsDto fbCampaignStatsDto = campaignService.getFacebookCampaignStats(startDate, endDate, CampaignSource.Facebook);
		StatsDto googleCampaignStatsDto = campaignService.getGoogleCampaignStats(startDate, endDate, Constants.PARTIAL_VIEW.equalsIgnoreCase(view) ? Constants.FULL_VIEW : view);
		BingCampaignStatsDto bingCampaignStatsDto = campaignService.getBingCampaignStats(startDate, endDate, Constants.PARTIAL_VIEW.equalsIgnoreCase(view) ? Constants.FULL_VIEW : view);
		List<CampaignSummaryDto> campaignSummaryDtoList = campaignService.getCampaignSummary(startDate, endDate, statType);
		
		List<MembershipStatsDto> membershipStatsList = transactionService.getMembershipStats(startDate, endDate);
		
		List<WeeklyCostDto> weeklyCostDtos = campaignService.getCostByWeek(startDate, endDate);
		List<WeeklyRevenueDto> weeklyRevenueDtos = campaignService.getRevenueByWeek(startDate, endDate);
		
		List<CampaignSummaryDto> campaignSummaryDtoListCopy = new ArrayList<CampaignSummaryDto>(campaignSummaryDtoList);
		for (Iterator<CampaignSummaryDto> iterator = campaignSummaryDtoListCopy.iterator(); iterator.hasNext(); ) {
			CampaignSummaryDto summary = iterator.next();
			if (Util.isNotNull(sources)
					&& !sources.contains(CampaignDetailSource.fromValue(summary.getCampaignDetailSource()))) {
				iterator.remove();
			} else {
				summary.setRevenue("partial".equals(view) ? summary.getRevenue()
						: "unique".equals(view) || "weekly_stats".equals(statType)
								|| "purchase_centric".equals(statType) ? summary.getUniqueAssistedConversionRevenue()
										: summary.getAssistedConversionRevenue());
				summary.setConversions("partial".equals(view) ? summary.getConversions()
						: "unique".equals(view) || "weekly_stats".equals(statType)
								|| "purchase_centric".equals(statType) ? summary.getUniqueAssistedConversions()
										: summary.getAssistedConversions());
			}
		}
		
		byte[] byteArray = ExcelExportUtil.exportCampaignStats(campaignSummaryDtoListCopy, fbCampaignStatsDto, bingCampaignStatsDto, googleCampaignStatsDto, membershipStatsList, weeklyCostDtos, weeklyRevenueDtos, startDate, endDate);
		
		String fileName = "Stats.xlsx";
		
		return ResponseEntity.ok()
	            .contentLength(byteArray.length)
	            .header("Content-Disposition","attachment; filename=" + fileName )
	            .body(byteArray);
	}
	
	@RequestMapping(value = "/profitandloss", method = RequestMethod.GET)
	public Map<String, StackedReportDto> getProfileAndLossReport(
			@RequestParam(value = "source",  required = false) String[] sourceArray,
			@RequestParam("from") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("to") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view,
			@RequestParam(value = "stattype", required = false , defaultValue = Constants.DEFAULT_STAT_TYPE) String statType) {

		
		return frequencies.parallelStream()
				.collect(Collectors.toMap(frequency -> frequency,
						frequency -> reportSummaryService.getProfileLossReport(frequency, startDate, endDate,
								(Util.isNull(sourceArray) || sourceArray.length == 0) ? getAllSources() : sourceArray,
								view, statType)));
	}
	
	private String[] getAllSources() {
		return new String[] { CampaignDetailSource.Bing.toString(), CampaignDetailSource.Analytics.toString(),
				CampaignDetailSource.Email.toString(), CampaignDetailSource.Facebook.toString() };
	}
}