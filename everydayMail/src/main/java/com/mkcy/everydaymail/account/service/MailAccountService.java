package com.mkcy.everydaymail.account.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mkcy.everydaymail.account.AdvertisingSender;
import com.mkcy.everydaymail.account.MailAccount;
import com.mkcy.everydaymail.account.MailAccountResponse;
import com.mkcy.everydaymail.account.dao.AdvertisingSenderDao;
import com.mkcy.everydaymail.account.dao.MailAccountDao;
import com.mkcy.everydaymail.account.dao.UnsubscribeStatementDao;
import com.mkcy.everydaymail.user.User;
import com.mkcy.everydaymail.user.dao.UserDao;

@Repository("mailAccountService")
public class MailAccountService {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	MailAccountDao mailAccountDao;
	
	@Autowired
	MailAccount account;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	AdvertisingSenderDao adSenderDao;
	
	private IMAPEmailConnection mailConnection;
	
	public void setMailAccountService() {
		mailConnection = new IMAPEmailConnection();
	}
	
	public void setMailAccountService(String id, String mailAddress) {
		mailConnection = new IMAPEmailConnection();
		
		MailAccount tmpAccount = new MailAccount();
		tmpAccount.setId(id);
		tmpAccount.setMailAddr(mailAddress);
		this.account = mailAccountDao.mailAccountSelect(tmpAccount);
	}
	
	public void setMailAccountService(MailAccount mailAccount) {
		this.account = mailAccountDao.mailAccountSelect(mailAccount);
		mailConnection = new IMAPEmailConnection();
	}
	
	public void setPassword(String newPassword) {
		account.setMailPw(newPassword);
		mailAccountDao.mailAccountUpdatePassword(account);
	}
	
	public MailAccount getAccount() {
		return account;
	}

	public void setAccount(MailAccount account) {
		this.account = account;
	}

	//getter, setter 끝 기능 시작
	
	//메일 유효성 검사
	public boolean checkMailValidation(String mailHost, String mailAddr, String mailPw) {
		mailConnection = new IMAPEmailConnection();
		
		this.account.setMailHost(mailHost);
		this.account.setMailAddr(mailAddr);
		this.account.setMailPw(mailPw);
		
		boolean res = login();
		
		return res;
	}
	
	//메일 계정 추가
	public void addMailAccount(String id, String mailHost, String mailAddr, String mailPw) {
		
		User user = userDao.userSelect(id);
		int num = user.getAccountsCnt()+1;
		
		account.setId(id);
		account.setMailHost(mailHost);
		account.setMailAddr(mailAddr);
		account.setMailPw(mailPw);
		account.setDeletedMails(0);
		account.setRegisterId(num);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = dateFormat.format(new Date());
		account.setRegisterDate(currentDate);
		
		mailAccountDao.mailAccountInsert(account);
		
		user.setAccountsCnt(num);
		userDao.userUpdateAccountNum(user);
		
	}
	
	
	//메일 계정 삭제
	public void deleteMailAccount(String id, String mailAddr) {
		account.setMailAddr(mailAddr);
		account.setId(id);
		
		adSenderDao.advertisingSenderDeleteAllOfMailAddr(account);
		mailAccountDao.mailAccountDelete(account);
	}
	
	//메일계정 count 초기화
	public void resetMailAccountCount(String id, String mailAddr) {
		account.setMailAddr(mailAddr);
		account.setId(id);
		account.setDeletedMails(0);
		mailAccountDao.mailAccountUpdateDeletedMails(account);
	}
	
	//메일 주소 체크
	public boolean isExistMail(String mailAddr) {
		MailAccount account = mailAccountDao.mailAccountSelect(mailAddr);
		
		if(account == null) {
			return false;
		}
		
		return true;
	}
	
