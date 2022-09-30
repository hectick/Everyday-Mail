package com.mkcy.everydaymail.account.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.mkcy.everydaymail.config.DBconfigInfo;

public class UnsubscribeStatementDao {

	private JdbcTemplate template;
	private BasicDataSource dataSource;
	
	private void setDataSource() {
		DBconfigInfo config = new DBconfigInfo();
		this.dataSource = new BasicDataSource();
		this.dataSource.setDriverClassName(config.getDriverName());
		this.dataSource.setUrl(config.getUrl());
		this.dataSource.setUsername(config.getUser());
		this.dataSource.setPassword(config.getPassword());
		this.template = new JdbcTemplate(dataSource);
	}
	
	private void closeDataSource(){
		try {
			if(!dataSource.isClosed()) {
				dataSource.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getAllUnsubscribeStatement () {
		setDataSource();
		List<String> list = new ArrayList<String>();
		
		String sql = "SELECT * FROM unsubscribe_statement";
		
		list = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				
			}
			
		}, new RowMapper<String>(){
			
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String words;
				words = rs.getString("words");
				return words;
			}
		});
		closeDataSource();
		//if(list.isEmpty()) return null;
		return list;
	}
}
