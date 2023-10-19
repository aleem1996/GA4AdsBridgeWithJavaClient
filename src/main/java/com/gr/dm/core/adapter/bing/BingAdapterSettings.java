package com.gr.dm.core.adapter.bing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;

/**
 * @author Aleem Malik
 */
@Component
public class BingAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.bing.api.developer_token}")
	private String developerToken;
	
	@Value("${dm.bing.api.client_id}")
	private String clientId;
	
	@Value("${dm.bing.api.client_secret}")
	private String clientSecret;
	
	@Value("${dm.bing.api.customer_id}")
	private Long customerId;
	
	@Value("${dm.bing.api.account_id}")
	private Long accountId;
	
	@Value("${dm.bing.api.token_expires_in}")
	private Long tokenExpiresIn;
	
	@Value("${dm.bing.api.access_token}")
	private String accessToken;
	
	@Value("${dm.bing.api.refresh_token}")
	private String refreshToken;
	
	@Value("${dm.bing.api.redirection_url}")
	private String redirectionUrl;

	public String getDeveloperToken() {
		return developerToken;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public Long getTokenExpiresIn() {
		return tokenExpiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getRedirectionUrl() {
		return redirectionUrl;
	}
}
