/**
 * 
 */
package com.gr.dm.core.adapter.gws;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gr.dm.core.adapter.Adapter;
import com.gr.dm.core.dto.LoginUser;
import com.gr.dm.core.dto.crm.CrmCampaignCodeResponseDetail;
import com.gr.dm.core.dto.crm.CrmRequestPayload;
import com.gr.dm.core.dto.crm.CrmResponse;
import com.gr.dm.core.dto.crm.CrmTransactionCount;
import com.gr.dm.core.dto.crm.CrmTransactionCountResponseDetail;
import com.gr.dm.core.dto.crm.CrmTransactionResponseDetail;
import com.gr.dm.core.entity.Campaign;
import com.gr.dm.core.entity.CampaignAttribution;
import com.gr.dm.core.entity.CampaignDetail;
import com.gr.dm.core.entity.CampaignTransaction;
import com.gr.dm.core.entity.TransactionDetail;

/**
 * 
 * @author Aleem Malik
 *
 */
@Service
public class CrmAdapter implements Adapter {

	private final ObjectMapper mapper = new ObjectMapper();
	private static final String GET_ALL_TRANSACTIONS = "getAllTransactions";
	private static final String GET_ALL_CAMPAIGNS = "getAllCampaigns";
	private static final String GET_ALL_TRANSACTION_COUNT = "getAllTransactionsCount";
	private static final String CREATE_CAMPAIGN = "createCampaign";
	private static final String TOKEN_BASED_LOGIN = "tokenBasedLogin";
	
	@Autowired
	private CrmAdapterSettings crmAdapterSettings;

	@Autowired
	private RestTemplate restTemplate;

	public CrmCampaignCodeResponseDetail loadAllCampaigns(String fromDate, String toDate, List<Campaign> campaigns, List<CampaignDetail> campaignDetails) throws JsonProcessingException {

		CrmRequestPayload.Builder builder = CrmRequestPayload.Builder.newInstance();
		builder.with("fromDate", fromDate).with("toDate", toDate);
		
		CrmResponse crmResponse = restTemplate.postForObject(crmAdapterSettings.getUri().concat(GET_ALL_CAMPAIGNS), builder.buildJsonNode(), CrmResponse.class);
		CrmCampaignCodeResponseDetail crmCampaignCodeResponseDetail = mapper.treeToValue(crmResponse.getDetail(), CrmCampaignCodeResponseDetail.class);
		CrmMapper.mapCampaignData(crmCampaignCodeResponseDetail, fromDate, toDate, campaigns, campaignDetails);
		return crmCampaignCodeResponseDetail;
	}

	public CrmTransactionResponseDetail loadAllTransactions(String fromDate, String toDate,
			List<CampaignTransaction> campaignTransactions, List<TransactionDetail> transactionDetails,
			List<CampaignAttribution> campaignAttributions, List<CampaignDetail> campaignDetails) throws JsonProcessingException {

		CrmRequestPayload.Builder builder = CrmRequestPayload.Builder.newInstance();
		builder.with("fromDate", fromDate).with("toDate", toDate);
		
		CrmResponse crmResponse = restTemplate.postForObject(crmAdapterSettings.getUri().concat(GET_ALL_TRANSACTIONS), builder.buildJsonNode(), CrmResponse.class);
		CrmTransactionResponseDetail crmTransactionResponseDetail = mapper.treeToValue(crmResponse.getDetail(), CrmTransactionResponseDetail.class);
		CrmMapper.mapTransactionData(crmTransactionResponseDetail, fromDate, toDate, campaignTransactions, transactionDetails, campaignAttributions, campaignDetails);
		return crmTransactionResponseDetail;
	}
	
	public CrmTransactionCount loadTransactionCount(String fromDate, String toDate) throws JsonProcessingException {
		
		CrmRequestPayload.Builder builder = CrmRequestPayload.Builder.newInstance();
		builder.with("fromDate", fromDate).with("toDate", toDate);
		
		CrmResponse crmResponse = restTemplate.postForObject(crmAdapterSettings.getUri().concat(GET_ALL_TRANSACTION_COUNT), builder.buildJsonNode(), CrmResponse.class);
		CrmTransactionCountResponseDetail crmTransactionCountResponseDetail = mapper.treeToValue(crmResponse.getDetail(), CrmTransactionCountResponseDetail.class);
		return crmTransactionCountResponseDetail.getTransactionsCount();
	}
	
	public CrmResponse createCampaign(Campaign campaign) {
		
		CrmRequestPayload.Builder builder = CrmRequestPayload.Builder.newInstance();
		builder.with("mcCode", campaign);
		
		CrmResponse crmResponse = restTemplate.postForObject(crmAdapterSettings.getUri().concat(CREATE_CAMPAIGN), builder.buildJsonNode(), CrmResponse.class);
		return crmResponse;
	}
	
	public CrmResponse silentLogin(LoginUser loginUser) {
		
		CrmRequestPayload.Builder builder = CrmRequestPayload.Builder.newInstance();		
		builder.with("token", loginUser.getToken());
		
		CrmResponse crmResponse = restTemplate.postForObject(crmAdapterSettings.getUri().concat(TOKEN_BASED_LOGIN), builder.buildJsonNode(), CrmResponse.class);
		return crmResponse;
	}

}
