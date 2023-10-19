package com.gr.dm.init;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

class DmClientHttpResponseErrorHandler implements ResponseErrorHandler {
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		// handle error
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		// handle error
		return false;
	}
}