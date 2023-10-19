package com.gr.dm.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.gr.dm.core.entity.CampaignAttribution;

import au.com.bytecode.opencsv.CSVReader;

public class Util {
	
	public static boolean isNullOrEmpty(String string) {
        return (((string == null) || (string.trim().length() == 0)) ? true : false);
    }
    
    public static boolean isNull(Object object) {
        return object == null;
    }
    
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }
    
    public static String convertFileToString(File file) throws IOException {
    	FileInputStream fis = null;
        fis = new FileInputStream(file);
    	byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String str = new String(data, "UTF-8");
        str = str.replaceAll("\"", "");
        return str;
    }
	
	public static CSVReader getCSVReader(File file) throws FileNotFoundException {
		return new CSVReader(new FileReader(file));
	}

	public static CSVReader getCSVReader(String response) {
		return new CSVReader(new StringReader(response));
	}
	
	public static boolean calculateAttributionData(Long attributionDays, CampaignAttribution campaignAttribution, Double revenue, Integer transactionCount) {
		if (attributionDays <= 1) {
			campaignAttribution.setPurchases1DayClick(campaignAttribution.getPurchases1DayClick() + transactionCount);
			campaignAttribution.setPurchases7DayClick(campaignAttribution.getPurchases7DayClick() + transactionCount);
			campaignAttribution.setPurchases28DayClick(campaignAttribution.getPurchases28DayClick() + transactionCount);
			campaignAttribution.setPurchasesGreaterThan28DayClick(campaignAttribution.getPurchasesGreaterThan28DayClick() + transactionCount);
			campaignAttribution.setRevenue1DayClick(campaignAttribution.getRevenue1DayClick() + revenue);
			campaignAttribution.setRevenue7DayClick(campaignAttribution.getRevenue7DayClick() + revenue);
			campaignAttribution.setRevenue28DayClick(campaignAttribution.getRevenue28DayClick() + revenue);
			campaignAttribution.setRevenueGreaterThan28DayClick(campaignAttribution.getRevenueGreaterThan28DayClick() + revenue);
		} else if (attributionDays >= 2 && attributionDays <= 7) {
			campaignAttribution.setPurchases7DayClick(campaignAttribution.getPurchases7DayClick() + transactionCount);
			campaignAttribution.setPurchases28DayClick(campaignAttribution.getPurchases28DayClick() + transactionCount);
			campaignAttribution.setPurchasesGreaterThan28DayClick(campaignAttribution.getPurchasesGreaterThan28DayClick() + transactionCount);
			campaignAttribution.setRevenue7DayClick(campaignAttribution.getRevenue7DayClick() + revenue);
			campaignAttribution.setRevenue28DayClick(campaignAttribution.getRevenue28DayClick() + revenue);
			campaignAttribution.setRevenueGreaterThan28DayClick(campaignAttribution.getRevenueGreaterThan28DayClick() + revenue);
		} else if (attributionDays >= 8 && attributionDays <= 28) {
			campaignAttribution.setPurchases28DayClick(campaignAttribution.getPurchases28DayClick() + transactionCount);
			campaignAttribution.setPurchasesGreaterThan28DayClick(campaignAttribution.getPurchasesGreaterThan28DayClick() + transactionCount);
			campaignAttribution.setRevenue28DayClick(campaignAttribution.getRevenue28DayClick() + revenue);
			campaignAttribution.setRevenueGreaterThan28DayClick(campaignAttribution.getRevenueGreaterThan28DayClick() + revenue);
		} else {
			campaignAttribution.setPurchasesGreaterThan28DayClick(campaignAttribution.getPurchasesGreaterThan28DayClick() + transactionCount);
			campaignAttribution.setRevenueGreaterThan28DayClick(campaignAttribution.getRevenueGreaterThan28DayClick() + revenue);
		}
		
		return true;
	}
	
	public static List<String> generateYearArray(Date fromDate, Date toDate) {
        List<String> yearArray = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        int fromYear = calendar.get(Calendar.YEAR);
        calendar.setTime(toDate);
        int toYear = calendar.get(Calendar.YEAR);
        for (int year = fromYear; year <= toYear; year++) {
            yearArray.add(String.valueOf(year));
        }
        return yearArray;
    }

    public static List<String> generateWeekArray(Date fromDate, Date toDate) {
        List<String> weekArray = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        Calendar startDateCal = Calendar.getInstance(Locale.GERMANY);
        startDateCal.setTime(fromDate);
        calendar.setTime(fromDate);
        Calendar endCalendar = Calendar.getInstance(Locale.GERMANY);
        endCalendar.setTime(toDate);
        SimpleDateFormat weekFormat = new SimpleDateFormat("MM/dd/yyyy");
        calendar.add(Calendar.DATE, (calendar.getFirstDayOfWeek() - calendar.get(Calendar.DAY_OF_WEEK) - 7) % 7);
        int i = 0;
        while (calendar.compareTo(endCalendar) <= 0) {
            Calendar weekStart = (Calendar) calendar.clone();
            if (i == 0 && weekStart.compareTo(startDateCal) <= 0) {
            	weekStart = startDateCal;
            }
            calendar.add(Calendar.DATE, 6);
            Calendar weekEnd = (Calendar) calendar.clone();
            calendar.add(Calendar.DATE, 1);
	        StringBuilder weekBuilder = new StringBuilder();
	        weekBuilder.append(weekFormat.format(weekStart.getTime()));
	        weekBuilder.append("-");
	        weekBuilder.append(weekFormat.format(weekEnd.getTime()));
	        weekArray.add(weekBuilder.toString());
	        i++;
        }
        
        String endWeekRangeStart = weekArray.get(weekArray.size() - 1).split("-")[0];
        String endWeekRangeEnd = weekArray.get(weekArray.size() - 1).split("-")[1];
        if (!weekFormat.format(endCalendar.getTime()).equals(endWeekRangeEnd)) {
        	weekArray.remove(weekArray.size() - 1);
        	StringBuilder weekBuilder = new StringBuilder();
	        weekBuilder.append(endWeekRangeStart);
	        weekBuilder.append("-");
	        weekBuilder.append(weekFormat.format(endCalendar.getTime()));
        	weekArray.add(weekBuilder.toString());
        }
//        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
//            StringBuilder weekBuilder = new StringBuilder();
//            weekBuilder.append(weekFormat.format(calendar.getTime()));
//            weekBuilder.append("-");
//            calendar.add(Calendar.DATE, (calendar.getFirstDayOfWeek() - calendar.get(Calendar.DAY_OF_WEEK) - 7) % 7);
//            weekBuilder.append(weekFormat.format(calendar.getTime()));
//            weekArray.add(weekBuilder.toString());
//            calendar.add(Calendar.DATE, 1);
//        }
//        weekFormat.format(endCalendar.getTime())
//        weekArray.get(23)
//        "06/12/2023-06/18/2023".indexOf()
        return weekArray;
    }

 

    public static List<String> generateMonthArray(Date fromDate, Date toDate) {
        List<String> monthArray = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(fromDate);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(toDate);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        while (startDate.before(endDate) || startDate.equals(endDate)) {
            String month = monthFormat.format(startDate.getTime());
            monthArray.add(month);
            startDate.add(Calendar.MONTH, 1);
        }
       return monthArray;
    }
}
