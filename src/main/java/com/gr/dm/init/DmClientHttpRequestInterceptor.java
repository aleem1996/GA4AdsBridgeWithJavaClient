package com.gr.dm.init;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.gr.dm.core.util.Constants;

class DmClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
	private final Logger logger = LoggerFactory.getLogger(DmClientHttpRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		HttpHeaders headers = request.getHeaders();
		headers.add(Constants.HEADER_X_SOURCE, "grcom");

		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		response = logResponse(response);
		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) throws IOException {
		logger.info("===========================request begin================================================");
		logger.info("URI         : {}", request.getURI());
		logger.info("Method      : {}", request.getMethod());
		logger.info("Headers     : {}", request.getHeaders());
		logger.info("Request body: {}", new String(body, "UTF-8"));
		logger.info("=============================request end================================================");
	}

	private ClientHttpResponse  logResponse(ClientHttpResponse response) throws IOException {
		final ClientHttpResponse responseCopy = new BufferingClientHttpResponseWrapper(response);
		logger.info("============================response begin==========================================");
		logger.info("Status code  : {}", responseCopy.getStatusCode());
		logger.info("Status text  : {}", responseCopy.getStatusText());
		logger.info("Headers      : {}", responseCopy.getHeaders());
		logger.info("Body      : {}", IOUtils.toString(responseCopy.getBody(), StandardCharsets.UTF_8));
		logger.info("==============================response end==========================================");
		return responseCopy;
	}
}