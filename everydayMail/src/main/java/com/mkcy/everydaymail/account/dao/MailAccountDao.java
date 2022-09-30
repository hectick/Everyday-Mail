package com.mkcy.everydaymail.account.dao;

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

import com.mkcy.everydaymail.account.MailAccount;
import com.mkcy.everydaymail.account.MailAccountResponse;
import com.mkcy.everydaymail.user.User;

@Repository
public class MailAccountDao {
	
	private JdbcTemplate template;
	

	@Autowired
	public MailAccountDao(BasicDataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}
	
	public int mailAccountInsert(MailAccount mailAccount) {
		int result = 0;
		
		String sql = "INSERT INTO account (index_id, mail_host, mail_addr, mail_pw, register_date, count_deleted_mails, register_id) values (?,?,?,?,?,?,?)";
		result = template.update(sql, mailAccount.getId(), mailAccount.getMailHost(), mailAccount.getMailAddr(), mailAccount.getMailPw(), mailAccount.getRegisterDate(), mailAccount.getDeletedMails(), mailAccount.getRegisterId());
			
		return result;
	}
	
	
	public int mailAccountDelete(MailAccount mailAccount) {
		int result = 0;

		String sql = "DELETE FROM account WHERE mail_addr = ? AND index_id = ?";
		result = template.update(sql, mailAccount.getMailAddr(), mailAccount.getId());
		
		return result;
	}
	
	//메일계정 주소로 찾기
	public MailAccount mailAccountSelect(MailAccount mailAccount) {
		List<MailAccount> accounts = null;
		
		String sql = "SELECT * FROM account WHERE mail_addr = ? AND index_id = ?";
		
		accounts = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, mailAccount.getMailAddr());
				pstmt.setString(2, mailAccount.getId());
			}
			
		}, new RowMapper<MailAccount>(){
			
			@Override
			public MailAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailAccount account = new MailAccount();
				account.setId(rs.getString("index_id"));
				account.setMailHost(rs.getString("mail_host"));
				account.setMailAddr(rs.getString("mail_addr"));
				account.setMailPw(rs.getNString("mail_pw"));
				account.setRegisterDate(rs.getString("register_date"));
				account.setDeletedMails(rs.getInt("count_deleted_mails"));
				account.setRegisterId(rs.getInt("register_id"));
				return account;
			}
		});
		
		if(accounts.isEmpty()) return null;
		
		return accounts.get(0);
	}
	
	//메일계정을 등록id로 찾기
	public MailAccount mailAccountSelect(String id, int register_id) {
		List<MailAccount> accounts = null;
		
		String sql = "SELECT * FROM account WHERE register_id = ? AND index_id = ?";
		
		accounts = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setInt(1, register_id);
				pstmt.setString(2, id);
			}
			
		}, new RowMapper<MailAccount>(){
			
			@Override
			public MailAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailAccount account = new MailAccount();
				account.setId(rs.getString("index_id"));
				account.setMailHost(rs.getString("mail_host"));
				account.setMailAddr(rs.getString("mail_addr"));
				account.setMailPw(rs.getNString("mail_pw"));
				account.setRegisterDate(rs.getString("register_date"));
				account.setDeletedMails(rs.getInt("count_deleted_mails"));
				account.setRegisterId(rs.getInt("register_id"));
				return account;
			}
		});
		
		if(accounts.isEmpty()) return null;
		
		return accounts.get(0);
	}
	
	//이미 존재하는 계정인지 찾기 용도
	public MailAccount mailAccountSelect(String mailAddr) {
		List<MailAccount> accounts = null;
		
		String sql = "SELECT * FROM account WHERE mail_addr = ?";
		
		accounts = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, mailAddr);
			}
			
		}, new RowMapper<MailAccount>(){
			
			@Override
			public MailAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailAccount account = new MailAccount();
				account.setId(rs.getString("index_id"));
				account.setMailHost(rs.getString("mail_host"));
				account.setMailAddr(rs.getString("mail_addr"));
				account.setMailPw(rs.getNString("mail_pw"));
				account.setRegisterDate(rs.getString("register_date"));
				account.setDeletedMails(rs.getInt("count_deleted_mails"));
				account.setRegisterId(rs.getInt("register_id"));
				return account;
			}
		});
		
		if(accounts.isEmpty()) return null;
		
		return accounts.get(0);
	}
	
	public int mailAccountUpdateDeletedMails(MailAccount mailAccount) {
		int result = 0;
		
		 String sql = "UPDATE account SET count_deleted_mails = ? WHERE mail_addr = ? AND index_id = ?";
		 result = template.update(sql, mailAccount.getDeletedMails(), mailAccount.getMailAddr(), mailAccount.getId());
		
		return result;
	}
	
	public int mailAccountUpdatePassword(MailAccount mailAccount) {
		int result = 0;
		
		 String sql = "UPDATE account SET mail_pw = ? WHERE mail_addr = ? AND index_id = ?";
		 result = template.update(sql, mailAccount.getMailPw(), mailAccount.getMailAddr(), mailAccount.getId());
		
		return result;
	}

	//해당 member_id의 모든 이메일 계정을 뽑아서 반환1 : 외부에 내보내기
	public List<MailAccountResponse> selectMailAccountResponseAll(User user) {
		List<MailAccountResponse> accounts =  new ArrayList<MailAccountResponse>();
		
		String sql = "SELECT * FROM account WHERE index_id = ?";
		
		accounts = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, user.getId());
			}
			
		}, new RowMapper<MailAccountResponse>(){
			
			@Override
			public MailAccountResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailAccountResponse account = new MailAccountResponse();
				account.setMailAddr(rs.getString("mail_addr"));
				account.setRegisterDate(rs.getString("register_date"));
				account.setDeletedMails(rs.getInt("count_deleted_mails"));
				account.setRegisterId(rs.getInt("register_id"));
				return account;
			}
		});
		
		return accounts;
	}
	
	//해당 member_id의 모든 이메일 계정을 뽑아서 반환2 : 내부 사용
	public List<MailAccount> selectMailAccountAll(User user) {
		List<MailAccount> accounts = new ArrayList<MailAccount>();
			
		String sql = "SELECT * FROM account WHERE index_id = ?";
			
		accounts = template.query(sql, new PreparedStatementSetter() {
				
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, user.getId());
			}
				
		}, new RowMapper<MailAccount>(){
				
			@Override
			public MailAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
				MailAccount account = new MailAccount();
				account.setId(rs.getString("index_id"));
				account.setMailHost(rs.getString("mail_host"));
				account.setMailAddr(rs.getString("mail_addr"));
				account.setMailPw(rs.getNString("mail_pw"));
				account.setRegisterDate(rs.getString("register_date"));
				account.setDeletedMails(rs.getInt("count_deleted_mails"));
				account.setRegisterId(rs.getInt("register_id"));
				return account;
			}
		});
		
		return accounts;
	}

}
