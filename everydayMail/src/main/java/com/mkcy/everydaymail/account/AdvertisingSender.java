package com.mkcy.everydaymail.account;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdvertisingSender {
	@JsonIgnore
	private String id;
	@JsonIgnore
	private String mailAddr;
	private String senderName; //광고메일을 보낸곳 이름
	private String senderAddr; //광고메일을 보낸곳 주소
	private int mailCount; //삭제된 "해당 sender에서 보낸 광고"메일의 개수
	private String unsubscribeLink = ""; //광고메일 구독취소 링크
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMailAddr() {
		return mailAddr;
	}

	public void setMailAddr(String mailAddr) {
		this.mailAddr = mailAddr;
	}
	
	public String getSenderName() {
		return senderName;
	}
	
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	public String getSenderAddr() {
		return senderAddr;
	}
	
	public void setSenderAddr(String senderAddr) {
		this.senderAddr = senderAddr;
	}
	
	public int getMailCount() {
		return mailCount;
	}
	
	public void setMailCount(int mailCount) {
		this.mailCount = mailCount;
	}
	
	public String getUnsubscribeLink() {
		return unsubscribeLink;
	}
	
	public void setUnsubscribeLink(String unsubscribeLink) {
		this.unsubscribeLink = unsubscribeLink;
	}
	
}
