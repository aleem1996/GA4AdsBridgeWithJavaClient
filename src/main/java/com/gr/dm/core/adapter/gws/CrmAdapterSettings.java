package com.gr.dm.core.adapter.gws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gr.dm.core.adapter.AdapterSettingsBase;

/**
 * 
 * @author Aleem Malik
 *
 */
@Component
public class CrmAdapterSettings extends AdapterSettingsBase {

	@Value("${dm.crm.url}")
	private String uri;

	public String getUri() {
		return this.uri;
	}
}
