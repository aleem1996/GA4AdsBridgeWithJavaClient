package com.gr.dm.core.util;

import java.util.Arrays;
import java.util.List;

public interface Constants {

	String GA_ADWORDS_CAMPAIGN_ID = "ga:adwordsCampaignID";
	String GA_CAMPAIGN = "ga:campaign";
	String GA_AD_COST = "ga:adCost";
	String GA_TRANSACTION_REVENUE = "ga:transactionRevenue";
	String GA_TRANSACTIONS = "ga:transactions";
	String GA_ITEM_REVENUE = "ga:itemRevenue";
	String GA_COUNTRY = "ga:country";
	String GA_SOURCE = "ga:source";
	String GA_TRANSACTION_ID = "ga:transactionId";
	String GA_BROWSER = "ga:browser";
	String GA_DEVICE_CATEGORY = "ga:deviceCategory";
	String GA_PRODUCT_CATEGORY = "ga:productCategory";
	String GA_PRODUCT_SKU = "ga:productSku";
	String GA_PRODUCT_NAME = "ga:productName";
	String GA_ITEM_QUANTITY = "ga:itemQuantity";
	String GA_KEYWORD = "ga:keyword";
	String GA_DATE_HOUR_MINIUTE = "ga:dateHourMinute";
	String GA_AD_CLICKS = "ga:adClicks";
	String GA_IMPRESSIONS = "ga:impressions";
	String GA_SOURCE_MEDIUM = "ga:sourceMedium";
	String GA_SESSIONS = "ga:sessions";
	String GA_PERCENT_NEW_SESSIONS = "ga:percentNewSessions";
	String GA_NEW_USERS = "ga:newUsers";
	String GA_BOUNCE_RATE = "ga:bounceRate";
	String GA_PAGE_VIEWS = "ga:pageViews";
	String GA_AVG_TIME_ON_PAGE = "ga:avgTimeOnPage";
	String GA_UNIQUE_PAGE_VIEWS = "ga:uniquePageViews";
	String GA_PAGE_VIEWS_PER_SESSION = "ga:pageViewsPerSession";
	String GA_AVG_SESSION_DURATION = "ga:avgSessionDuration";
	String GA_PAGE_PATH = "ga:pagePath";
	String GA_LANDING_PAGE_PATH = "ga:landingPagePath";
	String GA_TOTAL_EVENTS = "ga:totalEvents";
	String GA_EVENT_CATEGORY = "ga:eventCategory";
	String GA_EVENT_ACTION = "ga:eventAction";
	String GA_EVENT_LABEL = "ga:eventLabel";

	String TRANSACTION_SHIPMENT = "Shipment";
	String DATA_SPLITTER = "__";
	String GA_MCF_ASSISTED_CONVERSIONS = "mcf:assistedConversions";
	String GA_MCF_LAST_INTERACTION_CONVERSIONS = "mcf:lastInteractionConversions";
	String GA_MCF_CAMPAIGN_NAME = "mcf:campaignName";
	String GA_MCF_SOURCE_MEDIUM = "mcf:sourceMedium";
	String GA_MCF_CONVERSION_TYPE = "mcf:conversionType";
	String GA_MCF_TRANSACTION_ID = "mcf:transactionId";
	String GA_MCF_BASIC_CHANNEL_GROUPING = "mcf:basicChannelGrouping";

	String TRANSACTION_NEW_MEMBERSHIP = "NewMem";
	String TRANSACTION_RENEW_MEMBERSHIP = "RenewMem";
	String TRANSACTION_TRAVEL_INSURANCE = "TI";
	String TRANSACTION_MEDICAL_DEVICE = "MD";

	String FB_CAMPAIGN_ID = "campaign_id";
	String FB_CAMPAIGN_NAME = "campaign_name";
	String FB_SPEND = "spend";
	String FB_FIELD_ACTIONS = "actions";
	String FB_PIXEL_PURCHASE_EVENT = "offsite_conversion.fb_pixel_purchase";
	String FB_CLICKS = "inline_link_clicks";
	String FB_IMPRESSIONS = "impressions";
	String FB_ADSET_ID = "adset_id";
	String FB_ADSET_NAME = "adset_name";
	String FB_AD_ID = "ad_id";
	String FB_AD_NAME = "ad_name";
	String FB_IMAGE_URL = "image_url";
	String FB_ACTION_VALUES = "action_values";
	String FB_ACTION_ATTRIBUTION_VALUES = "1d_view, 1d_click, 7d_view, 7d_click, 28d_view, 28d_click";
	String FB_ACTION_ATTRIBUTION = "action_attribution_windows";
	String FB_PURCHASES = "purchases";
	String FB_PURCHASES_7_DAY = "purchases7Day";
	String FB_PURCHASES_28_DAY = "purchases28Day";

