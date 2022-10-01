package com.mkcy.everydaymail.account;

public class MailAccountResponse {
	//private String id;
	private int registerId;
	//private String mailHost;
	private String mailAddr;
	private String registerDate;
	private int deletedMails;
	
	public int getRegisterId() {
		return registerId;
	}
	
	public void setRegisterId(int registerId) {
		this.registerId = registerId;
	}
	
	public String getMailAddr() {
		return mailAddr;
	}
	
	public void setMailAddr(String mailAddr) {
		this.mailAddr = mailAddr;
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
	

}
