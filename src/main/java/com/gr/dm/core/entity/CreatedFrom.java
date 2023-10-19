package com.gr.dm.core.entity;

/**
 * @author Aleem Malik
 */
public enum CreatedFrom {

	Facebook("Facebook"), Analytics("Analytics"), Adwords("AdWords"), Bing("Bing"), SS("SS"), Email("Email"), GA4("GA4"), UA("UA");

	private String value;

	private CreatedFrom(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CreatedFrom fromValue(String value) {

		if (value != null) {
			for (CreatedFrom type : values()) {

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
