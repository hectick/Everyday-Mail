package com.mkcy.everydaymail.user;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Repository
public class UserBatchProcessingInfo {
	//0 : false, 1 : true
	@JsonIgnore
	private String id;
	private String spamWord;
	private int spamWordSetting;
	private int timeDeadline;
	private String timeDeadlineUnit;
	private int timeDeadlineSetting;
	private String importantWord;
	private int importantWordSetting;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSpamWord() {
		return spamWord;
	}
	public void setSpamWord(String spamWord) {
		this.spamWord = spamWord;
	}
	public int getSpamWordSetting() {
		return spamWordSetting;
	}
	public void setSpamWordSetting(int spamWordSetting) {
		this.spamWordSetting = spamWordSetting;
	}
	public int getTimeDeadline() {
		return timeDeadline;
	}
	public void setTimeDeadline(int timeDeadline) {
		this.timeDeadline = timeDeadline;
	}
	public String getTimeDeadlineUnit() {
		return timeDeadlineUnit;
	}
	public void setTimeDeadlineUnit(String timeDeadlineUnit) {
		this.timeDeadlineUnit = timeDeadlineUnit;
	}
	public int getTimeDeadlineSetting() {
		return timeDeadlineSetting;
	}
	public void setTimeDeadlineSetting(int timeDeadlineSetting) {
		this.timeDeadlineSetting = timeDeadlineSetting;
	}
	public String getImportantWord() {
		return importantWord;
	}
	public void setImportantWord(String importantWord) {
		this.importantWord = importantWord;
	}
	public int getImportantWordSetting() {
		return importantWordSetting;
	}
	public void setImportantWordSetting(int importantWordSetting) {
		this.importantWordSetting = importantWordSetting;
	}
	
	
	
}
