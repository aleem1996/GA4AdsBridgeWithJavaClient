package com.gr.dm.core.dto;

import java.util.Date;

import com.gr.dm.core.entity.CampaignSource;

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
public class AdGroupDto {

	private Integer id;

	private String name;

	private String campaignId;

	private String adGroupId;

	private CampaignSource adGroupSource;

	private Long transactionCount;

	private Double revenue;

	private Double cost;

	private Date startDate;

	private Date endDate;

	private Long clicks;

	private Long impressions;

	private Long purchases7Day;

	private Long purchases28Day;

	private Double revenue7Day;

	private Double revenue28Day;

	private Long purchases1DayView;
	
	private Long purchases7DayClick;
	
	private Double revenue1DayView;
	
	private Double revenue7DayClick;
}
