package com.gr.dm.core.adapter.googleanalytics4;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;

/**
 * 
 * @author Aleem Malik
 *
 */
@Component
public class GoogleAnalytics4AdapterSettings extends AdapterSettingsBase {
	
	@Value("${dm.ga.report.configfilename}")
	private String configFileName;

	public String getConfigFileName() {
		return this.configFileName;
	}
}
