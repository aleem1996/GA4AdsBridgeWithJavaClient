package com.gr.dm.core.adapter.googleads;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;
/**
 * 
 * @author Aleem Malik
 *
 */
@Component
public class GoogleAdsAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.googleads.configfilename}")
	private String configFileName;

	public String getConfigFileName() {
		return this.configFileName;
	}	
}
