package com.gr.dm.core.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.gr.dm.core.entity.Campaign;
import com.gr.integration.notification.dto.request.EmailSendRequestDto;
import com.gr.integration.notification.dto.request.EmailSendRequestDto.ProfileRecipientType;
import com.gr.integration.notification.service.GrNotificationIntegrationService;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Service
public class NotificationService {

	public static final Logger logger = Logger.getLogger(NotificationService.class.getName());

	private final Configuration templateConfiguration;

	@Autowired
	private GrNotificationIntegrationService grNotificationIntegrationService;

	@Value("${dm.email.supportEmailAddress}")
	private String supportDistro;

	@Value("${spring.application.name}")
	private String applicationName;

	@Autowired
	public NotificationService(Configuration templateConfiguration) {
		super();
		this.templateConfiguration = templateConfiguration;
	}

	private static String DATE_FORMAT = "EEEE, MMMM dd, yyyy hh:mm:ss a";

	@Async
	public void sendExceptionEmailNotification(Exception ex) {
		try {
			String emailBody = null;
			Template template = templateConfiguration.getTemplate("exception-email.html");
			Map<String, String> model = getMailMap(ex);
			emailBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
			String subject = applicationName + " - Exception Occurred";

			EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.ERROR);
			emailSendRequestDto.setEmailBody(emailBody);
			emailSendRequestDto.setEmailTo(supportDistro);
			emailSendRequestDto.setSubject(subject);
			grNotificationIntegrationService.sendEmailByProfileRecipientType(ProfileRecipientType.ERROR, emailSendRequestDto);
		} catch (Exception e) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
		}

	}

	@Async
	public void sendCampaignSyncFailureEmailNotification(Campaign campaign, int attemptNumber, Exception ex) {
		try {
			String emailBody = null;
			Template template = templateConfiguration.getTemplate("exception-email.html");
			Map<String, String> model = getMailMap(ex);
			model.put("reason", "Failed to sync campaign data in Dynamics.");
			model.put("attemptNumber", String.valueOf(attemptNumber));
			model.put("campaignName", campaign.getName());
			model.put("campaignId", campaign.getCampaignId());
			model.put("campaignSource", campaign.getCampaignSource().toString());
			String subject = applicationName + " - Exception Occurred" + " - Attempt Number: " + attemptNumber;
			emailBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

			EmailSendRequestDto emailSendRequestDto = new EmailSendRequestDto(ProfileRecipientType.ERROR);
			emailSendRequestDto.setEmailBody(emailBody);
			emailSendRequestDto.setSubject(subject);
			emailSendRequestDto.setEmailTo(supportDistro);
			grNotificationIntegrationService.sendEmailByProfileRecipientType(ProfileRecipientType.ERROR, emailSendRequestDto);
		} catch (Exception e) {
			logger.log(Level.SEVERE, ex.getMessage(), ex.getCause());
		}
	}

	private Map<String, String> getMailMap(Exception ex) {
		Map<String, String> model = new HashMap<String, String>();
		model.put("time", new SimpleDateFormat(DATE_FORMAT).format(new Date()));
		model.put("moduleName", "Digital Media");
		model.put("methodName", ex.getStackTrace()[0].getMethodName());
		model.put("className", ex.getStackTrace()[0].getClassName());
		model.put("stackTrace", ExceptionUtils.getStackTrace(ex));
		return model;
	}

}
