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
public class WeeklyCostDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String interval;

	private Double facebookCost;

	private Double adwordsCost;

	private Double bingCost;

	private Double totalCost;
}
