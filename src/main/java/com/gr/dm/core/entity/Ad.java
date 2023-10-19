package com.gr.dm.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "Ad")
@DynamicInsert
@DynamicUpdate
public class Ad implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String adId;

	private String adGroupId;
	
	private String headLine1;
	
	private String headLine2;
	
	@Column(length = 1000)
	private String imageUrl;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAdId() {
		return adId;
	}

	public void setAdId(String adId) {
		this.adId = adId;
	}

	public String getAdGroupId() {
		return adGroupId;
	}

	public void setAdGroupId(String adGroupId) {
		this.adGroupId = adGroupId;
	}

	public String getHeadLine1() {
		return headLine1;
	}

	public void setHeadLine1(String headLine1) {
		this.headLine1 = headLine1;
	}

	public String getHeadLine2() {
		return headLine2;
	}

	public void setHeadLine2(String headLine2) {
		this.headLine2 = headLine2;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