	public boolean login(){
		try {
			mailConnection.login(account);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void logout(){
		try {
			mailConnection.logout();
		}catch(MessagingException me) {
			me.printStackTrace();
		}
	}
	
	/*광고 수신자 관련*/
	public List<AdvertisingSender> getAdvertisingSender(String id, String mailAddress){
		//adSenderList 가져오기
		MailAccount tmpAccount = new MailAccount();
		tmpAccount.setId(id);
		tmpAccount.setMailAddr(mailAddress);
		this.account = mailAccountDao.mailAccountSelect(tmpAccount);
		
		List<AdvertisingSender> adSenderList = new ArrayList<>();
		adSenderList = adSenderDao.SelectAllAdvertisingSenders(account);
		
		return adSenderList;
	}
	
	/*개별 메일 계정에서 메일을 삭제하는 기본(참고) 관련 - private*/
	private int deleteAllMails() {
		int cnt = 0;
		try {
			cnt = mailConnection.deleteAllMails();
		}catch(MessagingException me) {
			me.printStackTrace();
		}
		return cnt;
		
	}

	private int deleteAdvertisingMails() {
		int cnt = 0;
		try {
			cnt = mailConnection.deleteAdvertisingMails();
		}catch(MessagingException me) {
			me.printStackTrace();
		}
		return cnt;
	}
	
	private int deleteMailsContainingSpecificText(String specificText) {
		int cnt = 0;
		try {
			cnt = mailConnection.deleteMailsContainingSpecificText(specificText);
		}catch(MessagingException me) {
			me.printStackTrace();
		}
		return cnt;
	}
	
	private int deleteMailsFromSpecificSender(String specificSender) {
		int cnt = 0;
		try {
			cnt = mailConnection.deleteMailsFromSpecificSender(specificSender);
		}catch(MessagingException me){
			me.printStackTrace();
		}
		return cnt;
	}
	
	private int deleteMailsInSpecificPeriod(String firstDay, String lastDay){
		int cnt = 0;
		try {
			cnt = mailConnection.deleteMailsInSpecificPeriod(firstDay, lastDay);
		}catch(MessagingException me) {
			me.printStackTrace();
		}
		return cnt;
	}
	
	private void readMails() {
		try {
			mailConnection.readMails();
		}catch(MessagingException me) {
			me.printStackTrace();
		}
	}
	

	/*계정 읽어오기*/
	public List<MailAccountResponse> getUserMailAccountResponseAll(User user){
		
		if(user.getAccountsCnt() == 0) return null;
		else {
			List<MailAccountResponse> accounts = null;
			accounts = mailAccountDao.selectMailAccountResponseAll(user);
			
			return accounts;
		}
	}
	
	private List<MailAccount> getUserMailAccountAll(User user){
		
		if(user.getAccountsCnt() == 0) return null;
		else {
			List<MailAccount> accounts = null;
			accounts = mailAccountDao.selectMailAccountAll(user);
			
			return accounts;
		}
	}

	
	public List<String> getUserMailAccountsAddressAll(User user){
		
		if(user.getAccountsCnt() == 0) return null;
		else {
			List<MailAccountResponse> accounts = new ArrayList<MailAccountResponse>();
			accounts = mailAccountDao.selectMailAccountResponseAll(user);
			
			List<String> accountAddrs = new ArrayList<String>();
			for(int i = 0; i < accounts.size(); i++) {
				accountAddrs.add(accounts.get(i).getMailAddr());
			}
			return accountAddrs;
		}
		
	}

	/*메일 스캔 기능 관련*/
	public void deleteAdvertisingMails(User user) {
		
		List<MailAccount> accounts = getUserMailAccountAll(user);
		
		UnsubscribeStatementDao unsubDao = new UnsubscribeStatementDao();
		List<String> unsubList = unsubDao.getAllUnsubscribeStatement();
		
		if(accounts == null || accounts.isEmpty()) {
			return;
		}else {
			int value = user.getTotalDeletedMails();
			for(int i = 0; i < accounts.size(); i++) {
				MailAccount turn = accounts.get(i);
				setMailAccountService(turn);
				List<AdvertisingSender> adList = adSenderDao.SelectAllAdvertisingSenders(turn);
				mailConnection.setAdSenderList(adList);
				mailConnection.setUnsubscribeWordsList(unsubList);
				login();
				int sum = deleteAdvertisingMails();
				List<AdvertisingSender> adSenderList = mailConnection.getAdSenderList();
				List<AdvertisingSender> newAdSenderList = mailConnection.getNewadSenderList();
				logout();
				value += sum; //user count 변경완료
				
				//개별 account count 변경
				int accountValue = turn.getDeletedMails() + sum;
				turn.setDeletedMails(accountValue);
				mailAccountDao.mailAccountUpdateDeletedMails(turn);
				

				//디비 업뎃-새로운 광고자
				
				for(int k = 0; k < newAdSenderList.size(); k++) {
					System.out.println(newAdSenderList.get(k).getSenderName());
					adSenderDao.advertisingSenderInsert(newAdSenderList.get(k));
				}
				
				//디비 업뎃 - 기존 광고자+ 새로운광고자
				for(int k = 0; k < adSenderList.size(); k++) {
					adSenderDao.advertisingSenderUpdateMailCount(adSenderList.get(k));
					adSenderDao.advertisingSenderUpdateUnsubscribeLink(adSenderList.get(k));
				}
				
			}
			//DB 업뎃
			user.setTotalDeletedMails(value);
			userDao.userUpdateDeletedMailsCnt(user);
			return;
		}		
	}
	
	public void deleteMailsContainingSpecificText(User user, String text) {
		
		List<MailAccount> accounts = getUserMailAccountAll(user);
		
		if(accounts == null || accounts.isEmpty()) {
			return;
		}else {
			int value = user.getTotalDeletedMails();
			for(int i = 0; i < accounts.size(); i++) {
				MailAccount turn = accounts.get(i);
				setMailAccountService(turn);
				login();
				int sum = deleteMailsContainingSpecificText(text);
				logout();
				value += sum; //변경완료
				
				//개별 account count 변경
				int accountValue = turn.getDeletedMails() + sum;
				turn.setDeletedMails(accountValue);
				mailAccountDao.mailAccountUpdateDeletedMails(turn);
			}
			
			//DB 업뎃
			user.setTotalDeletedMails(value);
			userDao.userUpdateDeletedMailsCnt(user);
			return;
		}
	}
	
	public void deleteMailsInSpecificPeriod(User user, String firstDay, String lastDay) {
		
		List<MailAccount> accounts = getUserMailAccountAll(user);
			
		if(accounts == null || accounts.isEmpty()) {
			return;
		}else {
			int value = user.getTotalDeletedMails();
			for(int i = 0; i < accounts.size(); i++) {
				MailAccount turn = accounts.get(i);
				setMailAccountService(turn);
				login();
				int sum = deleteMailsInSpecificPeriod(firstDay, lastDay);
				logout();
				value += sum; //변경완료
				
				//개별 account count 변경
				int accountValue = turn.getDeletedMails() + sum;
				turn.setDeletedMails(accountValue);
				mailAccountDao.mailAccountUpdateDeletedMails(turn);
			}
			
			//DB 업뎃
			user.setTotalDeletedMails(value);
			userDao.userUpdateDeletedMailsCnt(user);

			return;
		}
	}
	
	public void deleteMailsFromSpecificSender(User user, String sender) {
		List<MailAccount> accounts = getUserMailAccountAll(user);
		
		if(accounts == null || accounts.isEmpty()) {
			return;
		}else {
			int value = user.getTotalDeletedMails();
			for(int i = 0; i < accounts.size(); i++) {
				MailAccount turn = accounts.get(i);
				setMailAccountService(turn);
				login();
				int sum = deleteMailsFromSpecificSender(sender);
				logout();
				value += sum; //변경완료
				
				//개별 account count 변경
				int accountValue = turn.getDeletedMails() + sum;
				turn.setDeletedMails(accountValue);
				mailAccountDao.mailAccountUpdateDeletedMails(turn);
			}
			
			//DB 업뎃
			user.setTotalDeletedMails(value);
			userDao.userUpdateDeletedMailsCnt(user);
			return;
		}
	}
	
}
