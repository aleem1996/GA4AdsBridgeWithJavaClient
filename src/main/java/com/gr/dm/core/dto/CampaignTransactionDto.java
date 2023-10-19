package com.gr.dm.core.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Aleem Malik
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignTransactionDto {
	
	public CampaignTransactionDto(String transactionId, String memberId, Double transactionRevenue, String packageCode,
			Boolean isNewMembership, Boolean isRenewedMembership, Boolean hasTI, Boolean hasMedicalDevice,
			Date serverDate, String directSource, String assistSource) {
		super();
		this.transactionId = transactionId;
		this.memberId = memberId;
		this.transactionRevenue = transactionRevenue;
		this.packageCode = packageCode;
		this.isNewMembership = isNewMembership;
		this.isRenewedMembership = isRenewedMembership;
		this.hasTI = hasTI;
		this.hasMedicalDevice = hasMedicalDevice;
		this.serverDate = serverDate;
		this.directSource = directSource;
		this.assistSource = assistSource;
	}

	private Integer id;

	private String campaignId;

	private String transactionId;

	private String country;

	private String source;

	private String browser;

	private String device;

	private Boolean isNewMembership;

	private Boolean isRenewedMembership;

	private Boolean hasTI;

	private Boolean hasMedicalDevice;

	private Date startDate;

	private Date endDate;

	private Double transactionRevenue;

	private String memberId;

	private String packageCode;

	private String contractGuid;

	private Date clientDate;

	private Date serverDate;

	private String keywords;

	private Integer deviceCount;

	private String rpCode;

	private String directSource;

	private String assistSource;

}
