package com.gr.dm.integration.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.gr.dm.core.adapter.stackadapt.StackAdaptAdapterSettings;
import com.gr.dm.core.dto.StackAdaptCampaginStats;
import com.gr.dm.core.dto.StackAdaptCampaginStatsDto;
import com.gr.dm.core.dto.StackAdaptCampaignDto;
import com.gr.dm.core.dto.StackAdaptDataDto;
import com.gr.dm.core.util.DateUtil;
import com.gr.dm.core.util.Util;

@Service
public class StackAdaptIntegrationService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private StackAdaptAdapterSettings stackAdaptAdapterSettings;
	
	public StackAdaptCampaignDto getAllStackAdaptCampaigns(HttpEntity<String> requestEntity, Integer page) {
		UriComponents builder = UriComponentsBuilder.fromHttpUrl(stackAdaptAdapterSettings.getAllStackAdaptCampaignsUrl()+page.toString()).build();
		ResponseEntity response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, StackAdaptCampaignDto.class);
		StackAdaptCampaignDto stackAdaptAllCampaigns = (StackAdaptCampaignDto) response.getBody();
		return stackAdaptAllCampaigns;
	}

	public List<StackAdaptCampaginStats> getAllCampaignsStats(StackAdaptDataDto[] stackAdaptCampagins, List<StackAdaptCampaginStats> stackAdaptCampaginStatsList,
			HttpEntity<String> requestEntity, String startDateString, String endDateString) {
		
		Date startDate = DateUtil.getDate(endDateString);
		UriComponents builder;
		
		if (stackAdaptCampagins.length > 0) {
			for (int i=0; i<stackAdaptCampagins.length; i++) {
				if (Util.isNullOrEmpty(stackAdaptCampagins[i].getEnd_date()) || startDate.before(DateUtil.getDate(stackAdaptCampagins[i].getEnd_date())) || startDate.equals(DateUtil.getDate(stackAdaptCampagins[i].getEnd_date()))) {
					builder = UriComponentsBuilder.fromHttpUrl(stackAdaptAdapterSettings.getstackAdaptCampaignStatsUrl()+stackAdaptCampagins[i].getId()+"&start_date="+startDateString+"&end_date="+endDateString).build();
					restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, StackAdaptCampaignDto.class);
					ResponseEntity statsResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, StackAdaptCampaginStatsDto.class);
					StackAdaptCampaginStatsDto stackAdaptCampaginStats = (StackAdaptCampaginStatsDto) statsResponse.getBody();
					if (Util.isNotNull(stackAdaptCampaginStats.getTotal_stats())) {
						stackAdaptCampaginStatsList.add(stackAdaptCampaginStats.getTotal_stats());
					} else {
						stackAdaptCampaginStatsList.add(null);
					}
				} else {
					stackAdaptCampaginStatsList.add(null);
				}
			}
		}
			
		return stackAdaptCampaginStatsList;
	}
	
	public Long getTransactionCount(StackAdaptDataDto[] stackAdaptCampagins, HttpEntity<String> requestEntity, String startDateString, String endDateString) {
		Long totalConversions = 0L;
		Date startDate = DateUtil.getDate(startDateString);
		UriComponents builder;
		
		if (stackAdaptCampagins.length > 0) {
			for (int i=0; i<stackAdaptCampagins.length; i++) {
				if (Util.isNullOrEmpty(stackAdaptCampagins[i].getEnd_date()) || startDate.before(DateUtil.getDate(stackAdaptCampagins[i].getEnd_date())) || startDate.equals(DateUtil.getDate(stackAdaptCampagins[i].getEnd_date()))) {
					builder = UriComponentsBuilder.fromHttpUrl(stackAdaptAdapterSettings.getstackAdaptCampaignStatsUrl()+stackAdaptCampagins[i].getId()+"&start_date="+startDateString+"&end_date="+endDateString).build();
					ResponseEntity statsResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, StackAdaptCampaginStatsDto.class);
					StackAdaptCampaginStatsDto stackAdaptCampaginStats = (StackAdaptCampaginStatsDto) statsResponse.getBody();
					if (Util.isNotNull(stackAdaptCampaginStats.getTotal_stats())) {
					totalConversions = totalConversions + stackAdaptCampaginStats.getTotal_stats().getConv();
					}
				}
			}
		}
		return totalConversions;
	}
	
	private HttpHeaders buildHeader() {
	       HttpHeaders headers = new HttpHeaders();
	        headers.set("X-Authorization", stackAdaptAdapterSettings.getApiKey());
	        return headers;
	    }
}
