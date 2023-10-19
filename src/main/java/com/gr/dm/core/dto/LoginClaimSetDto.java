package com.gr.dm.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginClaimSetDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Boolean silentLogin = Boolean.TRUE;
	private String source = StringUtils.EMPTY;
	private UserDto loginUser;

	public static class Fields {
		public static final String SILENT_LOGIN = "silentLogin";
		public static final String LOGIN_USER = "loginUser";
		public static final String SOURCE = "source";
	}
}
