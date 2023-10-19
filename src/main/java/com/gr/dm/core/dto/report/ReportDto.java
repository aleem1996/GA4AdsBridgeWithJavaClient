package com.gr.dm.core.dto.report;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private String label;
	private String stack;
	private List<String> data;

}
