package com.gr.dm.core.dto.crm;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String requestCode;
	private String statusCode;
	private String statusShortMessage;
	private String statusMessage;
	private JsonNode detail;

}
