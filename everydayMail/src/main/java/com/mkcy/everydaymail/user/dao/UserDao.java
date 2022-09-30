package com.mkcy.everydaymail.user.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.mkcy.everydaymail.user.User;

@Repository
public class UserDao {
	
	private JdbcTemplate template;
	
	@Autowired
	public UserDao(BasicDataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}
	
	public int userInsert(User user) {
			
		int result = 0;
			
		String sql = "INSERT INTO member (member_id, member_pw, total_count_deleted_mails, count_accounts) values (?,?,?,?)";
		result = template.update(sql, user.getId(), user.getPassword(), user.getTotalDeletedMails(), user.getAccountsCnt());
		
		
		return result;
	}

	public User userSelect(String id) {
		List<User> users = null;
		final String sql = "SELECT * FROM member WHERE member_id = ?";
		users = template.query(sql, new PreparedStatementSetter(){
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, id);
			}
			
		}, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException{
				User mem = new User();
				mem.setId(rs.getString("member_id"));
				mem.setPassword(rs.getString("member_pw"));
				mem.setTotalDeletedMails(rs.getInt("total_count_deleted_mails"));
				mem.setAccountsCnt(rs.getInt("count_accounts"));
	
		
				return mem;
			}
		});
		
		if(users.isEmpty()) return null;
		
		return users.get(0);
	}
	
	public User userSelect(User user) {
		
		List<User> users = null;
		final String sql = "SELECT * FROM member WHERE member_id = ? and member_pw = ?";
		users = template.query(sql, new PreparedStatementSetter(){
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, user.getId());
				pstmt.setString(2, user.getPassword());
			}
			
		}, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException{
				User mem = new User();
				mem.setId(rs.getString("member_id"));
				mem.setPassword(rs.getString("member_pw"));
				mem.setTotalDeletedMails(rs.getInt("total_count_deleted_mails"));
				mem.setAccountsCnt(rs.getInt("count_accounts"));
	
		
				return mem;
			}
		});
		
		if(users.isEmpty()) return null;
		
		return users.get(0);
		
	}

	public int userUpdatePassword(User user) {
		
		int result = 0;
		
		String sql = "UPDATE member SET member_pw = ? WHERE member_id = ?";
		result = template.update(sql, user.getPassword(), user.getId());
		
		return result;
		
	}
	
	public int userUpdateAccountNum(User user) {
		int result = 0;
		
		String sql = "UPDATE member SET count_accounts = ? WHERE member_id = ?";
		result = template.update(sql, user.getAccountsCnt(), user.getId());
		
		return result;
	}
	
	public int userUpdateDeletedMailsCnt(User user) {
		int result = 0;
		
		String sql = "UPDATE member SET total_count_deleted_mails = ? WHERE member_id = ?";
		result = template.update(sql, user.getTotalDeletedMails(), user.getId());
		
		return result;
	}
	
	public int userDelete(User user) {
		
		int result = 0;
		String sql = "DELETE member WHERE member_id = ? AND member_pw = ?";
		result = template.update(sql, user.getId(), user.getPassword());
		
		return result;
		
	}


}
