package com.gr.dm.core.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandingPageStatsDto {
	private String landingPage;
	private String campaign;
	private Map<String, Double> metrics;
	private List<Map<String, Object>> events;
}
