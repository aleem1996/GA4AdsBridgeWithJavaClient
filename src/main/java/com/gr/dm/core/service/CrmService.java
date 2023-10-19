package com.gr.dm.core.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gr.dm.core.adapter.gws.CrmAdapter;
import com.gr.dm.core.dto.crm.CrmResponse;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignSource;

@Service
public class CrmService {
	
	public static final Logger logger = Logger.getLogger(CrmService.class.getName());
	private static final String FAILURE_STATUS_CODE = "201";
	private static final int maxRetryAttempts = 3;
	private int attemptNumber = 1;
	
	@Autowired
	CrmAdapter crmAdapter;
	
	@Autowired
	NotificationService notificationService;

	@Async
	@Retryable(value = { Exception.class }, maxAttempts = maxRetryAttempts, backoff = @Backoff(delay = 30000))
	public void createCampaignInCrm(Campaign campaign) throws Exception {
		if (!CampaignSource.Email.equals(campaign.getCampaignSource())) {
			try {
				CrmResponse crmResponse = crmAdapter.createCampaign(campaign);
				if (FAILURE_STATUS_CODE.equals(crmResponse.getStatusCode())) {
					throw new Exception("Unable to sync campaign in Dynamics.");
				}
				attemptNumber = 1;
			} catch (Exception ex) {
				logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
				sendCreateCampaignFailureEmail(campaign, ex);
				throw ex;
			}
		}
	}
	
	private void sendCreateCampaignFailureEmail(Campaign campaign, Exception ex) {
		notificationService.sendCampaignSyncFailureEmailNotification(campaign, attemptNumber, ex);
		attemptNumber = attemptNumber < maxRetryAttempts ? attemptNumber + 1 : 1;
	}
}
