package com.gr.dm.core.entity;

/**
 * @author Aleem Malik
 */
public enum CampaignDetailSource {

	Facebook("Facebook"), Analytics("Analytics"), Adwords("AdWords"), GoogleAds("GoogleAds"), Bing("Bing"), Email("Email"), StackAdapt("StackAdapt"), GA4("GA4"), UA("UA");

	private String value;

	private CampaignDetailSource(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CampaignDetailSource fromValue(String value) {

		if (value != null) {
			for (CampaignDetailSource type : values()) {

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
