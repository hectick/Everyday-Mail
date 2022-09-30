package com.mkcy.everydaymail.account;

import org.springframework.stereotype.Repository;

@Repository
public class MailAccount {
	private String id;
	private int registerId;
	private String mailHost;
	private String mailAddr;
	private String mailPw;
	private String registerDate;
	private int deletedMails;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getMailHost() {
		return mailHost;
	}
	
	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}
	
	public String getMailAddr() {
		return mailAddr;
	}
	
	public void setMailAddr(String mailAddr) {
		this.mailAddr = mailAddr;
	}
	
	public String getMailPw() {
		return mailPw;
	}
	
	public void setMailPw(String mailPw) {
		this.mailPw = mailPw;
	}
	
	public String getRegisterDate() {
		return registerDate;
	}
	
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	
	public int getDeletedMails() {
		return deletedMails;
	}
	
	public void setDeletedMails(int deletedMails) {
		this.deletedMails = deletedMails;
	}
	
	public int getRegisterId() {
		return registerId;
	}
	
	public void setRegisterId(int registerId) {
		this.registerId = registerId;
	}
}