	// Adwards
	List<String> ADWORDS_CAMPAIGN_REPORT_COLUMNS = Arrays.asList("CampaignName", "CampaignId", "Conversions", "ConversionValue", "Cost", "Impressions", "Clicks", "CampaignStatus");
	List<String> ADWORDS_ADGROUP_REPORT_COLUMNS = Arrays.asList("CampaignId", "AdGroupId", "AdGroupName", "Conversions", "ConversionValue", "Cost", "Impressions", "Clicks");
	List<String> ADWORDS_AD_REPORT_COLUMNS = Arrays.asList("Id", "AdGroupId", "HeadlinePart1", "HeadlinePart2", "Conversions", "ConversionValue", "Cost", "Impressions", "Clicks");
	List<String> ADWORDS_KEYWORD_REPORT_COLUMNS = Arrays.asList("Id", "AdGroupId", "Criteria", "Conversions", "ConversionValue", "Cost", "Impressions", "Clicks");

	// GoogleAds
	String GOOGLEADS_CAMPAIGN_REPORT_COLUMNS = "campaign.name,campaign.id,metrics.conversions,metrics.conversions_value,metrics.cost_micros,metrics.impressions,metrics.clicks,campaign.status";
	String GOOGLEADS_ADGROUP_REPORT_COLUMNS = "campaign.id, ad_group.id, ad_group.name, metrics.conversions, metrics.conversions_value, metrics.cost_micros, metrics.impressions, metrics.clicks";
	String GOOGLEADS_AD_REPORT_COLUMNS = "ad_group_ad.ad.id, ad_group.id, ad_group_ad.ad.expanded_text_ad.headline_part1, ad_group_ad.ad.expanded_text_ad.headline_part2, metrics.conversions, metrics.conversions_value, metrics.cost_micros, metrics.impressions, metrics.clicks";
	String GOOGLEADS_KEYWORD_REPORT_COLUMNS = "ad_group_criterion.criterion_id, ad_group.id, ad_group_criterion.keyword.text, metrics.conversions, metrics.conversions_value, metrics.cost_micros, metrics.impressions, metrics.clicks";
	String GOOGLEADS_ADTYPES = "APP_AD, APP_ENGAGEMENT_AD, APP_PRE_REGISTRATION_AD, CALL_AD, DISCOVERY_CAROUSEL_AD, DISCOVERY_MULTI_ASSET_AD, DYNAMIC_HTML5_AD, EXPANDED_DYNAMIC_SEARCH_AD, EXPANDED_TEXT_AD, HOTEL_AD, HTML5_UPLOAD_AD, IMAGE_AD, IN_FEED_VIDEO_AD, LEGACY_APP_INSTALL_AD, LEGACY_RESPONSIVE_DISPLAY_AD, LOCAL_AD, RESPONSIVE_DISPLAY_AD, RESPONSIVE_SEARCH_AD, SHOPPING_COMPARISON_LISTING_AD, SHOPPING_PRODUCT_AD, SHOPPING_SMART_AD, SMART_CAMPAIGN_AD, TEXT_AD, UNKNOWN, VIDEO_AD, VIDEO_BUMPER_AD, VIDEO_NON_SKIPPABLE_IN_STREAM_AD, VIDEO_OUTSTREAM_AD, VIDEO_RESPONSIVE_AD, VIDEO_TRUEVIEW_IN_STREAM_AD";

	String HEADER_X_AUTHORIZATION = "X_AUTHORIZATION";
	// Device headers
	String HEADER_X_DEVICE_APP_NAME = "X_APPNAME";
	String HEADER_X_DEVICE_APP_VERSION = "X_APP_VERSION";
	String HEADER_X_DEVICE_IMEI = "X_IMEI";
	String HEADER_X_DEVICE_TOKEN = "X_DEVICE_TOKEN";
	String HEADER_X_DEVICE_MODEL = "X_MODEL";
	String HEADER_X_DEVICE_TYPE = "X_DEVICE_TYPE";
	String HEADER_X_LOGIN_CONTACT_GUID = "X_LOGIN_CONTACT_GUID";
	String HEADER_X_SILENT_LOGIN = "X_SILENT_LOGIN";
	String HEADER_X_REQUEST_ID = "X_REQUEST_ID";

