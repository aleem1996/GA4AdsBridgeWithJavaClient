package com.gr.dm.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gr.dm.core.dao.ReportSummaryDao;
import com.gr.dm.core.dto.report.CostVsRevenueReportDto;
import com.gr.dm.core.dto.report.GenericReportDto;
import com.gr.dm.core.dto.report.ReportDto;
import com.gr.dm.core.dto.report.StackedReportDto;
import com.gr.dm.core.entity.CampaignDetailSource;
import com.gr.dm.core.repository.CampaignDetailRepository;
import com.gr.dm.core.util.Util;

@Service
public class ReportSummaryService {

	@Autowired
	ReportSummaryDao reportSummaryDao;

	@Autowired
	Environment env;

	@Autowired
	CampaignDetailRepository campaignDetailRepository;
	
	private final String lableColumnName = "Label";
	private final String sourceName = "SOURCE__NAME";

	@Cacheable(value = "costVsRoiCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#statType).concat('-').concat(#view).concat('-').concat(#frequency)")
	public StackedReportDto getCostVsRevenueReportOld(CampaignDetailSource campaignDetailSource, String frequency,
			Date startDate, Date endDate, String view, String statType) {
		String query = "";
		String reportName = "report.costvsrevenue." + frequency;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("view", view);
		paramMap.put("statType", statType);
		paramMap.put("frequency", frequency);
		paramMap.put("calledFrom", "costVsRevenueReport");
		String source = "";

		if (Util.isNotNull(campaignDetailSource)) {
			source = campaignDetailSource.toString();
			query = env.getProperty(reportName);
			query = query.replaceAll(sourceName, source);
			paramMap.put("source", source);
			StackedReportDto stackedReportDto = new StackedReportDto();
			updateStackedReport(query, stackedReportDto, paramMap);
			return stackedReportDto;
		} else {
			CampaignDetailSource[] campaignDetailSources = new CampaignDetailSource[] { CampaignDetailSource.Analytics,
					CampaignDetailSource.Bing, CampaignDetailSource.Email, CampaignDetailSource.Facebook, CampaignDetailSource.StackAdapt };
			StackedReportDto stackedReportDto = new StackedReportDto();
			for (CampaignDetailSource detailSource : campaignDetailSources) {
				source = detailSource.toString();
				paramMap.put("source", source);
				query = env.getProperty(reportName);
				query = query.replaceAll(sourceName, source);
				updateStackedReport(query, stackedReportDto, paramMap);
			}
			return stackedReportDto;
		}

	}
	
	@Cacheable(value = "costVsRoiCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#statType).concat('-').concat(#view).concat('-').concat(#frequency)")
	public StackedReportDto getCostVsRevenueReport(CampaignDetailSource campaignDetailSource, String frequency,
			Date startDate, Date endDate, String view, String statType) {
		
		List<CostVsRevenueReportDto> dataOfAllSources = new ArrayList<>();
		if (Util.isNotNull(campaignDetailSource)) {

			if ("weekly".equals(frequency)) {
				dataOfAllSources = campaignDetailRepository.getCostVsRevenueWeekly(statType, view, startDate, endDate, Arrays.asList(campaignDetailSource));
			} else if ("monthly".equals(frequency)) {
				dataOfAllSources = campaignDetailRepository.getCostVsRevenueMonthly(statType, view, startDate, endDate, Arrays.asList(campaignDetailSource));			
			} else if ("yearly".equals(frequency)) {
				dataOfAllSources = campaignDetailRepository.getCostVsRevenueYearly(statType, view, startDate, endDate, Arrays.asList(campaignDetailSource));			
			}
		} else {
			CampaignDetailSource[] campaignDetailSources = new CampaignDetailSource[] { CampaignDetailSource.Analytics,
					CampaignDetailSource.Bing, CampaignDetailSource.Email, CampaignDetailSource.Facebook, CampaignDetailSource.StackAdapt };
			if ("weekly".equals(frequency)) {
				dataOfAllSources = campaignDetailRepository.getCostVsRevenueWeekly(statType, view, startDate, endDate, Arrays.asList(campaignDetailSources));
			} else if ("monthly".equals(frequency)) {
				dataOfAllSources = campaignDetailRepository.getCostVsRevenueMonthly(statType, view, startDate, endDate, Arrays.asList(campaignDetailSources));			
			} else if ("yearly".equals(frequency)) {
				dataOfAllSources = campaignDetailRepository.getCostVsRevenueYearly(statType, view, startDate, endDate, Arrays.asList(campaignDetailSources));			
			}
		}
		
		Map<CampaignDetailSource, List<CostVsRevenueReportDto>> groupBySource = dataOfAllSources.stream()
		        .collect(Collectors.groupingBy(CostVsRevenueReportDto::getSource));

		List<String> ranges = new ArrayList<>();
		if ("weekly".equals(frequency)) {
			ranges = Util.generateWeekArray(startDate, endDate);
		} else if ("monthly".equals(frequency)) {
			ranges = Util.generateMonthArray(startDate, endDate);
		} else if ("yearly".equals(frequency)) {
			ranges = Util.generateYearArray(startDate, endDate);
		}
		
		StackedReportDto stackedReport = new StackedReportDto();
		stackedReport.setLabels(ranges);

		List<ReportDto> reportsDtoList = new ArrayList<>();
		for (CampaignDetailSource key: groupBySource.keySet()) {
			String [] datasetsCost = new String[ranges.size()];
			String [] datasetsRevenue = new String[ranges.size()];
			List<CostVsRevenueReportDto> reports = groupBySource.get(key);
			for(CostVsRevenueReportDto report: reports) {
				int index = ranges.indexOf(report.getLabel());
				if (index > -1) {
					datasetsCost[index] = String.valueOf(report.getCost());
					datasetsRevenue[index] = String.valueOf(report.getRevenue());
				}
			}
			ReportDto costReportDto = new ReportDto();
			ReportDto revenueReportDto = new ReportDto();
			costReportDto.setLabel(key + " Cost");
			costReportDto.setStack(key + " Cost");
			costReportDto.setData(Arrays.asList(datasetsCost));
			revenueReportDto.setLabel(key + " Revenue");
			revenueReportDto.setStack(key + " Revenue");
			revenueReportDto.setData(Arrays.asList(datasetsRevenue));
			reportsDtoList.add(costReportDto);
			reportsDtoList.add(revenueReportDto);
		};
		stackedReport.setDatasets(reportsDtoList);
		return stackedReport;
	}

