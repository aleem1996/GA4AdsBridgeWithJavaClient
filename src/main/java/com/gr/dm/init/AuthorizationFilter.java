
package com.gr.dm.init;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.JwtUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class AuthorizationFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
	
	private JwtUtil jwtUtil;
	
	private String[] allowedPaths = {"/api/v2/login", "/api/v2/clearcache", "/api/v2/landingPageStats", "api/v2/campaignLandingPageStats", "api/v2/campaigncost", "api/v2/useractivity"};

	public AuthorizationFilter() {
		logger.info("AuthorizationFilter init");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;

		Predicate<String> pathsPredicate = e -> request.getServletPath().indexOf(e) != -1;
		if (HttpMethod.OPTIONS.name().equals(request.getMethod()) || request.getServletPath().indexOf("/api/v2/") == -1
				|| Arrays.stream(allowedPaths).anyMatch(pathsPredicate)) {
			chain.doFilter(req, res);
			return;
		}
		
		String header = request.getHeader(Constants.HEADER_X_AUTH_TOKEN);

		if (header == null || !header.startsWith(Constants.HEADER_X_AUTH_TOKEN_PREFIX)) { 
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Authentication token is missing in request.");
			return;
		}
		
		try {
			String token = header.substring(7).trim();
			SignedJWT signedJWT = getJwtUtil(request).validateAndParseToken(token);
			JWTClaimsSet jwtClaimsSet = getJwtUtil(request).getClaimSetFromToken(signedJWT);
			String jwt = getJwtUtil(request).generateTokenFromClaimSet(jwtClaimsSet);
			jwt = Constants.HEADER_X_AUTH_TOKEN_PREFIX + jwt;
			response.addHeader(Constants.HEADER_X_AUTH_TOKEN, jwt);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token invalid or expired.");
			return;
		}

		chain.doFilter(req, res);
	}
	
	private JwtUtil getJwtUtil(HttpServletRequest req) {

		if (this.jwtUtil == null) {

			ServletContext servletContext = req.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getWebApplicationContext(servletContext);
			jwtUtil = webApplicationContext.getBean(JwtUtil.class);
		}
		return jwtUtil;
	}
	
	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}

}