	String HEADER_CACHE_CONTROL = "Cache-Control";
	String HEADER_PRAGMA = "Pragma";
	String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
	String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	String HEADER_X_ACCOUNT = "X_ACCOUNT";

	String HEADER_X_AUTH_TOKEN = "X_AUTH_TOKEN";
	String HEADER_X_AUTH_TOKEN_PREFIX = "Bearer ";

	// Request info headers
	String HEADER_X_SOURCE = "X_SOURCE";
	String HEADER_X_REQUEST_CODE = "X_REQUEST_CODE";

	String JSON_KEY_SOURCE = "source";
	String JSON_KEY_REQUEST_CODE = "requestCode";
	String JSON_KEY_REQUEST_APP_KEY = "requestingAppKey";

	String DATE_FORMAT = "MM-dd-yyyy";
	String DEFAULT_STAT_TYPE = "attribution";
	String FULL_VIEW = "full";
	String PARTIAL_VIEW = "partial";
	String DEFAULT_VIEW_TYPE = PARTIAL_VIEW;

	Integer THREAD_CORE_POOL_SIZE = 2;
	Integer THREAD_POOL_MAX_SIZE = 2;
	Integer THREAD_QUEUE_CAPACITY = 10;
	Integer THREAD_POOL_SIZE = 5;

	String REQUESTING_APP_KEY = "GRD-WEBSITE-KUJX8wEbT0sZwZsitE5MAGRD";

	// "General"
	Integer EXCEL_CELL_FORMAT_0 = 0;
	// "0"
	Integer EXCEL_CELL_FORMAT_1 = 1;
	// "0.00"
	Integer EXCEL_CELL_FORMAT_2 = 2;
	// "#,##0"
	Integer EXCEL_CELL_FORMAT_3 = 3;
	// "#,##0.00"
	Integer EXCEL_CELL_FORMAT_4 = 4;
	// "\"$\"#,##0_);(\"$\"#,##0)"
	Integer EXCEL_CELL_FORMAT_5 = 5;
	// "\"$\"#,##0_);[Red](\"$\"#,##0)"
	Integer EXCEL_CELL_FORMAT_6 = 6;
	// "\"$\"#,##0.00_);(\"$\"#,##0.00)"
	Integer EXCEL_CELL_FORMAT_7 = 7;
	// "\"$\"#,##0.00_);[Red](\"$\"#,##0.00)"
	Integer EXCEL_CELL_FORMAT_8 = 8;
	// "0%"
	Integer EXCEL_CELL_FORMAT_9 = 9;
	// "0.00%"
	Integer EXCEL_CELL_FORMAT_0xa = 0xa;
	// "0.00E+00"
	Integer EXCEL_CELL_FORMAT_0xb = 0xb;
	// "# ?/?"
	Integer EXCEL_CELL_FORMAT_0xc = 0xc;
	// "# ??/??"
	Integer EXCEL_CELL_FORMAT_0xd = 0xd;
	// "m/d/yy"
	Integer EXCEL_CELL_FORMAT_0xe = 0xe;
	// "d-mmm-yy"
	Integer EXCEL_CELL_FORMAT_0xf = 0xf;
	// "d-mmm"
	Integer EXCEL_CELL_FORMAT_0x10 = 0x10;
	// "mmm-yy"
	Integer EXCEL_CELL_FORMAT_0x11 = 0x11;
	// "h:mm AM/PM"
	Integer EXCEL_CELL_FORMAT_0x12 = 0x12;
	// "h:mm:ss AM/PM"
	Integer EXCEL_CELL_FORMAT_0x13 = 0x13;
	// "h:mm"
	Integer EXCEL_CELL_FORMAT_0x14 = 0x14;
	// "h:mm:ss"
	Integer EXCEL_CELL_FORMAT_0x15 = 0x15;
	// "m/d/yy h:mm"
	Integer EXCEL_CELL_FORMAT_0x16 = 0x16;
	
	
}
