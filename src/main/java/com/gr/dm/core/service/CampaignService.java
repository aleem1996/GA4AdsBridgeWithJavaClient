package com.gr.dm.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gr.dm.core.dto.BingCampaignDto;
import com.gr.dm.core.dto.BingCampaignListDto;
import com.gr.dm.core.dto.BingCampaignStatsDto;
import com.gr.dm.core.dto.CampaignTransactionDto;
import com.gr.dm.core.dto.FacebookCampaignDto;
import com.gr.dm.core.dto.FacebookCampaignListDto;
import com.gr.dm.core.dto.FbCampaignStatsDto;
import com.gr.dm.core.dto.GoogleCampaignDto;
import com.gr.dm.core.dto.GoogleCampaignListDto;
import com.gr.dm.core.dto.StatsDto;
import com.gr.dm.core.dto.report.CampaignPerformanceDto;
import com.gr.dm.core.dto.report.CampaignSummaryDto;
import com.gr.dm.core.dto.report.LatestDateDto;
import com.gr.dm.core.dto.report.SourceCostDto;
import com.gr.dm.core.dto.report.WeeklyCostDto;
import com.gr.dm.core.dto.report.WeeklyRevenueDto;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignAttribution;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.entity.CampaignHistory;
import com.gr.dm.core.entity.CampaignSource;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.repository.CampaignAttributionRepository;
import com.gr.dm.core.repository.CampaignDetailRepository;
import com.gr.dm.core.repository.CampaignHistoryRepository;
import com.gr.dm.core.repository.CampaignRepository;
import com.gr.dm.core.repository.CampaignTransactionRepository;
import com.gr.dm.core.util.Constants;
import com.gr.dm.core.util.Util;

/**
 * @author ufarooq
 */
@Service
public class CampaignService {
	
	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	CampaignDetailRepository campaignDetailRepository;
	
	@Autowired
	CampaignTransactionRepository campaignTransactionRepository;
	
	@Autowired
	CampaignAttributionRepository campaignAttributionRepository;
	
	@Autowired
	CampaignHistoryRepository campaignHistoryRepository;
	
	@Autowired
	CrmService crmService;
	
	public Campaign findCampaign(String campaignName, CampaignSource campaignSource) {
		return campaignRepository.findByNameAndCampaignSource(campaignName, campaignSource);
	}
	
	public Campaign findCampaignExcludingWhiteSpace(String campaignName, CampaignSource campaignSource) {
		campaignName = campaignName.replaceAll(" ", "");
		Campaign campaign = campaignRepository.findCampaignExcludingWhiteSpace(campaignName, campaignSource.getValue());
		if (Util.isNotNull(campaign)) {
			return campaign;
		}
		CampaignHistory campaignHistory = campaignHistoryRepository
				.findCampaignExcludingWhiteSpace(campaignName);
		if (Util.isNotNull(campaignHistory)) {
			return campaignHistory.getCampaign();
		}
		return null;
	}
	
	public Campaign findCampaign(String campaignId) {
		return campaignRepository.findByCampaignId(campaignId);
	}

	public void saveCampaign(List<Campaign> campaigns) {
		for (Campaign campaign : campaigns) {
			saveCampaign(campaign);
		}
	}

	public void saveCampaign(Campaign campaign) {
		Campaign savedCampaign = campaignRepository.findByCampaignIdAndCampaignSource(campaign.getCampaignId(),
				campaign.getCampaignSource());
		if (Util.isNull(savedCampaign)) {
			campaignRepository.save(campaign);
			createCampaignInCrm(campaign);
		} else {
			boolean isNameChanged = !savedCampaign.getName().equals(campaign.getName());
			if (isNameChanged) {
				saveCampaignHistory(savedCampaign, campaign.getName());
			}
			if (isNameChanged || !savedCampaign.getActive().equals(campaign.getActive())) {
				savedCampaign.setName(campaign.getName());
				savedCampaign.setActive(campaign.getActive());
				campaignRepository.save(savedCampaign);
				createCampaignInCrm(savedCampaign);
			}
		}
	}
	
	public void saveCampaignHistory(Campaign savedCampaign, String newName) {
		CampaignHistory campaignHistory = new CampaignHistory(savedCampaign.getName(), newName);
		campaignHistory.setCampaign(savedCampaign);
		campaignHistoryRepository.save(campaignHistory);
	}

