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
public class KeywordDto {

	private Integer id;

	private String adGroupId;

	private String keywordId;

	private String name;

	private Long transactionCount;

	private Double revenue;

	private Double cost;

	private Date startDate;

	private Date endDate;

	private Long clicks;

	private Long impressions;

}
