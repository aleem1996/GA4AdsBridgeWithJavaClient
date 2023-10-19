package com.gr.dm.core.adapter.adwords;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;

/**
 * @author Aleem Malik
 */
@Component
public class AdWordsAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.adwords.report.configfilename}")
	private String configFileName;

	public String getConfigFileName() {
		return this.configFileName;
	}
}
