package com.gr.dm.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class UserDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private String guid;
	private String firstName;
	private String lastName;
	private String email;
	private String name;
	private String primaryPhone;
	private String accountGuid;
	private String parentAccountName;
	private List<String> roles = new ArrayList<String>();
	private HashMap<String, List<Object>> permissions = new HashMap<String, List<Object>>();

}
