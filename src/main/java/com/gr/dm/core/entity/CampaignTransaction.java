package com.gr.dm.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

import com.gr.dm.core.dto.CampaignTransactionDto;
import com.gr.dm.core.util.NativeQueries;
import com.gr.dm.core.util.Util;

@Entity
@Table(name = "CampaignTransaction")
@DynamicInsert
@DynamicUpdate

@SqlResultSetMappings({ @SqlResultSetMapping(name = "CampaignTransaction.duplicateTransactionsMapping", classes = {
		@ConstructorResult(targetClass = CampaignTransactionDto.class, columns = {
				@ColumnResult(name = "transactionId", type = String.class),
				@ColumnResult(name = "memberId", type = String.class),
				@ColumnResult(name = "transactionRevenue", type = Double.class),
				@ColumnResult(name = "packageCode", type = String.class),
				@ColumnResult(name = "isNewMembership", type = Boolean.class),
				@ColumnResult(name = "isRenewedMembership", type = Boolean.class),
				@ColumnResult(name = "hasTI", type = Boolean.class),
				@ColumnResult(name = "hasMedicalDevice", type = Boolean.class),
				@ColumnResult(name = "serverDate", type = Date.class),
				@ColumnResult(name = "directSource", type = String.class),
				@ColumnResult(name = "assistSource", type = String.class)}) }) })

@NamedNativeQueries({
		@NamedNativeQuery(name = "CampaignTransaction.getDuplicateTransactions", query = NativeQueries.DUPLICATE_TRANSACTIONS, resultSetMapping = "CampaignTransaction.duplicateTransactionsMapping") })
public class CampaignTransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String campaignId;

	private String transactionId;

	private String country;

	private String source;

	private String browser;

	private String device;

	@Column(columnDefinition = "boolean default false")
	private Boolean isNewMembership = false;

	@Column(columnDefinition = "boolean default false")
	private Boolean isRenewedMembership = false;

	@Column(columnDefinition = "boolean default false")
	private Boolean hasTI = false;

	@Column(columnDefinition = "boolean default false")
	private Boolean hasMedicalDevice = false;

	private Date startDate;

	private Date endDate;

	private Double transactionRevenue;

	private String memberId;

	private String packageCode;

	private String contractGuid;

	private Integer deviceCount = 0;

	private Date clientDate;

	private Date serverDate;

	private String keywords;

	private String rpCode;

	@Transient
	private String campaignName;
	
	private String mcCode;

	@Enumerated(EnumType.STRING)
	private CampaignDetailSource transactionSource;
	
	@Enumerated(EnumType.STRING)
	private CreatedFrom createdFrom;
	
	private Boolean isDirect = false;
	
	private Boolean isAssisted = false;
	
	private Boolean isDefaultTransaction = true;
	
	private Boolean isDefaultAssist = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Boolean getIsNewMembership() {
		return isNewMembership;
	}

	public void setIsNewMembership(Boolean isNewMembership) {
		if(Util.isNotNull(isNewMembership)) {
			this.isNewMembership = isNewMembership;
		}
	}

	public Boolean getIsRenewedMembership() {
		return isRenewedMembership;
	}

	public void setIsRenewedMembership(Boolean isRenewedMembership) {
		if (Util.isNotNull(isRenewedMembership)) {
			this.isRenewedMembership = isRenewedMembership;
		}
	}

	public Boolean getHasTI() {
		return hasTI;
	}

	public void setHasTI(Boolean hasTI) {
		if (Util.isNotNull(hasTI)) {
			this.hasTI = hasTI;
		}
	}

	public Boolean getHasMedicalDevice() {
		return hasMedicalDevice;
	}

	public void setHasMedicalDevice(Boolean hasMedicalDevice) {
		if (Util.isNotNull(hasMedicalDevice)) {
			this.hasMedicalDevice = hasMedicalDevice;
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

	public CampaignDetailSource getTransactionSource() {
		return transactionSource;
	}

	public void setTransactionSource(CampaignDetailSource transactionSource) {
		this.transactionSource = transactionSource;
	}

	public Double getTransactionRevenue() {
		return transactionRevenue;
	}

	public void setTransactionRevenue(Double transactionRevenue) {
		this.transactionRevenue = transactionRevenue;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getContractGuid() {
		return contractGuid;
	}

	public void setContractGuid(String contractGuid) {
		this.contractGuid = contractGuid;
	}

	public Integer getDeviceCount() {
		return deviceCount;
	}

	public void setDeviceCount(Integer deviceCount) {
		if (Util.isNotNull(deviceCount)) {
			this.deviceCount = deviceCount;
		}
	}

	public Date getClientDate() {
		return clientDate;
	}

	public void setClientDate(Date clientDate) {
		this.clientDate = clientDate;
	}

	public Date getServerDate() {
		return serverDate;
	}

	public void setServerDate(Date serverDate) {
		this.serverDate = serverDate;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getRpCode() {
		return rpCode;
	}

	public void setRpCode(String rpCode) {
		this.rpCode = rpCode;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	
	public String getMcCode() {
		return mcCode;
	}

	public void setMcCode(String mcCode) {
		this.mcCode = mcCode;
	}
	
	public CreatedFrom getCreatedFrom() {
		return createdFrom;
	}

	public void setCreatedFrom(CreatedFrom createdFrom) {
		this.createdFrom = createdFrom;
	}

	public Boolean getIsDirect() {
		return isDirect;
	}

	public void setIsDirect(Boolean isDirect) {
		if (Util.isNotNull(isDirect)) {
			this.isDirect = isDirect;
		}
	}

	public Boolean getIsAssisted() {
		return isAssisted;
	}

	public void setIsAssisted(Boolean isAssisted) {
		if (Util.isNotNull(isAssisted)) {
			this.isAssisted = isAssisted;
		}
	}

	public Boolean getIsDefaultTransaction() {
		return isDefaultTransaction;
	}

	public void setIsDefaultTransaction(Boolean isDefaultTransaction) {
		if (Util.isNotNull(isDefaultTransaction)) {
			this.isDefaultTransaction = isDefaultTransaction;
		}
	}

	public Boolean getIsDefaultAssist() {
		return isDefaultAssist;
	}

	public void setIsDefaultAssist(Boolean isDefaultAssist) {
		if (Util.isNotNull(isDefaultAssist)) {
			this.isDefaultAssist = isDefaultAssist;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		result = prime * result + ((transactionSource == null) ? 0 : transactionSource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampaignTransaction other = (CampaignTransaction) obj;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		if (transactionSource != other.transactionSource)
			return false;
		return true;
	}

}
