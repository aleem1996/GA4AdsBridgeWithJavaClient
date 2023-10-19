package com.gr.dm.core.dto.report;

import org.apache.poi.ss.usermodel.CellStyle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExcelDataDto {

	public ExcelDataDto(Object value, Integer type) {
		super();
		this.value = value;
		this.type = type;
	}

	private Object value;
	private Integer type;
	private Short alignment = CellStyle.ALIGN_CENTER;

}
