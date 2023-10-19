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
public class WeeklyRevenueDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String interval;

	private Double totalRevenue;

	private Double weeklyNewRevenue;

	private Double totalAttributionRevenue;

	private Double gaBingTotalRevenue;

	private Double fb28DayTotalRevenue;

	private Double gaBingAssistedRevenue;

	private Double gaBingDirectRevenue;

	private Double fb28DayViewRevenue;

	private Double fb28DayClickRevenue;

	private Double gaAssistRevenue;

	private Double gaDirectRevenue;

	private Double bingAssistRevenue;

	private Double bingDirectRevenue;

}
