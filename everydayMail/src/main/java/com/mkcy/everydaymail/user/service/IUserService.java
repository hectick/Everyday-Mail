package com.mkcy.everydaymail.user.service;

import com.mkcy.everydaymail.user.User;
import com.mkcy.everydaymail.user.UserBatchProcessingInfo;

public interface IUserService {
	
	void userRegister(String id, String pw); //회원가입
	void userDelete(String id, String pw); //회원탈퇴
	boolean isExistId(String id);//유저 찾기
	User userLoginCheck(String id, String pw); //로그인회원 체크
	void passwordUpdate(User user, String newPw); //비밀번호 변경
	void userBatchInfoUpdate(UserBatchProcessingInfo userinfo); //일괄처리 정보 변경
	UserBatchProcessingInfo userBatchInfoGet(String id); //일괄처리 정보 가져오기
	public void userMailCountReset(User user);//유저 메일카운트정보 초기화
}


