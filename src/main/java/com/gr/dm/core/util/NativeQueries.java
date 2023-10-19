package com.gr.dm.core.util;

public interface NativeQueries {
	String CAMPAIGN_PERFORMANCE_REPORT = "select campaignDetailSource as 'Source',"
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(name),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(name),',',-1),',',1) else "
			+ " null end  end as 'Best Campaign', "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(name),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(name),',',-1),',',1) else "
			+ " null end  end as 'Worst Campaign' ,"
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(Conversions),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(Conversions),',',-1),',',1) else "
			+ " null  end  end as 'Best Campaign Conversion Count',  "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(Conversions),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(Conversions),',',-1),',',1) else "
			+ " null end  end as 'Worst Campaign Conversion Count' , "
			+ "  case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(Revenue),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(Revenue),',',-1),',',1) else "
			+ " null end  end as 'Best Campaign Revenue',  "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(Revenue),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(Revenue),',',-1),',',1) else "
			+ " null end  end as 'Worst Campaign Revenue', "
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(Cost),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(Cost),',',-1),',',1) else "
			+ " null end  end as 'Best Campaign Cost',  "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(Cost),',',1) else  "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(Cost),',',-1),',',1) else "
			+ " null end  end as 'Worst Campaign Cost'  " + " from ( "
			+ " select campaignDetailSource, name, max(Conversions) as 'Total', Conversions, Revenue, Cost, 'best' as 'type' from ( "
			+ " select campaignDetailSource, cm.name, sum(case when 'partial' = :view  then cd.transactionCount else case when campaignDetailSource IN ('Analytics', 'Bing') then case when 'unique' = :view then (uniqueAssistedConversionCount + directConversionCount) else (cd.assistedConversionCount + cd.directConversionCount) end else cd.transactionCount end end) as 'Conversions', "
			+ " sum(case when 'partial' = :view  then cd.revenue else case when campaignDetailSource IN ('Analytics', 'Bing') then (cd.assistedConversionRevenue + cd.directConversionRevenue) else cd.revenue end end) as 'Revenue', sum(cost) as 'Cost' "
			+ " from Campaign cm, CampaignDetail cd " + " where cm.campaignId = cd.campaignId "
			+ " and cd.startDate >= :startDate and cd.endDate <= :endDate "
			+ " group by cd.campaignDetailSource, cd.campaignId "
			+ " order by campaignDetailSource, Conversions desc, Cost " + " ) tbl " + " group by campaignDetailSource "
			+ " UNION ALL "
			+ " select campaignDetailSource, name, min(Conversions) as 'Total', Conversions, Revenue, Cost, 'worst' as 'type' from ( "
			+ " select campaignDetailSource, cm.name, sum(case when 'partial' = :view  then cd.transactionCount else case when campaignDetailSource IN ('Analytics', 'Bing') then case when 'unique' = :view then (uniqueAssistedConversionCount + directConversionCount) else (cd.assistedConversionCount + cd.directConversionCount) end else cd.transactionCount end end) as 'Conversions', "
			+ " sum(case when 'partial' = :view  then cd.revenue else case when campaignDetailSource IN ('Analytics', 'Bing') then (cd.assistedConversionRevenue + cd.directConversionRevenue) else cd.revenue end end) as 'Revenue', sum(cost) as 'Cost' "
			+ " from Campaign cm, CampaignDetail cd " + " where cm.campaignId = cd.campaignId "
			+ " and cd.startDate >= :startDate and cd.endDate <= :endDate "
			+ " group by cd.campaignDetailSource, cd.campaignId "
			+ " order by campaignDetailSource, Conversions, Cost desc " + " ) tbl " + " group by campaignDetailSource "
			+ " ) tbl2  " + " group by campaignDetailSource " + " order by campaignDetailSource;";

	String COST_BY_WEEK = "select "
			+ " CONCAT(DATE_FORMAT(SUBSTRING_INDEX(group_concat(DISTINCT startDate ORDER BY startDate SEPARATOR ' '), ' ', 1), '%m/%d/%y'), '-' "
			+ " ,DATE_FORMAT(case when SUBSTRING_INDEX(group_concat(DISTINCT startDate ORDER BY startDate SEPARATOR ' '), ' ', 1) = '2017-12-26' then DATE_ADD(SUBSTRING_INDEX(group_concat(DISTINCT startDate ORDER BY startDate SEPARATOR ' '), ' ', 1), "
			+ " INTERVAL 5 DAY) else DATE_ADD(SUBSTRING_INDEX(group_concat(DISTINCT startDate ORDER BY startDate SEPARATOR ' '), ' ', 1), INTERVAL 6 DAY) end, '%m/%d/%y')) as `interval`, "
			+ " sum(case when campaignDetailSource = 'Facebook' then cost else 0 end) as facebook_cost,"
			+ " sum(case when campaignDetailSource = 'Adwords' then cost else 0 end) as adwords_cost,"
			+ " sum(case when campaignDetailSource = 'Bing' then cost else 0 end) as bing_cost,"
			+ " sum(case when campaignDetailSource IN ('Adwords', 'Facebook', 'Bing') then cost else 0 end) as total_cost"
			+ " from CampaignDetail where startDate >= :startDate and endDate <= :endDate"
			+ " group by yearweek(startDate, 1)" + " order by startDate;";

	String REVENUE_BY_WEEK = "select CONCAT(DATE_FORMAT(startDate, '%m/%d/%y'), '-' ,DATE_FORMAT(case when startDate = '2017-12-26' then DATE_ADD(startDate, INTERVAL 5 DAY) else DATE_ADD(startDate, INTERVAL 6 DAY) end, '%m/%d/%y')) as `interval`, " + 
			" sum(ga_bing_unique_assist_revenue + ga_direct_revenue + bing_direct_revenue + fb_revenue) as total_revenue, "
			+ " weekly_new_revenue, "
			+ " sum(ga_assist_revenue + bing_assist_revenue + ga_direct_revenue + bing_direct_revenue + fb_28_day_view_revenue + fb_28_day_click_revenue) as 28_day_attribution,"
			+ " sum(ga_assist_revenue + bing_assist_revenue + ga_direct_revenue + bing_direct_revenue) as 28_day_ga_bing_revenue,"
			+ " sum(fb_28_day_view_revenue + fb_28_day_click_revenue) as 28_day_fb_attribution,"
			+ " sum(ga_assist_revenue + bing_assist_revenue) AS ga_bing_assist_revenue, "
			+ " sum(ga_direct_revenue + bing_direct_revenue) AS ga_bing_direct_revenue, "
			+ " fb_28_day_view_revenue, fb_28_day_click_revenue,"
			+ "  ga_assist_revenue, ga_direct_revenue, bing_assist_revenue, bing_direct_revenue "
			+ " from ( select SUBSTRING_INDEX(group_concat(DISTINCT startDate ORDER BY startDate SEPARATOR ' '), ' ', 1) as startDate, yearweek(startDate, 1) as week1,"
			+ " (select fbRevenue from( select sum(revenue1DayView + revenue28DayClick) as fbRevenue, yearweek(startDate, 1) as week2 from "
			+ " CampaignAttribution ca inner join Campaign cm on cm.campaignId = ca.campaignId "
			+ " where cm.campaignSource = 'Facebook' and ca.isManual = 0"
			+ " group by yearweek(startDate, 1)) tbl where week2 = week1) as fb_revenue,"
			+ " (select fbRevenue from( select sum(revenue28DayView) as fbRevenue, yearweek(startDate, 1) as week2 from "
			+ " CampaignAttribution ca inner join Campaign cm on cm.campaignId = ca.campaignId "
			+ " where cm.campaignSource = 'Facebook' and ca.isManual = 0"
			+ " group by yearweek(startDate, 1)) tbl where week2 = week1) as fb_28_day_view_revenue,"
			+ " (select fbRevenue from( select sum(revenue28DayClick) as fbRevenue, yearweek(startDate, 1) as week2 from "
			+ " CampaignAttribution ca inner join Campaign cm on cm.campaignId = ca.campaignId "
			+ " where cm.campaignSource = 'Facebook' and ca.isManual = 0"
			+ " group by yearweek(startDate, 1)) tbl where week2 = week1) as fb_28_day_click_revenue,"
			+ " sum(case when ct.isDefaultTransaction = 1 and isNewMembership = 1 and transactionSource in ('Analytics', 'Bing', 'Facebook') and transactionSource = case when transactionSource = 'Facebook' then 'Facebook' else createdFrom end  then transactionRevenue else 0 end) as weekly_new_revenue,"
			+ " sum(CASE WHEN transactionSource = 'Analytics' AND createdFrom = 'Analytics' AND isDefaultAssist = 1 THEN transactionRevenue ELSE 0 END) AS ga_assist_revenue,"
			+ " sum(CASE WHEN transactionSource = 'Bing' AND createdFrom = 'Bing' AND isDefaultAssist = 1 THEN transactionRevenue ELSE 0 END) AS bing_assist_revenue,"
			+ " sum(CASE WHEN transactionSource = 'Analytics' AND createdFrom = 'Analytics' AND isDirect = 1 THEN transactionRevenue ELSE 0 END) AS ga_direct_revenue,"
			+ " sum(CASE WHEN transactionSource = 'Bing' AND createdFrom = 'Bing' AND isDirect = 1 THEN transactionRevenue ELSE 0 END) AS bing_direct_revenue,"
			+ " sum(case when isDefaultAssist = 1 and isDirect = 0 and isDefaultTransaction = 1 and transactionId not in" 
			+ " (select transactionId from CampaignTransaction where transactionId = ct.transactionId and createdFrom = case when transactionSource = 'Facebook' then createdFrom else transactionSource end and isDirect = 1) then transactionRevenue else 0 end) as ga_bing_unique_assist_revenue"
			+ " from CampaignTransaction ct where startDate >= :startDate and endDate <= :endDate "
			+ " group by yearweek(startDate, 1) order by startDate ) tbl2 group by `interval`"
			+ " order by startDate;";

	String KEYWORD_PERFORMANCE_REPORT = " select adGroupSource as 'Source', "
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(name),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(name),',',-1),',',1) else"
			+ " null end  end as 'Best Keyword', "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(name),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(name),',',-1),',',1) else"
			+ " null end  end as 'Worst Keyword' ,"
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(Conversions),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(Conversions),',',-1),',',1) else"
			+ " null end  end as 'Best Keyword Conversion Count', "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(Conversions),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(Conversions),',',-1),',',1) else"
			+ " null end  end as 'Worst Keyword Conversion Count' ,"
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(Revenue),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(Revenue),',',-1),',',1) else"
			+ " null end  end as 'Best Keyword Revenue', "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(Revenue),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(Revenue),',',-1),',',1) else"
			+ " null end  end as 'Worst Keyword Revenue',"
			+ " case when substring_index(group_concat(type),',',1) = 'best' then substring_index(group_concat(Cost),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'best' then substring_index(substring_index(group_concat(Cost),',',-1),',',1) else"
			+ " null end  end as 'Best Keyword Cost', "
			+ " case when substring_index(group_concat(type),',',1) = 'worst' then substring_index(group_concat(Cost),',',1) else "
			+ " case when substring_index(substring_index(group_concat(type),',',-1),',',1) = 'worst' then substring_index(substring_index(group_concat(Cost),',',-1),',',1) else"
			+ " null end  end as 'Worst Keyword Cost' " + " from ("
			+ " select adGroupSource, name, max(Conversions) as 'Total', Conversions, Revenue, Cost, 'best' as 'type' from ("
			+ " select ad.adGroupSource, kw.name, sum(kd.transactionCount) as 'Conversions', sum(kd.revenue) as 'Revenue', sum(cost) as 'Cost'"
			+ " from AdGroup ad, Keyword kw, KeywordDetail kd"
			+ " where kw.adGroupId = ad.adGroupId and kw.keywordId = kd.keywordId"
			+ " and kd.startDate >= :startDate and kd.endDate <= :endDate" + " group by ad.adGroupSource, kw.keywordId"
			+ " order by ad.adGroupSource, Conversions desc, Cost" + " ) tbl" + " group by adGroupSource" + " UNION ALL"
			+ " select adGroupSource, name, min(Conversions) as 'Total', Conversions, Revenue, Cost, 'worst' as 'type' from ("
			+ " select ad.adGroupSource, kw.name, sum(kd.transactionCount) as 'Conversions', sum(kd.revenue) as 'Revenue', sum(cost) as 'Cost'"
			+ " from AdGroup ad, Keyword kw, KeywordDetail kd"
			+ " where kw.adGroupId = ad.adGroupId and kw.keywordId = kd.keywordId"
			+ " and kd.startDate >= :startDate and kd.endDate <= :endDate" + " group by ad.adGroupSource, kw.keywordId"
			+ " order by ad.adGroupSource, Conversions, Cost desc" + " ) tbl" + " group by adGroupSource" + " )"
			+ " tbl2" + " group by adGroupSource" + " order by adGroupSource";
	
	String DUPLICATE_TRANSACTIONS = "select transactionId, memberId, transactionRevenue, packageCode, isNewMembership, isRenewedMembership, hasTI, hasMedicalDevice, serverDate,"
			+ " (select transactionSource from CampaignTransaction where transactionId = ct.transactionId and isDirect = 1) as directSource,"
			+ " group_concat(case when isAssisted = 1 then transactionSource end) as assistSource"
			+ " from CampaignTransaction ct"
			+ " where createdFrom IN ('Analytics', 'Bing')"
			+ " and startDate >= :startDate and endDate <= :endDate"
			+ " and (isDirect = 1 and isAssisted = 1)"
			+ " or (isDirect = 0 and isAssisted = 1 and transactionId IN (select transactionId from CampaignTransaction where isDirect = 1 and createdFrom IN ('Bing', 'Analytics')))"
			+ " group by transactionId"
			+ " order by serverDate desc;";
	
	String CAMPAIGN_SUMMARY = "select cd.campaignDetailSource as `source`, round(sum(cd.cost), 2) as `cost`, "
			+ " round(coalesce(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and campaignDetailSource = 'Facebook' then "
			+ " (select sum(revenue1DayView + revenue7DayClick) from CampaignAttribution where startDate = cd.startDate and endDate = cd.endDate and isManual = false and campaignId = cd.campaignId) "
			+ " else cd.revenue end), 0), 2) as `revenue` , coalesce(round(sum(cd.cost) / sum(cd.clicks), 2), 0) as `cpc`,"
			+ " sum(cd.clicks) as `clicks`, coalesce(round(sum(cd.revenue) / sum(cd.cost), 2), 0) as `roi`, sum(cd.impressions) as `impressions`, count(distinct cd.campaignId ) as `total_campaigns`, "
			+ " coalesce(sum(case when :statType IN ('purchase_centric', 'weekly_stats') and campaignDetailSource = 'Facebook' then "
			+ " (select sum(purchases1DayView + purchases7DayClick) from CampaignAttribution where startDate = cd.startDate and endDate = cd.endDate and isManual = false and campaignId = cd.campaignId) "
			+ " else cd.transactionCount end), 0) as `conversions`,"
			+ " (select coalesce(sum(case when ct.isNewMembership = 1 then 1 else 0 end), 0) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource "
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `new_memberships`, "
			+ " (select coalesce(sum(case when ct.isRenewedMembership = 1 then 1 else 0 end), 0) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource "
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `renewed_memberships`, "
			+ " (select coalesce(sum(case when ct.isNewMembership = 0  and ct.isRenewedMembership = 0 and ct.hasTI = 1 then 1 else 0 end), 0) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource "
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `ti_count`, "
			+ " (select coalesce(sum(case when ct.hasMedicalDevice = 1 then ct.deviceCount else 0 end), 0) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource"
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `device_count`,"
			+ " (select coalesce(sum(case when ct.isNewMembership = 1 then ct.transactionRevenue else 0 end), 0.00) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource "
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `new_revenue`,"
			+ " (select coalesce(sum(case when ct.isRenewedMembership = 1 then ct.transactionRevenue else 0 end), 0.00) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource"
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `renew_revenue`,"
			+ " (select coalesce(sum(case when ct.isNewMembership = 0  and ct.isRenewedMembership = 0 and ct.hasTI = 1 then ct.transactionRevenue else 0 end), 0.00) from CampaignTransaction ct where ct.transactionSource = cd.campaignDetailSource "
			+ " and ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = case when ct.transactionSource = 'Facebook' then 'Facebook' else ct.createdFrom end and isDefaultTransaction = 1) as `ti_revenue`,"
			+ " coalesce(sum(case when cd.campaignDetailSource IN ('Analytics', 'Bing') then (cd.assistedConversionCount + cd.directConversionCount) else case when :statType IN ('purchase_centric', 'weekly_stats') and campaignDetailSource = 'Facebook' then "
			+ " (select sum(purchases1DayView + purchases28DayClick) from CampaignAttribution where startDate = cd.startDate and endDate = cd.endDate and isManual = false and campaignId = cd.campaignId) "
			+ " else cd.transactionCount end end), 0) as `assisted_conversions`, "
			+ " coalesce(sum(case when cd.campaignDetailSource IN ('Analytics', 'Bing') then (cd.assistedConversionRevenue + cd.directConversionRevenue) else case when :statType IN ('purchase_centric', 'weekly_stats') and campaignDetailSource = 'Facebook' then "
			+ " (select sum(revenue1DayView + revenue28DayClick) from CampaignAttribution where startDate = cd.startDate and endDate = cd.endDate and isManual = false and campaignId = cd.campaignId) "
			+ " else cd.revenue end end), 0) as `assisted_revenue`, "
			+ " coalesce(sum(case when cd.campaignDetailSource IN ('Analytics', 'Bing') then (cd.uniqueAssistedConversionCount + cd.directConversionCount) else case when :statType IN ('purchase_centric', 'weekly_stats') and campaignDetailSource = 'Facebook' then "
			+ " (select sum(purchases1DayView + purchases28DayClick) from CampaignAttribution where startDate = cd.startDate and endDate = cd.endDate and isManual = false and campaignId = cd.campaignId) "
			+ " else cd.transactionCount end end), 0) as `unique_assisted_conversions`, "
			+ " coalesce(sum(case when cd.campaignDetailSource IN ('Analytics', 'Bing') then (cd.uniqueAssistedConversionRevenue + cd.directConversionRevenue) else case when :statType IN ('purchase_centric', 'weekly_stats') and campaignDetailSource = 'Facebook' then "
			+ " (select sum(revenue1DayView + revenue28DayClick) from CampaignAttribution where startDate = cd.startDate and endDate = cd.endDate and isManual = false and campaignId = cd.campaignId) "
			+ " else cd.revenue end end), 0) as `unique_assisted_revenue` " 
			+ " from CampaignDetail cd "
			+ " where cd.startDate >= :startDate and cd.endDate <= :endDate" 
			+ " group by cd.campaignDetailSource";
	
	String CAMPAIGN_SUMMARY_BY_CLICK = "select "
			+ " case when campaignDetailSource = 'Adwords' then 'Analytics' else campaignDetailSource end as `source`, "
			+ " sum(cost) as `cost`, sum(revenue) as `revenue`,  "
			+ " coalesce(sum(cost) / sum(clicks), 0) as `cpc`, coalesce(sum(clicks)) as `clicks`,"
			+ " coalesce(sum(revenue) / sum(cost), 0) as `roi`, sum(impressions) as  `impressions`,"
			+ " count(distinct campaignId) as `total_campaigns`, sum(transactionCount) as `conversions`,"
			+ " (select coalesce(sum(case when isNewMembership = 1 then 1 else 0 end), 0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `new_memberships`,"
			+ " (select coalesce(sum(case when isRenewedMembership = 1 then 1 else 0 end), 0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `renewed_memberships`,"
			+ " (select coalesce(sum(case when isNewMembership = 0 and isRenewedMembership = 0 and hasTI = 1 then 1 else 0 end), 0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `ti_count`,"
			+ " (select coalesce(sum(case when hasMedicalDevice = 1 then deviceCount else 0 end), 0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `device_count`,"
			+ " (select coalesce(sum(case when isNewMembership = 1 then transactionRevenue else 0 end), 0.0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `new_revenue`,"
			+ " (select coalesce(sum(case when isRenewedMembership = 1 then transactionRevenue else 0 end), 0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `renew_revenue`,"
			+ " (select coalesce(sum(case when isNewMembership = 0 and isRenewedMembership = 0 and hasTI = 1 then transactionRevenue else 0 end), 0.0) from CampaignTransaction ct "
			+ " where ct.startDate >= :startDate and ct.endDate <= :endDate and ct.transactionSource = campaignDetailSource "
			+ " and createdFrom = case when transactionSource = 'Email' then 'Email' else 'SS' end) as `ti_revenue`,"
			+ " sum(transactionCount) as `assisted_conversions`, sum(revenue) as `assisted_revenue`,"
			+ " sum(transactionCount) as `unique_assisted_conversions`, sum(revenue) as `unique_assisted_revenue`"
			+ " from CampaignDetail where startDate >= :startDate and endDate <= :endDate"
			+ " and campaignDetailSource != 'Analytics' "
			+ " group by campaignDetailSource";
}
