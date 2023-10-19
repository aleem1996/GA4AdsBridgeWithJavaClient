package com.gr.dm.core.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.gr.dm.core.util.Util;

@Component
public class ReportSummaryDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public Map<String, List<String>> executeQuery(String query, Map<String, Object> params) {

		Map<String, List<String>> mappedData = new LinkedHashMap<String, List<String>>();

		namedParameterJdbcTemplate.query(query, params, new ResultSetExtractor<Object>() {

			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData metData = rs.getMetaData();
				for (int colcount = 0; colcount < metData.getColumnCount(); colcount++) {
					mappedData.put(metData.getColumnLabel(colcount + 1), new ArrayList<String>());
				}

				while (rs.next()) {
					for (int colindex = 0; colindex < mappedData.size(); colindex++) {
						String mapKey = mappedData.keySet().toArray()[colindex].toString();
						String value = Util.isNull(rs.getString(mapKey)) ? "" : rs.getString(mapKey).trim();
						mappedData.get(mapKey).add(value);
					}
				}
				return mappedData;
			}

		});

		return mappedData;
	}

}
