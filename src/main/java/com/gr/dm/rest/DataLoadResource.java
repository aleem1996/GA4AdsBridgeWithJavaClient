package com.gr.dm.rest;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.service.AnalyticsSyncService;
import com.gr.dm.core.service.CacheService;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2")
public class DataLoadResource {

	@Autowired
	private AnalyticsSyncService analyticsSyncService;
	
	@Autowired
	private CacheService cacheService;

	@RequestMapping(value = "/load", method = RequestMethod.GET)
	public String loadGoogleAnalyticsData() throws Exception {

		analyticsSyncService.loadYesterdayData();

		return "Data Loading call initiated successfully";
	}

	@RequestMapping(value = "/loadcompletedata", method = RequestMethod.GET)
	public String loadCompleteData() throws Exception {

		analyticsSyncService.loadCompleteData();

		return "Data Loading call initiated successfully";
	}

	@RequestMapping(value = "/loaddatabetween/{fromdate}/{todate}", method = RequestMethod.GET)
	public String loadDataBetween(
			@PathVariable("fromdate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date startDate,
			@PathVariable("todate") @DateTimeFormat(pattern = Constants.DATE_FORMAT) Date endDate,
			@RequestParam(value = "source", required = false) CampaignSource campaignSource) throws Exception {

		if (Util.isNull(campaignSource)) {
			analyticsSyncService.loadDataBetween(startDate, endDate);
		} else {
			analyticsSyncService.loadDataBetween(startDate, endDate, campaignSource);
		}

		return "Data Loading call initiated successfully";
	}
	
	@RequestMapping(value = "/clearcache", method = RequestMethod.GET)
	public String clearCache() {
		cacheService.clearCache();
		return "Cache cleared successfully";
	}

}