	public void createCampaignInCrm(Campaign campaign) {
		try {
			crmService.createCampaignInCrm(campaign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveCampaignDetail(List<CampaignDetail> campaignDetails) {
		for (CampaignDetail campaignDetail : campaignDetails) {
			saveCampaignDetail(campaignDetail);
		}
	}

	public void saveCampaignDetail(CampaignDetail campaignDetail) {
		CampaignDetail savedDetail = getSavedCampaignDetail(campaignDetail);
		if (Util.isNull(savedDetail)) {
			campaignDetailRepository.save(campaignDetail);
		} else {
			savedDetail.setClicks(campaignDetail.getClicks());
			savedDetail.setCost(campaignDetail.getCost());
			savedDetail.setImpressions(campaignDetail.getImpressions());
			savedDetail.setRevenue(campaignDetail.getRevenue());
			savedDetail.setTransactionCount(campaignDetail.getTransactionCount());
			savedDetail.setLastUpdated(campaignDetail.getLastUpdated());
			campaignDetailRepository.save(savedDetail);
		}
	}
	
	public void saveGrCampaignDetail(CampaignDetail campaignDetail) {
		CampaignDetail savedDetail = getSavedCampaignDetail(campaignDetail);
		if (Util.isNull(savedDetail)) {
			campaignDetailRepository.save(campaignDetail);
		} else {
			savedDetail.setGrRevenue(campaignDetail.getGrRevenue());
			savedDetail.setGrTransactionCount(campaignDetail.getGrTransactionCount());
			campaignDetailRepository.save(savedDetail);
		}
	}
	
	public CampaignDetail getSavedCampaignDetail(CampaignDetail campaignDetail) {
		CampaignDetail savedDetail = campaignDetailRepository.getCampaignDetail(campaignDetail.getCampaignId(),
				campaignDetail.getCampaignDetailSource(), campaignDetail.getStartDate(),
				campaignDetail.getEndDate());
		return savedDetail;
	}
	
	/**
	 * This method is used to insert an empty record in CampaignDetail for the
	 * given input parameters. Purpose: In case a transaction exists for a
	 * particular date and no entry for Campaign Detail exists, trigger and
	 * stored procedure on Campaign Detail won't update the stats against that
	 * transaction record.
	 * 
	 * @param campaignId
	 * @param campaignDetailSource
	 * @param startDate
	 * @param endDate
	 */
	public void insertDummyRecordInCampaignDetail(String campaignId, CampaignDetailSource campaignDetailSource,
			Date startDate, Date endDate) {
		CampaignDetail campaignDetail = new CampaignDetail();
		campaignDetail.setCampaignId(campaignId);
		campaignDetail.setCampaignDetailSource(campaignDetailSource);
		campaignDetail.setStartDate(startDate);
		campaignDetail.setEndDate(endDate);

		CampaignDetail savedDetail = getSavedCampaignDetail(campaignDetail);
		if (Util.isNull(savedDetail)) {
			campaignDetailRepository.save(campaignDetail);
		}
	}
	
	
	public void saveCampaignAttribution(List<CampaignAttribution> campaignAttributions) {
		for (CampaignAttribution campaignAttribution : campaignAttributions) {
			saveCampaignAttribution(campaignAttribution);
		}
	}

	public void saveCampaignAttribution(CampaignAttribution campaignAttribution) {
		CampaignAttribution savedAttribution = getSavedCampaignAttribution(campaignAttribution);
		if (Util.isNull(savedAttribution)) {
			campaignAttributionRepository.save(campaignAttribution);
		} else {
			savedAttribution.setPurchases1DayView(campaignAttribution.getPurchases1DayView());
			savedAttribution.setPurchases7DayView(campaignAttribution.getPurchases7DayView());
			savedAttribution.setPurchases28DayView(campaignAttribution.getPurchases28DayView());
			savedAttribution.setPurchases1DayClick(campaignAttribution.getPurchases1DayClick());
			savedAttribution.setPurchases7DayClick(campaignAttribution.getPurchases7DayClick());
			savedAttribution.setPurchases28DayClick(campaignAttribution.getPurchases28DayClick());
			savedAttribution.setPurchasesGreaterThan28DayClick(campaignAttribution.getPurchasesGreaterThan28DayClick());
			savedAttribution.setRevenue1DayView(campaignAttribution.getRevenue1DayView());
			savedAttribution.setRevenue7DayView(campaignAttribution.getRevenue7DayView());
			savedAttribution.setRevenue28DayView(campaignAttribution.getRevenue28DayView());
			savedAttribution.setRevenue1DayClick(campaignAttribution.getRevenue1DayClick());
			savedAttribution.setRevenue7DayClick(campaignAttribution.getRevenue7DayClick());
			savedAttribution.setRevenue28DayClick(campaignAttribution.getRevenue28DayClick());
			savedAttribution.setRevenueGreaterThan28DayClick(campaignAttribution.getRevenueGreaterThan28DayClick());
			campaignAttributionRepository.save(savedAttribution);
		}
	}
	
	public void saveGrCampaignAttribution(CampaignAttribution campaignAttribution) {
		CampaignAttribution savedAttribution = getSavedCampaignAttribution(campaignAttribution);
		if (Util.isNull(savedAttribution)) {
			campaignAttributionRepository.save(campaignAttribution);
		} else {
			savedAttribution.setPurchases1DayView(
					savedAttribution.getPurchases1DayView() + campaignAttribution.getPurchases1DayView());
			savedAttribution.setPurchases7DayView(
					savedAttribution.getPurchases7DayView() + campaignAttribution.getPurchases7DayView());
			savedAttribution.setPurchases28DayView(
					savedAttribution.getPurchases28DayView() + campaignAttribution.getPurchases28DayView());
			savedAttribution.setPurchases1DayClick(
					savedAttribution.getPurchases1DayClick() + campaignAttribution.getPurchases1DayClick());
			savedAttribution.setPurchases7DayClick(
					savedAttribution.getPurchases7DayClick() + campaignAttribution.getPurchases7DayClick());
			savedAttribution.setPurchases28DayClick(
					savedAttribution.getPurchases28DayClick() + campaignAttribution.getPurchases28DayClick());
			savedAttribution.setRevenue1DayView(
					savedAttribution.getRevenue1DayView() + campaignAttribution.getRevenue1DayView());
			savedAttribution.setRevenue7DayView(
					savedAttribution.getRevenue7DayView() + campaignAttribution.getRevenue7DayView());
			savedAttribution.setRevenue28DayView(
					savedAttribution.getRevenue28DayView() + campaignAttribution.getRevenue28DayView());
			savedAttribution.setRevenue1DayClick(
					savedAttribution.getRevenue1DayClick() + campaignAttribution.getRevenue1DayClick());
			savedAttribution.setRevenue7DayClick(
					savedAttribution.getRevenue7DayClick() + campaignAttribution.getRevenue7DayClick());
			savedAttribution.setRevenue28DayClick(
					savedAttribution.getRevenue28DayClick() + campaignAttribution.getRevenue28DayClick());
			campaignAttributionRepository.save(savedAttribution);
		}
	}
	
	
	public CampaignAttribution getSavedCampaignAttribution(CampaignAttribution campaignAttribution) {
		return campaignAttributionRepository.getCampaignAttribution(
				campaignAttribution.getCampaignId(), campaignAttribution.getStartDate(),
				campaignAttribution.getEndDate(), campaignAttribution.getIsManual());
	}

	public List<Campaign> getCampaigns() {
		return (List<Campaign>) campaignRepository.findAll();
	}

	public List<CampaignDetail> getCampaignsDetail() {
		return (List<CampaignDetail>) campaignDetailRepository.findAll();
	}
	
	/**
	 * Fetch data wrapped in dto
	 */
	public GoogleCampaignListDto getGoogleCampaigns(Date startDate, Date endDate, String view) {
		List<GoogleCampaignDto> campaigns = campaignRepository.getGoogleCampaigns(startDate, endDate, view);
		GoogleCampaignListDto googleCampaignListDto = new GoogleCampaignListDto();
		googleCampaignListDto.setCampaigns(campaigns);
		googleCampaignListDto.setStats(getGoogleCampaignStats(startDate, endDate, view));
		return googleCampaignListDto;
	}
	
	public StatsDto getGoogleCampaignStats(Date startDate, Date endDate) {
		return getGoogleCampaignStats(startDate, endDate, Constants.DEFAULT_VIEW_TYPE);
	}
	
	public StatsDto getGoogleCampaignStats(Date startDate, Date endDate, String view) {
			return campaignRepository.getGoogleStats(startDate, endDate, view);
	}

	public void deleteCampaignsData() {
		campaignRepository.deleteAll();
		campaignDetailRepository.deleteAll();
	}

	public FacebookCampaignListDto getFacebookCampaigns(Date startDate, Date endDate, CampaignSource campaignSource) {
		List<FacebookCampaignDto> campaigns = campaignRepository.getFacebookCampaigns(startDate, endDate, campaignSource);
		FacebookCampaignListDto campaignListDto = new FacebookCampaignListDto();
		campaignListDto.setCampaigns(campaigns);
		campaignListDto.setStats(getFacebookCampaignStats(startDate, endDate, campaignSource));
		return campaignListDto;
	}
	
	public FbCampaignStatsDto getFacebookCampaignStats(Date startDate, Date endDate, CampaignSource campaignSource) {
		return campaignRepository.getFbStats(startDate, endDate, campaignSource);
	}
	
	public BingCampaignListDto getBingCampaigns(Date startDate, Date endDate, String view) {
		List<BingCampaignDto> campaigns = campaignRepository.getBingCampaigns(startDate, endDate, view);
		BingCampaignListDto campaignListDto = new BingCampaignListDto();
		campaignListDto.setCampaigns(campaigns);
		campaignListDto.setStats(getBingCampaignStats(startDate, endDate, view));
		return campaignListDto;
	}
	
	public BingCampaignStatsDto getBingCampaignStats(Date startDate, Date endDate) {
		return getBingCampaignStats(startDate, endDate, Constants.PARTIAL_VIEW);
	}
	
	public BingCampaignStatsDto getBingCampaignStats(Date startDate, Date endDate, String view) {
		return campaignRepository.getBingStats(startDate, endDate, view);
	}
	
	public BingCampaignListDto getStackAdaptCampaigns(Date startDate, Date endDate) {
		List<BingCampaignDto> campaigns = campaignRepository.getStackAdaptCampaigns(startDate, endDate);
		BingCampaignListDto campaignListDto = new BingCampaignListDto();
		campaignListDto.setCampaigns(campaigns);
		campaignListDto.setStats(campaignRepository.getStackAdaptStats(startDate, endDate));
		return campaignListDto;
	}
	
	public BingCampaignStatsDto getStackAdaptCampaignStats(Date startDate, Date endDate) {
		return campaignRepository.getStackAdaptStats(startDate, endDate);
	}


	public List<CampaignTransactionDto> getCampaignTransactions(String id, Date startDate, Date endDate, CampaignDetailSource campaignDetailSource) {
		
		List<CampaignTransaction> transactions = campaignTransactionRepository.getCampaignTransactionsBetweenDateRange(id, startDate, endDate, campaignDetailSource);
		List<CampaignTransactionDto> campaignTransactionDtoList = new ArrayList<CampaignTransactionDto>();
		
		ModelMapper campaignTransactionMapper = new ModelMapper();
		for (CampaignTransaction campaignTransaction : transactions) {
			
			CampaignTransactionDto campaignTransactionDto = new CampaignTransactionDto();
			
			campaignTransactionMapper.map(campaignTransaction, campaignTransactionDto);
			campaignTransactionDtoList.add(campaignTransactionDto);
		}
		return campaignTransactionDtoList;
	}
	
	public void updateAssistedConversionsData() {
		campaignDetailRepository.updateAssistedConversionsData();
	}
	
	@Cacheable(value = "campaignSummaryCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#statType)")
	public List<CampaignSummaryDto> getCampaignSummary(Date startDate, Date endDate, String statType) {
		if ("click_centric".equals(statType)) {
			return campaignDetailRepository.getCampaignSummaryByClick(startDate, endDate);
		}
		return campaignDetailRepository.getCampaignSummary(startDate, endDate, statType);
	}
	
	@Cacheable(value = "campaignPerformanceCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#view)")
	public List<CampaignPerformanceDto> getCampaignPerformanceReport(Date startDate, Date endDate, String view) {
		return campaignDetailRepository.getCampaignPerformanceReport(startDate, endDate, view);
	}
	
	public LatestDateDto getDateOfLastFetchedData(CampaignDetailSource campaignDetailSource) {
		return campaignDetailRepository.getDateOfLastFetchedData(campaignDetailSource);
	}
	
	public List<WeeklyCostDto> getCostByWeek(Date startDate, Date endDate) {
		return campaignDetailRepository.getCostByWeek(startDate, endDate);
	}
	
	public List<WeeklyRevenueDto> getRevenueByWeek(Date startDate, Date endDate) {
		return campaignDetailRepository.getRevenueByWeek(startDate, endDate);
	}
	
	public List<SourceCostDto> getCost(Date startDate, Date endDate, CampaignDetailSource... campaignDetailSources){
		return campaignDetailRepository.getCost(startDate, endDate, campaignDetailSources);
	}
}
