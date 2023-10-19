package com.gr.dm.core.dto.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public interface GenericReportDto {

	String getSource();
	Double getVal();
	String getLabel();
}
