package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.Date;

public class McCode implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String code;
	private Date lastUpdated;
	private Integer expiryDays;

	public McCode() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Integer getExpiryDays() {
		return expiryDays;
	}

	public void setExpiryDays(Integer expiryDays) {
		this.expiryDays = expiryDays;
	}
}