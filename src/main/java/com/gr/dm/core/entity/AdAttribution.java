package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.gr.dm.core.util.Util;

/**
 * @author Aleem Malik
 */
@Entity
@Table(name = "AdAttribution")
@DynamicInsert
@DynamicUpdate
public class AdAttribution implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String adId;

	private Integer purchases1DayView = 0;
	
	private Integer purchases7DayView = 0;

	private Integer purchases28DayView = 0;

	private Integer purchases1DayClick = 0;

	private Integer purchases7DayClick = 0;

	private Integer purchases28DayClick = 0;

	private Double revenue1DayView = 0.0;

	private Double revenue7DayView = 0.0;

	private Double revenue28DayView = 0.0;

	private Double revenue1DayClick = 0.0;

	private Double revenue7DayClick = 0.0;

	private Double revenue28DayClick = 0.0;

	private Date startDate;

	private Date endDate;

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

	public Integer getPurchases1DayView() {
		return purchases1DayView;
	}

	public void setPurchases1DayView(Integer purchases1DayView) {
		if (Util.isNotNull(purchases1DayView)) {
			this.purchases1DayView = purchases1DayView;
		}
	}

	public Integer getPurchases7DayView() {
		return purchases7DayView;
	}

	public void setPurchases7DayView(Integer purchases7DayView) {
		if (Util.isNotNull(purchases7DayView)) {
			this.purchases7DayView = purchases7DayView;
		}
	}

	public Integer getPurchases28DayView() {
		return purchases28DayView;
	}

	public void setPurchases28DayView(Integer purchases28DayView) {
		if (Util.isNotNull(purchases28DayView)) {
			this.purchases28DayView = purchases28DayView;
		}
	}

	public Integer getPurchases1DayClick() {
		return purchases1DayClick;
	}

	public void setPurchases1DayClick(Integer purchases1DayClick) {
		if (Util.isNotNull(purchases1DayClick)) {
			this.purchases1DayClick = purchases1DayClick;
		}
	}

	public Integer getPurchases7DayClick() {
		return purchases7DayClick;
	}

	public void setPurchases7DayClick(Integer purchases7DayClick) {
		if (Util.isNotNull(purchases7DayClick)) {
			this.purchases7DayClick = purchases7DayClick;
		}
	}

	public Integer getPurchases28DayClick() {
		return purchases28DayClick;
	}

	public void setPurchases28DayClick(Integer purchases28DayClick) {
		if (Util.isNotNull(purchases28DayClick)) {
			this.purchases28DayClick = purchases28DayClick;
		}
	}

	public Double getRevenue1DayView() {
		return revenue1DayView;
	}

	public void setRevenue1DayView(Double revenue1DayView) {
		if (Util.isNotNull(revenue1DayView)) {
			this.revenue1DayView = revenue1DayView;
		}
	}

	public Double getRevenue7DayView() {
		return revenue7DayView;
	}

	public void setRevenue7DayView(Double revenue7DayView) {
		if (Util.isNotNull(revenue7DayView)) {
			this.revenue7DayView = revenue7DayView;
		}
	}

	public Double getRevenue28DayView() {
		return revenue28DayView;
	}

	public void setRevenue28DayView(Double revenue28DayView) {
		if (Util.isNotNull(revenue28DayView)) {
			this.revenue28DayView = revenue28DayView;
		}
	}

	public Double getRevenue1DayClick() {
		return revenue1DayClick;
	}

	public void setRevenue1DayClick(Double revenue1DayClick) {
		if (Util.isNotNull(revenue1DayClick)) {
			this.revenue1DayClick = revenue1DayClick;
		}
	}

	public Double getRevenue7DayClick() {
		return revenue7DayClick;
	}

	public void setRevenue7DayClick(Double revenue7DayClick) {
		if (Util.isNotNull(revenue7DayClick)) {
			this.revenue7DayClick = revenue7DayClick;
		}
	}

	public Double getRevenue28DayClick() {
		return revenue28DayClick;
	}

	public void setRevenue28DayClick(Double revenue28DayClick) {
		if (Util.isNotNull(revenue28DayClick)) {
			this.revenue28DayClick = revenue28DayClick;
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}