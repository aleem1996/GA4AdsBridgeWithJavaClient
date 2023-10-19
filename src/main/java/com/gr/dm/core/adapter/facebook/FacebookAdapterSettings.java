package com.gr.dm.core.adapter.facebook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;

/**
 * @author Aleem Malik
 */
@Component
public class FacebookAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.fb.api.access_token}")
	private String accessToken;
	
	@Value("${dm.fb.api.ad_account_id}")
	private String adAccountId;
	
	public String getAccessToken(){
		return this.accessToken;
	}
	
	public String getAdAccountId(){
		return this.adAccountId;
	}
	
}
