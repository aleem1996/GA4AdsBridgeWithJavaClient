package com.gr.dm.core.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.gr.dm.core.dto.AggregateDto;
import com.gr.dm.core.dto.KeywordDto;
import com.gr.dm.core.dto.KeywordListDto;
import com.gr.dm.core.dto.report.KeywordPerformanceDto;
import com.gr.dm.core.entity.Keyword;
import com.gr.dm.core.entity.KeywordDetail;
import com.gr.dm.core.repository.KeywordDetailRepository;
import com.gr.dm.core.repository.KeywordRepository;
import com.gr.dm.core.util.Util;

/**
 * @author ufarooq
 */
@Service
public class KeywordService {

	@Autowired
	KeywordRepository keywordRepository;

	@Autowired
	KeywordDetailRepository keywordDetailRepository;

	public void saveKeyword(List<Keyword> keywords) {
		for (Keyword keyword : keywords) {
			Keyword savedKeyword = keywordRepository.findByAdGroupIdAndKeywordId(keyword.getAdGroupId(), keyword.getKeywordId());
			if (Util.isNull(savedKeyword)) {
				saveKeyword(keyword);
			}
		}
	}

	public void saveKeyword(Keyword keyword) {
		keywordRepository.save(keyword);
	}

	public void saveKeywordDetail(List<KeywordDetail> keywordDetails) {
		for (KeywordDetail keywordDetail : keywordDetails) {
			KeywordDetail savedDetail = keywordDetailRepository.getKeywordDetail(keywordDetail.getKeywordId(), keywordDetail.getStartDate(), keywordDetail.getEndDate());
			if (Util.isNull(savedDetail)) {
				saveKeywordDetail(keywordDetail);
			} else {
				savedDetail.setClicks(keywordDetail.getClicks());
				savedDetail.setCost(keywordDetail.getCost());
				savedDetail.setImpressions(keywordDetail.getImpressions());
				savedDetail.setRevenue(keywordDetail.getRevenue());
				savedDetail.setTransactionCount(keywordDetail.getTransactionCount());
				saveKeywordDetail(savedDetail);
			}
		}
	}

	public void saveKeywordDetail(KeywordDetail keywordDetail) {
		keywordDetailRepository.save(keywordDetail);
	}
	
	public KeywordListDto getKeywords(String adGroupId, Date startDate, Date endDate) {
		List<KeywordDto> keywords =  keywordRepository.getKeywords(adGroupId, startDate, endDate);
		AggregateDto aggregateDto = keywordRepository.getStats(adGroupId, startDate, endDate);
		KeywordListDto keywordListDto = new KeywordListDto();
		keywordListDto.setKeywordDto(keywords);
		keywordListDto.setAggregateDto(aggregateDto);
		return keywordListDto;
	}
	
	@Cacheable(value = "keywordPerformanceCache", key = "#startDate.toString().concat('-').concat(#endDate.toString())")
	public List<KeywordPerformanceDto> getKeywordPerformanceReport(Date startDate, Date endDate) {
		return keywordDetailRepository.getKeywordPerformanceReport(startDate, endDate);
	}

}
