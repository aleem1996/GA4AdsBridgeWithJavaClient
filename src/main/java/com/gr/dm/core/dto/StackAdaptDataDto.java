package com.gr.dm.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StackAdaptDataDto {
	private Integer id;

	private String name;
	
	private String start_date;
	
	private String end_date;
	
	private String updated_at;
}