	@Cacheable(value = "costVsRoiCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat('-').concat(#frequency)")
	public StackedReportDto getCostVsRevenueReportByClick(CampaignDetailSource campaignDetailSource, String frequency,
			Date startDate, Date endDate) {
		String query = "";
		String reportName = "report.costvsrevenue.clickcentric." + frequency;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		String source = "";

		if (Util.isNotNull(campaignDetailSource)) {
			source = campaignDetailSource.toString();
			query = env.getProperty(reportName);
			query = query.replaceAll(sourceName, source);
			paramMap.put("source", source);
			StackedReportDto stackedReportDto = new StackedReportDto();
			updateStackedReport(query, stackedReportDto, paramMap);
			return stackedReportDto;
		} else {
			CampaignDetailSource[] campaignDetailSources = new CampaignDetailSource[] { CampaignDetailSource.Adwords,
					CampaignDetailSource.Bing, CampaignDetailSource.Email, CampaignDetailSource.Facebook, CampaignDetailSource.StackAdapt };
			StackedReportDto stackedReportDto = new StackedReportDto();
			for (CampaignDetailSource detailSource : campaignDetailSources) {
				source = detailSource.toString();
				paramMap.put("source", source);
				query = env.getProperty(reportName);
				query = query.replaceAll(sourceName, CampaignDetailSource.Adwords.equals(detailSource)
						? CampaignDetailSource.Analytics.toString() : source);
				updateStackedReport(query, stackedReportDto, paramMap);
			}
			return stackedReportDto;
		}

	}

	@Cacheable(value = "productSalesCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#statType)")
	public StackedReportDto getProductSalesReport(Date startDate, Date endDate, String statType) {
		String reportName = "report.productsales" + ("click_centric".equals(statType) ? ".clickcentric" : "");
		String query = env.getProperty(reportName);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		StackedReportDto stackedReportDto = new StackedReportDto();
		updateStackedReport(query, stackedReportDto, paramMap);
		stackedReportDto.getLabels().add("All");

		for (ReportDto dto : stackedReportDto.getDatasets()) {
			Double sum = 0.0;
			for (String str : dto.getData()) {
				Double dbl = Double.valueOf(str);
				sum += dbl;
			}

			dto.getData().add(String.format("%,.2f", sum).replaceAll(",", ""));
		}
		return stackedReportDto;
	}

	@Cacheable(value = "roiCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#statType).concat('-').concat(#view).concat('-').concat(#sources).concat('-').concat(#frequency)")
	public StackedReportDto getRoiReport(String frequency, Date startDate, Date endDate,
			String[] sources, String view, String statType) {
		String reportName = "click_centric".equals(statType) ? "report.roi.clickcentric." : "report.roi.";
		reportName = reportName + frequency;
		StackedReportDto stackedReportDto = generateReport(startDate, endDate, sources, reportName, view, statType, frequency);
		return stackedReportDto;
	}

	@Cacheable(value = "profitLossCache", key = "#startDate.toString().concat('-').concat(#endDate.toString()).concat('-').concat(#statType).concat('-').concat(#view).concat('-').concat(#sources).concat('-').concat(#frequency)")
	public StackedReportDto getProfileLossReport(String frequency, Date startDate, Date endDate,
			String[] sources, String view, String statType) {
		String reportName = "click_centric".equals(statType) ? "report.profitloss.clickcentric." : "report.profitloss.";
		reportName = reportName + frequency;
		StackedReportDto stackedReportDto = generateReport(startDate, endDate, sources, reportName, view, statType, frequency);
		return stackedReportDto;
	}

