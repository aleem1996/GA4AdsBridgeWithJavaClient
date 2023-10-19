package com.gr.dm.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.analyticsreporting.v4.model.SearchUserActivityResponse;
import com.gr.dm.core.dto.AdGroupListDto;
import com.gr.dm.core.dto.AdListDto;
import com.gr.dm.core.dto.BingCampaignListDto;
import com.gr.dm.core.dto.CampaignLpStatsRequest;
import com.gr.dm.core.dto.CampaignTransactionDto;
import com.gr.dm.core.dto.FacebookCampaignListDto;
import com.gr.dm.core.dto.GoogleCampaignListDto;
import com.gr.dm.core.dto.KeywordListDto;
import com.gr.dm.core.dto.LandingPageStatsDto;
import com.gr.dm.core.dto.report.LatestDateDto;
import com.gr.dm.core.dto.report.SourceCostDto;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.TransactionDetail;
import com.gr.dm.core.service.AdGroupService;
import com.gr.dm.core.service.AdService;
import com.gr.dm.core.service.AnalyticsSyncService;
import com.gr.dm.core.service.CampaignService;
import com.gr.dm.core.service.KeywordService;
import com.gr.dm.core.service.TransactionService;
import com.gr.dm.core.util.Constants;

/**
 * 
 * @author Aleem Malik
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2")
public class AnalyticsResource {

	@Autowired
	CampaignService campaignService;

	@Autowired
	TransactionService transactionService;

	@Autowired
	AdGroupService adGroupService;

	@Autowired
	AdService adService;

	@Autowired
	KeywordService keywordService;

	@Autowired
	AnalyticsSyncService analyticsSyncService;

	private final SimpleDateFormat analyticsDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@RequestMapping("/")
	public String hello() {
		return "Hello World! Welcome to reporting api for Digital Media";
	}

	@RequestMapping(value = "/google/campaigns", method = RequestMethod.GET)
	public GoogleCampaignListDto getGoogleCampaigns(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view) {

		return campaignService.getGoogleCampaigns(startDate, endDate, view);
	}

	@RequestMapping(value = "/campaigns/{id}/transactions", method = RequestMethod.GET)
	public List<CampaignTransactionDto> getGoogleCampaignTransactions(@PathVariable("id") String id,
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam("source") CampaignDetailSource campaignDetailSource) {

		return campaignService.getCampaignTransactions(id, startDate, endDate, campaignDetailSource);
	}

	@RequestMapping(value = "/campaigns/{campaignid}/adgroups", method = RequestMethod.GET)
	public AdGroupListDto getAdGroups(@PathVariable("campaignid") String campaignId,
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "source", required = false) CampaignSource campaignSource) {

		return adGroupService.getAdGroups(campaignId, startDate, endDate, campaignSource);
	}

	@RequestMapping(value = "/adgroups/{adgroupid}/ads", method = RequestMethod.GET)
	public AdListDto getAds(@PathVariable("adgroupid") String adGroupId,
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "source", required = false) CampaignSource campaignSource) {

		return adService.getAds(adGroupId, startDate, endDate, campaignSource);
	}

	@RequestMapping(value = "/adgroups/{adgroupid}/keywords", method = RequestMethod.GET)
	public KeywordListDto getKeywords(@PathVariable("adgroupid") String adGroupId,
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) {

		return keywordService.getKeywords(adGroupId, startDate, endDate);
	}

	@RequestMapping(value = "/fb/campaigns", method = RequestMethod.GET)
	public FacebookCampaignListDto getFacebookCampaigns(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) {

		return campaignService.getFacebookCampaigns(startDate, endDate, CampaignSource.Facebook);
	}

	@RequestMapping(value = "/email/campaigns", method = RequestMethod.GET)
	public FacebookCampaignListDto getEmailCampaigns(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) {

		return campaignService.getFacebookCampaigns(startDate, endDate, CampaignSource.Email);
	}
	
	@RequestMapping(value = "/bing/campaigns", method = RequestMethod.GET)
	public BingCampaignListDto getBingCampaigns(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "view", required = false , defaultValue = Constants.DEFAULT_VIEW_TYPE) String view) {

		return campaignService.getBingCampaigns(startDate, endDate, view);
	}

	@RequestMapping(value = "/transactions/{id}", method = RequestMethod.GET)
	public List<TransactionDetail> getTransactionDetails(@PathVariable("id") String transactionId) {

		return transactionService.getTransactionsDetail(transactionId);
	}

	@RequestMapping(value = "/latestdate", method = RequestMethod.GET)
	public LatestDateDto getDateOfLastFetchedData(@RequestParam(value = "source") CampaignDetailSource source) {

		return campaignService.getDateOfLastFetchedData(source);
	}
	
	@RequestMapping(value = "/duplicatetransactions", method = RequestMethod.GET)
	public List<CampaignTransactionDto> getDuplicateTransactions(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) {

		return transactionService.getDuplicateTransactions(startDate, endDate);
	}
	
	@RequestMapping(value = "/landingPageStats", method = RequestMethod.GET)
	public List<LandingPageStatsDto> getLandingPageStats(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate, @RequestParam(value = "campaign", required = false) String campaign, @RequestParam("path") List<String> paths) throws Exception {
		List<LandingPageStatsDto> landingPageStats = analyticsSyncService.getLandingPageStats(startDate, endDate, campaign, paths);
		return landingPageStats;
	}
	
	@RequestMapping(value = "/campaignLandingPageStats", method = RequestMethod.POST)
	public List<LandingPageStatsDto> getCampaignLandingPageStats(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate, @RequestBody List<CampaignLpStatsRequest> campaignLpStatsRequest) throws Exception {
		List<LandingPageStatsDto> landingPageStatsDtoList=  analyticsSyncService.getCampaignLandingPageStats(startDate, endDate, campaignLpStatsRequest);
		return landingPageStatsDtoList;
	}
	
	@GetMapping("/campaigncost")
	public ResponseEntity<List<SourceCostDto>> getCampaignCosts(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate){
		List<SourceCostDto> sourceCostDtoList = campaignService.getCost(startDate, endDate, CampaignDetailSource.Analytics, CampaignDetailSource.Facebook, CampaignDetailSource.Bing);
		return ResponseEntity.ok(sourceCostDtoList);
		
	}
	
	@GetMapping("/useractivity")
	public ResponseEntity<SearchUserActivityResponse> getUserActivity(@RequestParam("userid") String userId, @RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Optional<Date> startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Optional<Date> endDate) throws Exception {
        SearchUserActivityResponse searchUserActivityResponse = analyticsSyncService.getUserActivityReport(userId, startDate, endDate);
		return ResponseEntity.ok(searchUserActivityResponse);
	}
	
	@GetMapping("/stackadaptall")
	public ResponseEntity<Object> getStackAdaptAll(@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) throws Exception {
		
		analyticsSyncService.loadStackAdaptData(startDate, endDate);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/stackadapt/campaigns", method = RequestMethod.GET)
	public BingCampaignListDto getStackAdaptCampaigns(
			@RequestParam("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@RequestParam("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate) {

		return campaignService.getStackAdaptCampaigns(startDate, endDate);
	}
}