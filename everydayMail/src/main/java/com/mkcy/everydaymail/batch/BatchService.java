package com.mkcy.everydaymail.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mkcy.everydaymail.account.MailAccount;
import com.mkcy.everydaymail.account.dao.MailAccountDao;
import com.mkcy.everydaymail.account.service.IMAPEmailConnection;
import com.mkcy.everydaymail.user.User;
import com.mkcy.everydaymail.user.UserBatchProcessingInfo;
import com.mkcy.everydaymail.user.dao.UserDao;

@Repository
public class BatchService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	MailAccountDao accountDao;
	
	@Autowired
	User user;
	
	@Autowired
	UserDao userDao;
	
    public void deleteMailsContainingSpamWord(UserBatchProcessingInfo userInfo) throws InterruptedException {
    	//중요단어 on/off 확인
		String importantWord = null;
		String spamWord = userInfo.getSpamWord();
    	if(userInfo.getImportantWordSetting() == 1) {
			importantWord = userInfo.getImportantWord();
		}
    	
    	//사용자의 모든 계정 불러오기 및 중요단어 빼고 삭제
    	user = userDao.userSelect(userInfo.getId());
    	List<MailAccount> accounts = accountDao.selectMailAccountAll(user);
    	
    	for(int i= 0; i < accounts.size(); i++) {
    		MailAccount turn = accounts.get(i);
    		try {
    			IMAPEmailConnection mailConnection = new IMAPEmailConnection();
    			mailConnection.login(turn);
    			int tmp = mailConnection.deleteSpamWordMails(spamWord, importantWord);
    			mailConnection.logout();
    			
    			//user DB 업뎃
    			int value = user.getTotalDeletedMails();
    			user.setTotalDeletedMails(value + tmp);
    			userDao.userUpdateDeletedMailsCnt(user);
    			
        	    //account 디비 업뎃
        	    int accountValue = turn.getDeletedMails() + tmp;
        	    turn.setDeletedMails(accountValue);
    			accountDao.mailAccountUpdateDeletedMails(turn);
    			
    		}catch(Exception e) {
    			log.error("SPAM DELETE SERVICE ERROR");
    			e.printStackTrace();
    		}
 
    	}
    }
	
	
    public void deleteMailsExceededSpecificPeriod(UserBatchProcessingInfo userInfo) throws InterruptedException {
    	//중요단어 on/off 확인
    	String importantWord = null;
    	int numValue = userInfo.getTimeDeadline();
    	String unitValue = userInfo.getTimeDeadlineUnit();
    	if(userInfo.getImportantWordSetting() == 1) {
    		importantWord = userInfo.getImportantWord();
    	}
    	    	
    	//사용자의 모든 계정 불러오기 및 중요단어 빼고 삭제
    	user = userDao.userSelect(userInfo.getId());
    	List<MailAccount> accounts = accountDao.selectMailAccountAll(user);
    	    	
    	for(int i= 0; i < accounts.size(); i++) {
    	MailAccount turn = accounts.get(i);
    	try {
    	    IMAPEmailConnection mailConnection = new IMAPEmailConnection();
    	    mailConnection.login(turn);
    	    int tmp = mailConnection.deleteTimeMails(numValue, unitValue, importantWord);
    	    mailConnection.logout();
    	    			
    	    //user DB 업뎃
    	    int value = user.getTotalDeletedMails();
    	    user.setTotalDeletedMails(value + tmp);
    	    userDao.userUpdateDeletedMailsCnt(user);
    	    
    	    //account 디비 업뎃
    	    int accountValue = turn.getDeletedMails() + tmp;
    	    turn.setDeletedMails(accountValue);
			accountDao.mailAccountUpdateDeletedMails(turn);
    	    			
    	    }catch(Exception e) {
    	    	log.error("TIME DELETE SERVICE ERROR");
    	    	e.printStackTrace();
    	    }
    	 
    	}
    }
}
