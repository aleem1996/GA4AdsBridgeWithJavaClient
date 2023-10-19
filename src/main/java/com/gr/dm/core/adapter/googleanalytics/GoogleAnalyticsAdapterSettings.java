package com.gr.dm.core.adapter.googleanalytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;

/**
 * 
 * @author Aleem Malik
 *
 */
@Component
public class GoogleAnalyticsAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.ga.report.viewid}")
	private String viewId;

	@Value("${dm.ga.report.configfilename}")
	private String configFileName;

	public String getConfigFileName() {
		return this.configFileName;
	}

	public String getViewId() {
		return this.viewId;
	}
}