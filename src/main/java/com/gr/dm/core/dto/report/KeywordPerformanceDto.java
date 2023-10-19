package com.gr.dm.core.dto.report;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordPerformanceDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String source;

	private String bestKeyword;

	private String worstKeyword;

	private Long bestKeywordConversions;

	private Long worstKeywordConversions;

	private Double bestKeywordRevenue;

	private Double worstKeywordRevenue;

	private Double bestKeywordCost;

	private Double worstKeywordCost;

}