	private StackedReportDto generateReportOld(Date startDate, Date endDate, CampaignDetailSource[] sources,
			String reportName, String view, String statType) {
		String query = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("view", view);
		paramMap.put("statType", statType);
		
		StackedReportDto stackedReportDto = new StackedReportDto();
		List<String> allSources = new ArrayList<String>();
		for (CampaignDetailSource campaignDetailSource : sources) {
			String source = "click_centric".equals(statType) && CampaignDetailSource.Analytics.equals(campaignDetailSource) ? CampaignDetailSource.Adwords.toString() : campaignDetailSource.toString();
			paramMap.put("source", source);
			query = env.getProperty(reportName);
			query = query.replaceAll(sourceName, campaignDetailSource.toString());
			updateStackedReport(query, stackedReportDto, paramMap);
			allSources.add(source);
		}

		query = env.getProperty(reportName);
		query = query.replaceAll(sourceName, "All");
		paramMap.put("source", allSources);
		updateStackedReport(query, stackedReportDto, paramMap);
		return stackedReportDto;
	}

	private StackedReportDto generateReport(Date startDate, Date endDate, String[] sources,
			String reportName, String view, String statType, String frequency) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("view", view);
		paramMap.put("statType", statType);
		
		List<GenericReportDto> reports = new ArrayList<>();
		if (reportName.contains("report.profit")) {
			if ("weekly".equals(frequency)) {
				reports = campaignDetailRepository.getProfitLossWeekly(statType, view, startDate, endDate, Arrays.asList(sources));
			} else if ("monthly".equals(frequency)) {
				reports = campaignDetailRepository.getProfitLossMonthly(statType, view, startDate, endDate, Arrays.asList(sources));
			} else if ("yearly".equals(frequency)) {
				reports = campaignDetailRepository.getProfitLossYearly(statType, view, startDate, endDate, Arrays.asList(sources));
			}			
		} else if (reportName.contains("report.roi")) {
			if ("weekly".equals(frequency)) {
				reports = campaignDetailRepository.getRoiWeekly(statType, view, startDate, endDate, Arrays.asList(sources));
			} else if ("monthly".equals(frequency)) {
				reports = campaignDetailRepository.getRoiMonthly(statType, view, startDate, endDate, Arrays.asList(sources));
			} else if ("yearly".equals(frequency)) {
				reports = campaignDetailRepository.getRoiYearly(statType, view, startDate, endDate, Arrays.asList(sources));
			}
		}
		
		Map<String, List<GenericReportDto>> groupBySource = reports.stream()
		        .collect(Collectors.groupingBy(GenericReportDto::getSource));

		List<String> ranges = new ArrayList<>();
		if ("weekly".equals(frequency)) {
			ranges = Util.generateWeekArray(startDate, endDate);
		} else if ("monthly".equals(frequency)) {
			ranges = Util.generateMonthArray(startDate, endDate);
		} else if ("yearly".equals(frequency)) {
			ranges = Util.generateYearArray(startDate, endDate);
		}
		
		StackedReportDto stackedReport = new StackedReportDto();
		stackedReport.setLabels(ranges);
		
		List<ReportDto> reportsDtoList = new ArrayList<>();
		for (String key: groupBySource.keySet()) {
			String [] datasetsValue = new String[ranges.size()];
			for (int i = 0; i < datasetsValue.length; i++) {
				datasetsValue[i] = "0.0";
			}
			List<GenericReportDto> sourceReports = groupBySource.get(key);
			for(GenericReportDto report: sourceReports) {
				int index = ranges.indexOf(report.getLabel());
				if (index > -1) {
					datasetsValue[index] = String.valueOf(report.getVal());
				}
			}
			ReportDto valueReportDto = new ReportDto();
			valueReportDto.setLabel(key.toString());
			valueReportDto.setStack(key.toString());
			valueReportDto.setData(Arrays.asList(datasetsValue));
			reportsDtoList.add(valueReportDto);
		};
		
		stackedReport.setDatasets(reportsDtoList);
		return stackedReport;
	}
	
	private void updateStackedReport(String query, StackedReportDto stackedReportDto, Map<String, Object> paramMap) {
		Map<String, List<String>> mappedData = reportSummaryDao.executeQuery(query, paramMap);
		
		stackedReportDto.setLabels(mappedData.get(lableColumnName));
		List<ReportDto> reportDtoList = new ArrayList<ReportDto>();
		mappedData.forEach((key, value) -> {
			
			if (lableColumnName.equals(key)) {
				return;			
			}
			
			ReportDto reportDto = new ReportDto();
			reportDto.setLabel(key);
			reportDto.setStack(key);
			reportDto.setData(value);

			reportDtoList.add(reportDto);
		});
		
		if (Util.isNull(stackedReportDto.getDatasets())) {
			stackedReportDto.setDatasets(reportDtoList);
		} else {
			stackedReportDto.getDatasets().addAll(reportDtoList);
		}
	}
}
