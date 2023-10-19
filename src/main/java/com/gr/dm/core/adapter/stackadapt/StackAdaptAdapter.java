package com.gr.dm.core.adapter.stackadapt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.gr.dm.core.adapter.Adapter;
import com.gr.dm.core.dto.StackAdaptCampaginStats;
import com.gr.dm.core.dto.StackAdaptCampaignDto;
import com.gr.dm.core.dto.StackAdaptDataDto;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.service.CampaignService;
import com.gr.dm.core.util.Util;
import com.gr.dm.integration.service.StackAdaptIntegrationService;

@Service
public class StackAdaptAdapter implements Adapter{

	@Autowired
	CampaignService campaignService;
	
	@Autowired
	private StackAdaptIntegrationService stackAdaptIntegrationService;
	
	@Autowired
	private StackAdaptAdapterSettings stackAdaptAdapterSettings;
	
	
	public void loadAllStackAdaptCampaigns(List<Campaign> campaigns, List<CampaignDetail> campaignDetails, String startDateString, String endDateString) {
		
		StackAdaptDataDto[] stackAdaptCampagins = null;
		Integer page = 1;
		
		HttpHeaders headers = buildHeader();
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
		
		stackAdaptCampagins = getStackAdaptCampaigns30(stackAdaptCampagins, requestEntity, page);
		
		List<StackAdaptCampaginStats> stackAdaptCampaginStatsList = new ArrayList<StackAdaptCampaginStats>();
		
		stackAdaptIntegrationService.getAllCampaignsStats(stackAdaptCampagins, stackAdaptCampaginStatsList, requestEntity, startDateString, endDateString);

		StackAdaptMapper.mapCampaignData(stackAdaptCampagins, stackAdaptCampaginStatsList, campaigns, campaignDetails, startDateString, endDateString);
	}
	
	private StackAdaptDataDto[] getStackAdaptCampaigns30(StackAdaptDataDto[] stackAdaptCampagins, HttpEntity<String> requestEntity, Integer page) {
		
		int totalCampaigns = 0;
		StackAdaptCampaignDto stackAdaptAllCampaigns = stackAdaptIntegrationService.getAllStackAdaptCampaigns(requestEntity, page);
		if (Util.isNotNull(stackAdaptAllCampaigns) && Util.isNotNull(stackAdaptAllCampaigns.getTotal_campaigns())) {
		totalCampaigns = stackAdaptAllCampaigns.getTotal_campaigns();
		stackAdaptCampagins = (StackAdaptDataDto[]) ArrayUtils.addAll(stackAdaptCampagins, stackAdaptAllCampaigns.getData());
		}
		
		if(totalCampaigns > stackAdaptCampagins.length) {
			page++;
			stackAdaptCampagins = getStackAdaptCampaigns30(stackAdaptCampagins, requestEntity, page);
		}
		
		return stackAdaptCampagins;
	}
	
	public Long getStackAdaptTransactionCount(String startDate, String endDate) {
		Long totalConversions = 0L;
		Integer page = 1;
		
		HttpHeaders headers = buildHeader();
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
		
		StackAdaptDataDto[] stackAdaptCampagins =null;
		stackAdaptCampagins = getStackAdaptCampaigns30(stackAdaptCampagins, requestEntity, page);
		
		if (Util.isNotNull(stackAdaptCampagins)) {
			totalConversions = stackAdaptIntegrationService.getTransactionCount(stackAdaptCampagins, requestEntity, startDate, endDate);
		}
		
		return totalConversions;
	}
	
	private HttpHeaders buildHeader() {
	       HttpHeaders headers = new HttpHeaders();
	        headers.set("X-Authorization", stackAdaptAdapterSettings.getApiKey());
	        return headers;
	    }
	
}
