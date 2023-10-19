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
public class AdDto {

	private Integer id;

	private String adGroupId;

	private String adId;

	private String headLine1;

	private String headLine2;

	private Long transactionCount;

	private Double revenue;

	private Double cost;

	private Date startDate;

	private Date endDate;

	private Long clicks;

	private Long impressions;

	private String imageUrl;

	private Long purchases7Day;

	private Long purchases28Day;

	private Double revenue7Day;

	private Double revenue28Day;
	
	private Long purchases1DayView;
	
	private Long purchases7DayClick;
	
	private Double revenue1DayView;
	
	private Double revenue7DayClick;

}
