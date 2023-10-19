package com.gr.dm.core.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LatestDateDto {

	private String dataFetchDate;
	private String dataSyncDate;
}
