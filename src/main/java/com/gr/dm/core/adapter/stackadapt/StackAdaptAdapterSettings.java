package com.gr.dm.core.adapter.stackadapt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.gr.dm.core.adapter.AdapterSettingsBase;

@Component
public class StackAdaptAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.stackadapt.apikey}")
	private String apiKey;
	
	@Value("${dm.stackadapt.getallcampaginsurl}")
	private String allStackAdaptCampaignsUrl;
	
	@Value("${dm.stackadapt.getcampaginstatsurl}")
	private String stackAdaptCampaignStatsUrl;
	
	public String getApiKey(){
		return this.apiKey;
	}
	
	public String getAllStackAdaptCampaignsUrl(){
		return this.allStackAdaptCampaignsUrl;
	}
	
	public String getstackAdaptCampaignStatsUrl(){
		return this.stackAdaptCampaignStatsUrl;
	}
}
