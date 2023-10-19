package com.gr.dm.core.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date getDate(String date) {
		LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
		Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public static String getYesterdayDate() {

		LocalDate now = LocalDate.now().minusDays(1);
		return now.format(DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public static String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	public static Date addDaysToDate(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		date = cal.getTime();
		return date;
	}

	public static Long getDaysBetween(Date startDate, Date endDate) {
		LocalDate date1 = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate date2 = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return ChronoUnit.DAYS.between(date1, date2);
	}

	public static Date getMidDate(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();
		Date midDate = new Date(startDate.getTime() + (diff / 2));
		return DateUtil.trimDate(midDate);
	}

	public static Date trimDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date addCurrentTimeToDate(Date date) {
		Calendar now = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, now.get(Calendar.SECOND));
		return cal.getTime();
	}

	public static com.microsoft.bingads.v13.reporting.Date getBingDate(Date date) {
		com.microsoft.bingads.v13.reporting.Date bingDate = new com.microsoft.bingads.v13.reporting.Date();
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		bingDate.setDay(localDate.getDayOfMonth());
		bingDate.setMonth(localDate.getMonthValue());
		bingDate.setYear(localDate.getYear());
		return bingDate;
	}

}
