package com.gr.dm.core.dto.crm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gr.dm.core.util.Constants;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CrmRequestPayload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String requestingAppKey;
	private String requestCode;
	private Map<String, Object> detail = new HashMap<String, Object>();

	private CrmRequestPayload() {
	}

	public String getRequestingAppKey() {
		return requestingAppKey;
	}

	public String getRequestCode() {
		return requestCode;
	}

	public Map<String, Object> getDetail() {
		return detail;
	}
	
	public Object getDetail(String key) {
		return this.detail.get(key);
	}

	public static class Builder {

		private String requestingAppKey;
		private String requestCode;
		private Map<String, Object> detail = new HashMap<String, Object>();

		private static final ObjectMapper mapper = new ObjectMapper();

		public Builder() {
			this.withRequestCode("123");
			this.withRequestingAppKey(Constants.REQUESTING_APP_KEY);
		}

		public static CrmRequestPayload.Builder newInstance() {
			return new CrmRequestPayload.Builder();
		}

		public Builder withRequestingAppKey(String requestingAppKey) {
			this.requestingAppKey = requestingAppKey;
			return this;
		}

		public Builder withRequestCode(String requestCode) {
			this.requestCode = requestCode;
			return this;
		}

		public Builder with(String key, Object value) {
			this.detail.put(key, value);
			return this;
		}

		// build object using this function if you want to move objects in detail to root level
		public JsonNode buildJsonNode() {

			JsonNode jsonNode = mapper.valueToTree(this.build());
			ObjectNode objectNode = ((ObjectNode) jsonNode);
			
			this.detail.forEach((key, value) -> {
				JsonNode node = mapper.valueToTree(value);
				objectNode.set(key, node);
			});
			objectNode.remove("detail");
			return jsonNode;
		}

		public CrmRequestPayload build() {

			CrmRequestPayload crmRequestPayload = new CrmRequestPayload();
			crmRequestPayload.requestCode = this.requestCode;
			crmRequestPayload.requestingAppKey = this.requestingAppKey;

			crmRequestPayload.detail.putAll(this.detail);

			return crmRequestPayload;
		}
	}
}
