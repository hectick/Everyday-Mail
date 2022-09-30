package com.mkcy.everydaymail.user;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Repository
public class User {
	private String id;
	@JsonIgnore
	private String password;
	private int totalDeletedMails;
	@JsonIgnore
	private int accountsCnt;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getTotalDeletedMails() {
		return totalDeletedMails;
	}
	
	public void setTotalDeletedMails(int totalDeletedMails) {
		this.totalDeletedMails = totalDeletedMails;
	}
	
	public int getAccountsCnt() {
		return accountsCnt;
	}
	
	public void setAccountsCnt(int accountsCnt) {
		this.accountsCnt = accountsCnt;
	}
}
