package com.gr.dm.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.dm.core.adapter.gws.CrmAdapter;
import com.gr.dm.core.dto.LoginUser;
import com.gr.dm.core.dto.UserDto;
import com.gr.dm.core.dto.crm.CrmResponse;
import com.gr.dm.core.service.AnalyticsSyncService;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.JwtUtil;
import com.gr.dm.core.util.Util;

/**
 * 
 * @author Aleem Malik
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v2")
public class AuthResource {

	@Autowired
	CrmAdapter adapter;

	@Autowired
	AnalyticsSyncService syncService;
	
	@Autowired
	private JwtUtil jwtUtil;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<CrmResponse> login(@RequestBody LoginUser user) throws Exception {

		CrmResponse crmResponse = new CrmResponse();
		if (Util.isNullOrEmpty(user.getToken())) {
			crmResponse.setStatusMessage("Token/contactGuid is mandatory");
			return new ResponseEntity<>(crmResponse, HttpStatus.BAD_REQUEST);
		}

		crmResponse = this.adapter.silentLogin(user);
		JsonNode responseUser = crmResponse.getDetail().get("user");
		
		if(Util.isNull(responseUser)) {
			crmResponse.setStatusMessage("Invalid token/credentials received for login request.");
			return new ResponseEntity<>(crmResponse, HttpStatus.BAD_REQUEST);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		UserDto userDto = mapper.treeToValue(responseUser, UserDto.class);

		String jwt = jwtUtil.generateToken(userDto);

		// TODO: build jwt token for login user
		jwt = Constants.HEADER_X_AUTH_TOKEN_PREFIX + jwt;

		HttpHeaders headers = new HttpHeaders();
		headers.add(Constants.HEADER_X_AUTH_TOKEN, jwt);

		return new ResponseEntity<CrmResponse>(crmResponse, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/refreshtoken", method = RequestMethod.GET)
	public ResponseEntity<Object> refreshToken() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

}