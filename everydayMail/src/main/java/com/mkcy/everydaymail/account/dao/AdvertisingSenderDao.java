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

import com.mkcy.everydaymail.account.AdvertisingSender;
import com.mkcy.everydaymail.account.MailAccount;

@Repository
public class AdvertisingSenderDao {

	private JdbcTemplate template;

	@Autowired
	public AdvertisingSenderDao(BasicDataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	
	public int advertisingSenderInsert(AdvertisingSender advertisingSender) {

		int result = 0;
		
		String sql = "INSERT INTO advertising_sender (index_id, mail_addr, sender_name, sender_addr, mail_count, unsubscribe_link) values (?,?,?,?,?,?)";
		result = template.update(sql, advertisingSender.getId(), advertisingSender.getMailAddr(), advertisingSender.getSenderName(), advertisingSender.getSenderAddr(), advertisingSender.getMailCount(), advertisingSender.getUnsubscribeLink());
		
		return result;
		
	}
	
	public AdvertisingSender advertisingSenderSelect (AdvertisingSender advertisingSender) {
		List<AdvertisingSender> senders = new ArrayList<AdvertisingSender>();
		
		String sql = "SELECT * FROM advertising_sender WHERE mail_addr = ? AND sender_name = ? AND sender_addr = ? AND index_id = ?";
		
		senders = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, advertisingSender.getMailAddr());
				pstmt.setString(2, advertisingSender.getSenderName());
				pstmt.setString(3, advertisingSender.getSenderAddr());
				pstmt.setString(4, advertisingSender.getId());
			}
			
		}, new RowMapper<AdvertisingSender>(){
			
			@Override
			public AdvertisingSender mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdvertisingSender sender = new AdvertisingSender();
				sender.setId(rs.getString("index_id"));
				sender.setMailAddr(rs.getString("mail_addr"));
				sender.setSenderName(rs.getString("sender_name"));
				sender.setSenderAddr(rs.getString("sender_addr"));
				sender.setMailCount(rs.getInt("mail_count"));
				sender.setUnsubscribeLink(rs.getString("unsubscribe_link"));
				return sender;
			}
		});
		
		return senders.get(0);
	}
	
	//메일 개수 업뎃
	public int advertisingSenderUpdateMailCount(AdvertisingSender advertisingSender) {
		int result = 0;
		
		String sql = "UPDATE advertising_sender SET mail_count = ? WHERE mail_addr = ? AND sender_name = ? AND sender_addr = ? AND index_id = ?";
		result = template.update(sql, advertisingSender.getMailCount(), advertisingSender.getMailAddr(), advertisingSender.getSenderName(), advertisingSender.getSenderAddr(), advertisingSender.getId());
		
		return result;
	}
	
	//구독취소링크 업뎃
	public int advertisingSenderUpdateUnsubscribeLink(AdvertisingSender advertisingSender) {
		int result = 0;
		
		String sql = "UPDATE advertising_sender SET unsubscribe_link = ? WHERE mail_addr = ? AND sender_name = ? AND sender_addr = ? AND index_id = ?";
		result = template.update(sql, advertisingSender.getUnsubscribeLink(), advertisingSender.getMailAddr(), advertisingSender.getSenderName(), advertisingSender.getSenderAddr(), advertisingSender.getId());
		
		return result;
	}
	
	public int advertisingSenderDeleteAllOfMailAddr(MailAccount mailAccount) {
		int result = 0;

		String sql = "DELETE FROM advertising_sender WHERE index_id = ? AND mail_addr = ?";
		result = template.update(sql, mailAccount.getId(), mailAccount.getMailAddr());
		
		return result;
	}
	
	public List<AdvertisingSender> SelectAllAdvertisingSenders(MailAccount mailAccount) {
		List<AdvertisingSender> adSenders = new ArrayList<AdvertisingSender>();
		
		String sql = "SELECT * FROM advertising_sender WHERE index_id = ? AND mail_addr = ?";
		
		adSenders = template.query(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException{
				pstmt.setString(1, mailAccount.getId());
				pstmt.setString(2, mailAccount.getMailAddr());
			}
			
		}, new RowMapper<AdvertisingSender>(){
			
			@Override
			public AdvertisingSender mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdvertisingSender sender = new AdvertisingSender();
				sender.setId(rs.getString("index_id"));
				sender.setMailAddr(rs.getString("mail_addr"));
				sender.setSenderName(rs.getString("sender_name"));
				sender.setSenderAddr(rs.getString("sender_addr"));
				sender.setMailCount(rs.getInt("mail_count"));
				sender.setUnsubscribeLink(rs.getString("unsubscribe_link"));
				return sender;
			}
		});
		
		return adSenders;
	}	
	
}
