package com.gr.dm.core.dto.report;

import java.io.Serializable;

import com.gr.dm.core.entity.CampaignDetailSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipStatsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private CampaignDetailSource source;

	private Long newMemberships;

	private Long renewedMemberships;

	private Double newMembershipsRevenue;

	private Double renewedMembershipsRevenue;

}
