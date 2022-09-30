package com.mkcy.everydaymail.user.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mkcy.everydaymail.account.MailAccount;
import com.mkcy.everydaymail.account.dao.AdvertisingSenderDao;
import com.mkcy.everydaymail.account.dao.MailAccountDao;
import com.mkcy.everydaymail.user.User;
import com.mkcy.everydaymail.user.UserBatchProcessingInfo;
import com.mkcy.everydaymail.user.dao.UserBatchProcessingInfoDao;
import com.mkcy.everydaymail.user.dao.UserDao;


@Repository("userService")
public class UserService implements IUserService{
	@Autowired
	UserDao userDao;
	
	@Autowired
	User user;
	
	@Autowired
	MailAccountDao accountDao;
	
	@Autowired
	MailAccount account;
	
	@Autowired
	UserBatchProcessingInfo userBatchInfo;
	
	@Autowired
	UserBatchProcessingInfoDao userBatchInfoDao;
	
	@Autowired
	AdvertisingSenderDao adSenderDao;
	
	/*회원가입*/
	@Override
	public void userRegister(String id, String pw) {
		//유저정보 세팅
		user.setId(id);
		user.setPassword(pw);
		user.setTotalDeletedMails(0);
		user.setAccountsCnt(0);
		userDao.userInsert(user);
		
		//배치서비스 기본정보 세팅
		userBatchInfo.setId(id);
		userBatchInfo.setImportantWordSetting(0);
		userBatchInfo.setTimeDeadlineSetting(0);
		userBatchInfo.setSpamWordSetting(0);
		userBatchInfoDao.userBatchProcessingInfoInsert(userBatchInfo);
	}
	
	/*아이디 중복 검사*/
	@Override
	public boolean isExistId(String id){
		User findUser = userDao.userSelect(id);
		if(findUser != null) {
			return true;
		}else {
			return false;
		}
	}
	
	/*로그인 유저정보 반환 or 로그인 안되어 있으면 null 반환*/
	@Override
	public User userLoginCheck(String id, String pw) {
		user.setId(id);
		user.setPassword(pw);
		
		User findUser = userDao.userSelect(user);
		
		return findUser;
	
	}
	
	/*회원탈퇴*/
	@Override
	public void userDelete(String id, String pw) {
		
		user.setId(id);
		user.setPassword(pw);
		
		//user 및 연관정보 삭제
		userDao.userDelete(user);
		userBatchInfoDao.userBatchProcessingInfoDelete(id);
		List<MailAccount> accounts = accountDao.selectMailAccountAll(user);
		if(accounts != null && !(accounts.isEmpty())) {
			for(int i = 0; i < accounts.size(); i++) {
				adSenderDao.advertisingSenderDeleteAllOfMailAddr(accounts.get(i));
				accountDao.mailAccountDelete(accounts.get(i));
			}
		}
		
	}
	
	/*비번 업뎃*/
	@Override
	public void passwordUpdate(User user, String newPw) {
		user.setPassword(newPw);
		userDao.userUpdatePassword(user);
	}
	
	/*배치정보 업뎃*/
	@Override
	public void userBatchInfoUpdate(UserBatchProcessingInfo userinfo) {
		userBatchInfoDao.userBatchProcessingInfoUpdate(userinfo);
	}
	
	/*배치정보 가져오기*/
	@Override
	public UserBatchProcessingInfo userBatchInfoGet(String id) {
		return userBatchInfoDao.userBatchProcessingInfoSelect(id);
	}
	
	/*개발용*/
	@Override
	public void userMailCountReset(User user) {
		user.setTotalDeletedMails(0);
		userDao.userUpdateDeletedMailsCnt(user);
	}

}
