
package com.gr.dm.init;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.gr.dm.core.util.Constants;

@Component
public class DmCorsFilter implements Filter {

	private final Logger log = LoggerFactory.getLogger(DmCorsFilter.class);

	public DmCorsFilter() {
		log.info("SimpleCORSFilter init");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) res;
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", getAllowedHeaders());
		response.setHeader("Access-Control-Expose-Headers", getAllowedHeaders());

		chain.doFilter(req, res);
	}

	private String getAllowedHeaders() {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpHeaders.ACCEPT).append(", ")
		.append(HttpHeaders.CONTENT_TYPE).append(", ")
		.append(HttpHeaders.AUTHORIZATION).append(", ")
		.append(Constants.HEADER_IF_MODIFIED_SINCE).append(", ")
		.append(Constants.HEADER_CACHE_CONTROL).append(", ")
		.append(Constants.HEADER_PRAGMA).append(", ")
		.append(Constants.HEADER_CONTENT_DISPOSITION).append(", ")
		.append(Constants.HEADER_X_AUTH_TOKEN).append(", ")
		.append(Constants.HEADER_X_SOURCE).append(", ")
		.append(Constants.HEADER_X_REQUEST_CODE).append(", ")
		.append(Constants.HEADER_X_AUTHORIZATION).append(", ")
		.append(Constants.HEADER_X_DEVICE_APP_NAME).append(", ")
		.append(Constants.HEADER_X_DEVICE_APP_VERSION).append(", ")
		.append(Constants.HEADER_X_DEVICE_IMEI).append(", ")
		.append(Constants.HEADER_X_DEVICE_MODEL).append(", ")
		.append(Constants.HEADER_X_LOGIN_CONTACT_GUID).append(", ")
		.append(Constants.HEADER_X_SILENT_LOGIN).append(", ")
		.append(Constants.HEADER_X_ACCOUNT).append(", ")
		.append(Constants.HEADER_X_DEVICE_TYPE);
		
		return sb.toString();
	}
	
	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}

}
