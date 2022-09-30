package com.mkcy.everydaymail.user.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mkcy.everydaymail.user.UserBatchProcessingInfo;

@Repository
public class UserBatchProcessingInfoDao {
	
	private JdbcTemplate template;
	
	@Autowired
	public UserBatchProcessingInfoDao(BasicDataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}
	
	//삽입
	public int userBatchProcessingInfoInsert(UserBatchProcessingInfo userInfo) {
		
		int result = 0;
			
		String sql = "INSERT INTO batch_processing_info (index_id, spam_word, spam_word_setting, time_deadline, time_deadline_unit, time_deadline_setting, important_word, important_word_setting) values (?,?,?,?,?,?,?,?)";
		result = template.update(sql, userInfo.getId(), userInfo.getSpamWord(), userInfo.getSpamWordSetting(), userInfo.getTimeDeadline(), userInfo.getTimeDeadlineUnit(), userInfo.getTimeDeadlineSetting(), userInfo.getImportantWord(), userInfo.getImportantWordSetting());
			
		return result;
	}
	
	
	//수정
	public int userBatchProcessingInfoUpdate(UserBatchProcessingInfo userInfo) {
		
		int result = 0;
		
		String sql = "DELETE batch_processing_info WHERE index_id = ?";
		result = template.update(sql, userInfo.getId());
		
		String sql2 = "INSERT INTO batch_processing_info (index_id, spam_word, spam_word_setting, time_deadline, time_deadline_unit, time_deadline_setting, important_word, important_word_setting) values (?,?,?,?,?,?,?,?)";
		result = template.update(sql2, userInfo.getId(), userInfo.getSpamWord(), userInfo.getSpamWordSetting(), userInfo.getTimeDeadline(), userInfo.getTimeDeadlineUnit(), userInfo.getTimeDeadlineSetting(), userInfo.getImportantWord(), userInfo.getImportantWordSetting());
			
		return result;
	}
	
	//삭제
	public int userBatchProcessingInfoDelete(String id) {
		
		int result = 0;
		String sql = "DELETE batch_processing_info WHERE index_id = ?";
		result = template.update(sql, id);
		
		return result;
	}
	
	//모두 가져오기
	public List<UserBatchProcessingInfo> userBatchProcessingInfoSelectAll() {
		
		List<UserBatchProcessingInfo> userInfos = new ArrayList<>();
		
		String sql = "SELECT * FROM batch_processing_info";
		
		userInfos = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				
			}
			
		}, new RowMapper<UserBatchProcessingInfo>(){
			
			@Override
			public UserBatchProcessingInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserBatchProcessingInfo userInfo = new UserBatchProcessingInfo();
				userInfo.setId(rs.getString("index_id"));
				userInfo.setSpamWord(rs.getString("spam_word"));
				userInfo.setSpamWordSetting(rs.getInt("spam_word_setting"));
				userInfo.setTimeDeadline(rs.getInt("time_deadline"));
				userInfo.setTimeDeadlineUnit(rs.getString("time_deadline_unit"));
				userInfo.setTimeDeadlineSetting(rs.getInt("time_deadline_setting"));
				userInfo.setImportantWord(rs.getString("important_word"));
				userInfo.setImportantWordSetting(rs.getInt("important_word_setting"));
				return userInfo;
			}
		});
		
		return userInfos;
	}
	
	//하나만 가져오기
	public UserBatchProcessingInfo userBatchProcessingInfoSelect(String id) {
		
		List<UserBatchProcessingInfo> userInfos = new ArrayList<>();
		
		String sql = "SELECT * FROM batch_processing_info WHERE index_id = ?";
		
		userInfos = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, id);
			}
			
		}, new RowMapper<UserBatchProcessingInfo>(){
			
			@Override
			public UserBatchProcessingInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserBatchProcessingInfo userInfo = new UserBatchProcessingInfo();
				userInfo.setId(rs.getString("index_id"));
				userInfo.setSpamWord(rs.getString("spam_word"));
				userInfo.setSpamWordSetting(rs.getInt("spam_word_setting"));
				userInfo.setTimeDeadline(rs.getInt("time_deadline"));
				userInfo.setTimeDeadlineUnit(rs.getString("time_deadline_unit"));
				userInfo.setTimeDeadlineSetting(rs.getInt("time_deadline_setting"));
				userInfo.setImportantWord(rs.getString("important_word"));
				userInfo.setImportantWordSetting(rs.getInt("important_word_setting"));
				return userInfo;
			}
		});
		
		if(userInfos.isEmpty()) return null;
		
		return userInfos.get(0);
	}
	
}
