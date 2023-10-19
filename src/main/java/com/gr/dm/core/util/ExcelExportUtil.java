package com.gr.dm.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gr.dm.core.dto.BingCampaignStatsDto;
import com.gr.dm.core.dto.FbCampaignStatsDto;
import com.gr.dm.core.dto.StatsDto;
import com.gr.dm.core.dto.report.CampaignSummaryDto;
import com.gr.dm.core.dto.report.ExcelDataDto;
import com.gr.dm.core.dto.report.MembershipStatsDto;
import com.gr.dm.core.dto.report.WeeklyCostDto;
import com.gr.dm.core.dto.report.WeeklyRevenueDto;
import com.gr.dm.core.entity.CampaignDetailSource;

public class ExcelExportUtil {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

	public static byte[] exportCampaignStats(List<CampaignSummaryDto> campaignSummaryDtoList,
			FbCampaignStatsDto fbCampaignStats, BingCampaignStatsDto bingCampaignStats, StatsDto googleCampaignStats,
			List<MembershipStatsDto> membershipStatsList, List<WeeklyCostDto> weeklyCostDtos,
			List<WeeklyRevenueDto> weeklyRevenueDtos, Date startDate, Date endDate) throws IOException {

		Workbook workbook = new XSSFWorkbook();
		String dateString = dateFormat.format(startDate) + " to " + dateFormat.format(endDate);
		populateWorkbook(workbook, "Summary", 0, 0, Arrays.asList("Global Rescue", dateString),
				getCampaignSummaryMap(campaignSummaryDtoList));
		populateWorkbook(workbook, "Facebook Stats", 0, 0, Arrays.asList("Global Rescue", "Facebook: " + dateString),
				getFbStatsMap(fbCampaignStats));
		populateWorkbook(workbook, "Google Stats", 0, 0, Arrays.asList("Global Rescue", "Google: " + dateString),
				getGoogleStatsMap(googleCampaignStats));
		populateWorkbook(workbook, "Bing Stats", 0, 0, Arrays.asList("Global Rescue", "Bing: " + dateString),
				getBingStatsMap(bingCampaignStats));
		populateWorkbook(workbook, "Weekly Spend", 0, 0, Arrays.asList("Marketing Media Spend by Week"),
				getWeeklyCostMap(weeklyCostDtos));
		populateWorkbook(workbook, "Weekly Spend", 0, 6, Arrays.asList("Revenue by Week"),
				getWeeklyRevenueMap(weeklyRevenueDtos));

		int rowNum = 0;
		for (MembershipStatsDto membershipStatsDto : membershipStatsList) {
			Map<String, List<Object>> map = getMembershipStatsMap(membershipStatsDto);
			populateWorkbook(workbook, "Membership Stats", rowNum, 0,
					Arrays.asList("Global Rescue", membershipStatsDto.getSource().toString() + ": " + dateString), map);
			rowNum = rowNum + 7;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		return baos.toByteArray();
	}

	private static void populateWorkbook(Workbook workbook, String sheetName, int rowNumber, int columnNumber,
			List<String> headers, Map<String, List<Object>> dataMap) {

		Sheet spreadsheet = workbook.getSheet(sheetName);
		if (Util.isNull(spreadsheet)) {
			spreadsheet = workbook.createSheet(sheetName);
		}

		Integer maxSize = dataMap.entrySet().stream().max(Comparator.comparingInt(entry -> entry.getValue().size()))
				.get().getValue().size();

		for (int i = 0; i < headers.size(); i++) {
			Row row0 = spreadsheet.getRow(rowNumber);
			if (Util.isNull(row0)) {
				row0 = spreadsheet.createRow(rowNumber);
			}
			Cell cell0 = row0.createCell(columnNumber);
			CellStyle cellStyle = row0.getSheet().getWorkbook().createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			Font font = workbook.createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 10);
			cellStyle.setFont(font);
			cell0.setCellStyle(cellStyle);
			cell0.setCellValue(headers.get(i));
			CellRangeAddress rangeAddress = new CellRangeAddress(rowNumber, rowNumber, columnNumber,
					columnNumber + maxSize);
			spreadsheet.addMergedRegion(rangeAddress);
			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangeAddress, spreadsheet, workbook);
			RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangeAddress, spreadsheet, workbook);
			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangeAddress, spreadsheet, workbook);
			RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangeAddress, spreadsheet, workbook);
			rowNumber++;
		}

		for (int i = 0; i < dataMap.size(); i++) {
			String mapKey = dataMap.keySet().toArray()[i].toString();
			Row row = spreadsheet.getRow(rowNumber);
			if (Util.isNull(row)) {
				row = spreadsheet.createRow(rowNumber);
			}
			rowNumber++;
			Cell cell = row.createCell(columnNumber);
			CellStyle cellStyleText = createTextCellStyle(workbook);
			cell.setCellStyle(cellStyleText);
			cell.setCellValue(mapKey);

			for (int index = 1; index <= dataMap.get(mapKey).size(); index++) {

				Cell cell1 = row.createCell(columnNumber + index);
				Object valueObject = dataMap.get(mapKey).get(index - 1);
				cellStyleText = createTextCellStyle(workbook);
				if (valueObject instanceof ExcelDataDto) {
					ExcelDataDto excelDataDto = (ExcelDataDto) valueObject;
					if(excelDataDto.getType().equals(5) || excelDataDto.getType().equals(7)) {
						cellStyleText.setDataFormat(workbook.createDataFormat().getFormat("_($#,##0.00_);_($(#,##0.00);_(@_)"));
					} else {
						cellStyleText.setDataFormat(excelDataDto.getType().shortValue());
					}
					cell1.setCellStyle(cellStyleText);
					if (excelDataDto.getValue() instanceof Number) {
						cell1.setCellValue(Double.parseDouble(excelDataDto.getValue().toString()));
					} else if (excelDataDto.getValue() instanceof String) {
						cell1.setCellValue(excelDataDto.getValue().toString());
					}
				} else {
					String value = valueObject.toString();
					cell1.setCellStyle(cellStyleText);
					cell1.setCellValue(value);
				}
			}
		}

		/*for (int i = 0; i <= columnNumber + maxSize; i++) {
			spreadsheet.autoSizeColumn(i);
		}*/
		
		autosizeColumns(spreadsheet, columnNumber + maxSize);
	}
	
	public static void autosizeColumns(Sheet sheet, int numColumns) {
	    for (int i = 0; i <= numColumns; i++) {
	        sheet.autoSizeColumn(i);
	        sheet.setColumnWidth(i, sheet.getColumnWidth(i) + (5 * 256));
	    }
	}

	private static Map<String, List<Object>> getCampaignSummaryMap(List<CampaignSummaryDto> campaignSummaryDtoList) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		Double totalCost = 0.0;
		Double totalRevenue = 0.0;
		Long totalClicks = 0L;
		Long totalImpressions = 0L;
		Long totalCampaigns = 0L;
		Long totalConversions = 0L;
		Long totalNew = 0L;
		Long totalRenew = 0L;
		Long totalTi = 0L;
		Long totalMd = 0L;
		Double totalNewRev = 0.0;
		Double totalRenewRev = 0.0;
		Double totalTiRev = 0.0;

		for (CampaignSummaryDto campaignSummaryDto : campaignSummaryDtoList) {

			if (CampaignDetailSource.Adwords.toString().equals(campaignSummaryDto.getCampaignDetailSource())) {
				continue;
			}
			dataMap.put("Source", Arrays.asList("Cost", "Revenue", "New Rev.", "Renew Rev.", "TI Rev.", "Conv.", "CPC",
					"Clicks", "ROI", "Impressions", "Campaigns", "New", "Renew", "TI", "MD"));

			Object cost = new ExcelDataDto(campaignSummaryDto.getCost(), Constants.EXCEL_CELL_FORMAT_5);
			Object revenue = new ExcelDataDto(campaignSummaryDto.getRevenue(),
					Constants.EXCEL_CELL_FORMAT_5);
			Object cpc = new ExcelDataDto(campaignSummaryDto.getClicks() > 0
					? campaignSummaryDto.getCost() / campaignSummaryDto.getClicks() : 0.0,
					Constants.EXCEL_CELL_FORMAT_7);
			Object clicks = new ExcelDataDto(campaignSummaryDto.getClicks(), Constants.EXCEL_CELL_FORMAT_3);
			Object roi = new ExcelDataDto(campaignSummaryDto.getCost() > 0
					? campaignSummaryDto.getRevenue() / campaignSummaryDto.getCost() : 0.0,
					Constants.EXCEL_CELL_FORMAT_2);
			Object impressions = new ExcelDataDto(campaignSummaryDto.getImpressions(), Constants.EXCEL_CELL_FORMAT_3);
			Object campaigns = new ExcelDataDto(campaignSummaryDto.getTotalCampaigns(), Constants.EXCEL_CELL_FORMAT_3);
			Object conversions = new ExcelDataDto(campaignSummaryDto.getConversions(),
					Constants.EXCEL_CELL_FORMAT_3);
			Object newCount = new ExcelDataDto(campaignSummaryDto.getNewMembershipCount(),
					Constants.EXCEL_CELL_FORMAT_3);
			Object renewCount = new ExcelDataDto(campaignSummaryDto.getRenewedMembershipCount(),
					Constants.EXCEL_CELL_FORMAT_3);
			Object tiCount = new ExcelDataDto(campaignSummaryDto.getTiCount(), Constants.EXCEL_CELL_FORMAT_3);
			Object mdCount = new ExcelDataDto(campaignSummaryDto.getTotalDeviceCount(), Constants.EXCEL_CELL_FORMAT_3);
			Object newRev = new ExcelDataDto(campaignSummaryDto.getNewMembershipRevenue(),
					Constants.EXCEL_CELL_FORMAT_5);
			Object renewRev = new ExcelDataDto(campaignSummaryDto.getRenewedMembershipRevenue(),
					Constants.EXCEL_CELL_FORMAT_5);
			Object tiRev = new ExcelDataDto(campaignSummaryDto.getTiRevenue(), Constants.EXCEL_CELL_FORMAT_5);

			totalCost += campaignSummaryDto.getCost();
			totalRevenue += campaignSummaryDto.getRevenue();
			totalClicks += campaignSummaryDto.getClicks();
			totalImpressions += campaignSummaryDto.getImpressions();
			totalCampaigns += campaignSummaryDto.getTotalCampaigns();
			totalConversions += campaignSummaryDto.getConversions();
			totalNew += campaignSummaryDto.getNewMembershipCount();
			totalRenew += campaignSummaryDto.getRenewedMembershipCount();
			totalTi += campaignSummaryDto.getTiCount();
			totalMd += campaignSummaryDto.getTotalDeviceCount();
			totalNewRev += campaignSummaryDto.getNewMembershipRevenue();
			totalRenewRev += campaignSummaryDto.getRenewedMembershipRevenue();
			totalTiRev += campaignSummaryDto.getTiRevenue();

			dataMap.put(campaignSummaryDto.getCampaignDetailSource(),
					Arrays.asList(cost, revenue, newRev, renewRev, tiRev, conversions, cpc, clicks, roi, impressions,
							campaigns, newCount, renewCount, tiCount, mdCount));
		}

		dataMap.put("Total",
				Arrays.asList(new ExcelDataDto(totalCost, Constants.EXCEL_CELL_FORMAT_5),
						new ExcelDataDto(totalRevenue, Constants.EXCEL_CELL_FORMAT_5),
						new ExcelDataDto(totalNewRev, Constants.EXCEL_CELL_FORMAT_5),
						new ExcelDataDto(totalRenewRev, Constants.EXCEL_CELL_FORMAT_5),
						new ExcelDataDto(totalTiRev, Constants.EXCEL_CELL_FORMAT_5),
						new ExcelDataDto(totalConversions, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto((totalCost / totalClicks), Constants.EXCEL_CELL_FORMAT_7),
						new ExcelDataDto(totalClicks, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto((totalRevenue / totalCost), Constants.EXCEL_CELL_FORMAT_2),
						new ExcelDataDto(totalImpressions, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(totalCampaigns, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(totalNew, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(totalRenew, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(totalTi, Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(totalMd, Constants.EXCEL_CELL_FORMAT_3)));

		return dataMap;
	}

	private static Map<String, List<Object>> getMembershipStatsMap(MembershipStatsDto membershipStats) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		dataMap.put("", Arrays.asList("Count", "% Count", "Revenue", "% Revenue"));
		Long totalMemberships = membershipStats.getNewMemberships() + membershipStats.getRenewedMemberships();
		Double totalRevenue = membershipStats.getNewMembershipsRevenue()
				+ membershipStats.getRenewedMembershipsRevenue();
		Double newMembershipPercent = totalMemberships > 0
				? (membershipStats.getNewMemberships().doubleValue() / totalMemberships) : 0.0;
		Double renewedMembershipPercent = totalMemberships > 0
				? (membershipStats.getRenewedMemberships().doubleValue() / totalMemberships) : 0.0;
		Double newMembershipRevenuePercent = totalRevenue > 0
				? (membershipStats.getNewMembershipsRevenue() / totalRevenue) : 0.0;
		Double renewedMembershipRevenuePercent = totalRevenue > 0
				? (membershipStats.getRenewedMembershipsRevenue() / totalRevenue) : 0.0;

		dataMap.put("New",
				Arrays.asList(new ExcelDataDto(membershipStats.getNewMemberships(), Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(newMembershipPercent, Constants.EXCEL_CELL_FORMAT_0xa),
						new ExcelDataDto(membershipStats.getNewMembershipsRevenue(), Constants.EXCEL_CELL_FORMAT_7),
						new ExcelDataDto(newMembershipRevenuePercent, Constants.EXCEL_CELL_FORMAT_0xa)));

		dataMap.put("Renew",
				Arrays.asList(new ExcelDataDto(membershipStats.getRenewedMemberships(), Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(renewedMembershipPercent, Constants.EXCEL_CELL_FORMAT_0xa),
						new ExcelDataDto(membershipStats.getRenewedMembershipsRevenue(), Constants.EXCEL_CELL_FORMAT_7),
						new ExcelDataDto(renewedMembershipRevenuePercent, Constants.EXCEL_CELL_FORMAT_0xa)));

		dataMap.put("Total", Arrays.asList(new ExcelDataDto(totalMemberships, Constants.EXCEL_CELL_FORMAT_3), "",
				new ExcelDataDto(totalRevenue, Constants.EXCEL_CELL_FORMAT_7), ""));

		return dataMap;
	}

	private static Map<String, List<Object>> getFbStatsMap(FbCampaignStatsDto fbCampaignStats) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		dataMap.put("", Arrays.asList("7 Day Att", "28 Day Att"));
		dataMap.put("Stats", Arrays.asList("Results", ""));
		dataMap.put("Impressions",
				Arrays.asList(new ExcelDataDto(fbCampaignStats.getImpressions(), Constants.EXCEL_CELL_FORMAT_3), ""));
		dataMap.put("Clicks",
				Arrays.asList(new ExcelDataDto(fbCampaignStats.getClicks(), Constants.EXCEL_CELL_FORMAT_3), ""));
		Double cpc = fbCampaignStats.getClicks() > 0 ? fbCampaignStats.getCost() / fbCampaignStats.getClicks() : 0.0;
		dataMap.put("Cost Per Click (CPC)", Arrays.asList(new ExcelDataDto(cpc, Constants.EXCEL_CELL_FORMAT_7), ""));
		dataMap.put("Transactions/Leads",
				Arrays.asList(new ExcelDataDto(fbCampaignStats.getPurchases7Day(), Constants.EXCEL_CELL_FORMAT_3),
						new ExcelDataDto(fbCampaignStats.getPurchases28Day(), Constants.EXCEL_CELL_FORMAT_3)));
		dataMap.put("Revenue",
				Arrays.asList(new ExcelDataDto(fbCampaignStats.getRevenue7Day(), Constants.EXCEL_CELL_FORMAT_7),
						new ExcelDataDto(fbCampaignStats.getRevenue28Day(), Constants.EXCEL_CELL_FORMAT_7)));
		dataMap.put("Social Spend",
				Arrays.asList(new ExcelDataDto(fbCampaignStats.getCost(), Constants.EXCEL_CELL_FORMAT_7),
						new ExcelDataDto(fbCampaignStats.getCost(), Constants.EXCEL_CELL_FORMAT_7)));
		Double roi7Day = fbCampaignStats.getCost() > 0 ? fbCampaignStats.getRevenue7Day() / fbCampaignStats.getCost()
				: 0;
		Double roi28Day = fbCampaignStats.getCost() > 0 ? fbCampaignStats.getRevenue28Day() / fbCampaignStats.getCost()
				: 0;
		dataMap.put("ROAS", Arrays.asList(new ExcelDataDto(roi7Day, Constants.EXCEL_CELL_FORMAT_2),
				new ExcelDataDto(roi28Day, Constants.EXCEL_CELL_FORMAT_2)));
		return dataMap;
	}

	private static Map<String, List<Object>> getGoogleStatsMap(StatsDto googleStats) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		dataMap.put("", Arrays.asList("30 Day Lookback"));
		dataMap.put("Stats", Arrays.asList("Results"));
		dataMap.put("Impressions",
				Arrays.asList(new ExcelDataDto(googleStats.getImpressions(), Constants.EXCEL_CELL_FORMAT_3)));
		dataMap.put("Clicks", Arrays.asList(new ExcelDataDto(googleStats.getClicks(), Constants.EXCEL_CELL_FORMAT_3)));
		Double cpc = googleStats.getClicks() > 0 ? googleStats.getCost() / googleStats.getClicks() : 0.0;
		dataMap.put("Cost Per Click (CPC)", Arrays.asList(new ExcelDataDto(cpc, Constants.EXCEL_CELL_FORMAT_7)));
		dataMap.put("Transactions/Leads",
				Arrays.asList(new ExcelDataDto(googleStats.getGaConversions(), Constants.EXCEL_CELL_FORMAT_3)));
		dataMap.put("Revenue", Arrays
				.asList(new ExcelDataDto(googleStats.getGaRevenue(), Constants.EXCEL_CELL_FORMAT_7)));
		dataMap.put("Paid Search Spend",
				Arrays.asList(new ExcelDataDto(googleStats.getCost(), Constants.EXCEL_CELL_FORMAT_7)));
		Double roi7Day = googleStats.getCost() > 0 ? googleStats.getGaRevenue() / googleStats.getCost()
				: 0;
		dataMap.put("ROAS", Arrays.asList(new ExcelDataDto(roi7Day, Constants.EXCEL_CELL_FORMAT_2)));
		return dataMap;
	}

	private static Map<String, List<Object>> getBingStatsMap(BingCampaignStatsDto bingStats) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		dataMap.put("", Arrays.asList("30 Day Lookback"));
		dataMap.put("Stats", Arrays.asList("Results"));
		dataMap.put("Impressions",
				Arrays.asList(new ExcelDataDto(bingStats.getImpressions(), Constants.EXCEL_CELL_FORMAT_3)));
		dataMap.put("Clicks", Arrays.asList(new ExcelDataDto(bingStats.getClicks(), Constants.EXCEL_CELL_FORMAT_3)));
		Double cpc = bingStats.getClicks() > 0 ? bingStats.getCost() / bingStats.getClicks() : 0.0;
		dataMap.put("Cost Per Click (CPC)", Arrays.asList(new ExcelDataDto(cpc, Constants.EXCEL_CELL_FORMAT_7)));
		dataMap.put("Transactions/Leads",
				Arrays.asList(new ExcelDataDto(bingStats.getConversions(), Constants.EXCEL_CELL_FORMAT_3)));
		dataMap.put("Revenue", Arrays
				.asList(new ExcelDataDto(bingStats.getRevenue(), Constants.EXCEL_CELL_FORMAT_7)));
		dataMap.put("Paid Search Spend",
				Arrays.asList(new ExcelDataDto(bingStats.getCost(), Constants.EXCEL_CELL_FORMAT_7)));
		Double roi7Day = bingStats.getCost() > 0 ? bingStats.getRevenue() / bingStats.getCost() : 0;
		dataMap.put("ROAS", Arrays.asList(new ExcelDataDto(roi7Day, Constants.EXCEL_CELL_FORMAT_2)));
		return dataMap;
	}

	private static Map<String, List<Object>> getWeeklyCostMap(List<WeeklyCostDto> weeklyCostDtos) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		dataMap.put("Date Range", Arrays.asList("Facebook", "Google", "Bing", "Weekly Totals"));
		for (WeeklyCostDto weeklyCostDto : weeklyCostDtos) {
			Object fbCost = new ExcelDataDto(weeklyCostDto.getFacebookCost(), Constants.EXCEL_CELL_FORMAT_7);
			Object googleCost = new ExcelDataDto(weeklyCostDto.getAdwordsCost(), Constants.EXCEL_CELL_FORMAT_7);
			Object bingCost = new ExcelDataDto(weeklyCostDto.getBingCost(), Constants.EXCEL_CELL_FORMAT_7);
			Object totalCost = new ExcelDataDto(weeklyCostDto.getTotalCost(), Constants.EXCEL_CELL_FORMAT_7);
			dataMap.put(weeklyCostDto.getInterval(), Arrays.asList(fbCost, googleCost, bingCost, totalCost));
		}
		return dataMap;
	}

	private static Map<String, List<Object>> getWeeklyRevenueMap(List<WeeklyRevenueDto> weeklyRevenueDtos) {
		Map<String, List<Object>> dataMap = new LinkedHashMap<String, List<Object>>();
		dataMap.put("Date Range", Arrays.asList("Weekly Revenue", "Weekly New Revenue", "30 Days GA + Bing Lookback, 28 Days FB Attribution",
				"30 Days GA Lookback", "30 Days Bing Lookback", "28 Days FB Attribution", "30 Days GA Assisted Lookback", "30 Days GA Direct Lookback",
				"30 Days Bing Assisted Lookback", "30 Days Bing Direct Lookback", "28 Days Fb View Attribution", "28 Days Fb Click Attribution"));
		for (WeeklyRevenueDto weeklyRevenueDto : weeklyRevenueDtos) {
			Object totalRevenue = new ExcelDataDto(weeklyRevenueDto.getTotalRevenue(), Constants.EXCEL_CELL_FORMAT_7);
			Object newRevenue = new ExcelDataDto(weeklyRevenueDto.getWeeklyNewRevenue(), Constants.EXCEL_CELL_FORMAT_7);
			Object totalAttributionRevenue = new ExcelDataDto(weeklyRevenueDto.getTotalAttributionRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object fb28DayTotalRevenue = new ExcelDataDto(weeklyRevenueDto.getFb28DayTotalRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object fb28DayViewRevenue = new ExcelDataDto(weeklyRevenueDto.getFb28DayViewRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object fb28DayClickRevenue = new ExcelDataDto(weeklyRevenueDto.getFb28DayClickRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object gaAssistRevenue = new ExcelDataDto(weeklyRevenueDto.getGaAssistRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object gaDirectRevenue = new ExcelDataDto(weeklyRevenueDto.getGaDirectRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object gaLookbackRevenue = new ExcelDataDto(weeklyRevenueDto.getGaAssistRevenue() + weeklyRevenueDto.getGaDirectRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object bingAssistRevenue = new ExcelDataDto(weeklyRevenueDto.getBingAssistRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object bingDirectRevenue = new ExcelDataDto(weeklyRevenueDto.getBingDirectRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			Object bingLookbackRevenue = new ExcelDataDto(weeklyRevenueDto.getBingAssistRevenue() + weeklyRevenueDto.getBingDirectRevenue(),
					Constants.EXCEL_CELL_FORMAT_7);
			dataMap.put(weeklyRevenueDto.getInterval(),
					Arrays.asList(totalRevenue, newRevenue, totalAttributionRevenue, gaLookbackRevenue, bingLookbackRevenue,
							fb28DayTotalRevenue, gaAssistRevenue, gaDirectRevenue, bingAssistRevenue, bingDirectRevenue, fb28DayViewRevenue,
							fb28DayClickRevenue));
		}
		return dataMap;
	}

	private static CellStyle createTextCellStyle(Workbook workbook) {
		CellStyle cellStyleText = workbook.createCellStyle();

		Font normalFont = workbook.createFont();
		normalFont.setFontName("Arial");
		normalFont.setFontHeightInPoints((short) 10);

		/* Set cell style cellStyleText */
		cellStyleText.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		cellStyleText.setFont(normalFont);
		cellStyleText.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyleText.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyleText.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyleText.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		return cellStyleText;
	}
}
