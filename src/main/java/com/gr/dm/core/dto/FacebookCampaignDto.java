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
public class FacebookCampaignDto {

	private Integer id;

	private String name;

	private String campaignId;

	private Long transactionCount;

	private Double revenue;

	private Double cost;

	private Date startDate;

	private Date endDate;

	private Long newMembershipCount;

	private Long renewedMembershipCount;

	private Long tiCount;

	private Long totalDeviceCount;

	private Long clicks;

	private Long impressions;

	private Long purchases1Day;

	private Long purchases7Day;

	private Long purchases28Day;

	private Double revenue1Day;

	private Double revenue7Day;

	private Double revenue28Day;
	
	private Long purchases1DayView;
	
	private Long purchases7DayClick;
	
	private Double revenue1DayView;
	
	private Double revenue7DayClick;
}
