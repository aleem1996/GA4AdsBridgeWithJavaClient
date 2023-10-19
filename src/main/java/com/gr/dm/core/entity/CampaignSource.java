package com.gr.dm.core.entity;

/**
 * @author Aleem Malik
 */
public enum CampaignSource {

	Facebook("Facebook"), Google("Google"), Bing("Bing"), Email("Email"), StackAdapt("StackAdapt"), GoogleGA4("GoogleGA4"), GoogleUA("GoogleUA");

	private String value;

	private CampaignSource(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CampaignSource fromValue(String value) {

		if (value != null) {
			for (CampaignSource type : values()) {

				if (value.equalsIgnoreCase(type.value)) {
					return type;
				}
			}
		}
		return null;
	}

	public static class Fields {
		public static final String VALUE = "value";
	}
}
