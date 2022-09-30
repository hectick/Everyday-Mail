package com.mkcy.everydaymail.user.controller;

import java.util.HashMap;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mkcy.everydaymail.user.User;
import com.mkcy.everydaymail.user.UserBatchProcessingInfo;
import com.mkcy.everydaymail.user.service.UserService;

@RestController
@RequestMapping(value="/api")
public class UserController {

	@Resource(name="userService")
	UserService service;
	
	//회원가입
	@RequestMapping(value="/signup/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> signUp(@RequestBody HashMap<String, Object> requestData) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String id = (String)requestData.get("id");
		String password = (String)requestData.get("password");
		
		//존재하지 않는 아이디인지 체크
		boolean isExist = service.isExistId(id);
		
		if(isExist == false) {
			//회원가입 가능
			service.userRegister(id, password);
			map.put("success", true);
		}else {
			//회원가입 불가
			map.put("success", false);
			map.put("errorMessage", "이미 존재하는 아이디입니다.");
		}
		return map;
	}
	
	//회원탈퇴
	@RequestMapping(value="/withdraw/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> withdraw(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		//탈퇴시 비밀번호 입력받아서 비교
		String password = (String)requestData.get("password");

		if(user.getPassword().equals(password)) {
			//회원탈퇴 가능
			service.userDelete(user.getId(), password);
			session.invalidate();
			map.put("success", true);
		}else {
			//탈퇴 불가능
			map.put("success", false);
			map.put("errorMessage", "비밀번호가 일치하지 않습니다.");
			
		}
		return map;
	}
	
	//로그인
	@RequestMapping(value="/login/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> login(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		String id = (String)requestData.get("id");
		String password = (String)requestData.get("password");
		
		User user = service.userLoginCheck(id, password);
		
		if(user != null) {
			//로그인 성공 시 -> 세선 생성
			//세션 유지시간 설정 
			session.setAttribute("user", user);
			session.setMaxInactiveInterval(1800); // 1800 = 60s*30 (30분)
			String sessionId = session.getId();
			map.put("success", true);
			map.put("JSESSIONID", sessionId);
		}else {
			//로그인 실패 시
			map.put("success", false);
			map.put("errorMessage", "아이디 또는 비밀번호를 확인해 주세요.");
		}

		return map;
	}

	//로그아웃
	@RequestMapping(value="/logout/", method=RequestMethod.POST, produces = "application/json; charset=utf8") 
	public Map<String, Object> logout(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		session.invalidate();
		
		map.put("success", true);
		
		return map;
	}
	
	//현재 로그인중인 사용자정보 확인
	@RequestMapping(value="/myinfo/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> getUser(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		User renewedUser = service.userLoginCheck(user.getId(), user.getPassword());
		
		map.put("success", true);
		map.put("data", renewedUser);

		return map;
	}
	
	//대시보드 메일 수 가져오기
	@RequestMapping(value="/dashboard/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> getDashboard(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		User renewedUser = service.userLoginCheck(user.getId(), user.getPassword());
		
		map.put("success", true);
		map.put("deletedMails", renewedUser.getTotalDeletedMails());

		return map;
	}
	
	
	//비밀번호 변경
	@RequestMapping(value="/myinfo/changepw/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> updatePassword(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		String currentPassword = (String)requestData.get("currentPassword");
		String newPassword = (String)requestData.get("newPassword");
		if(!currentPassword.equals(user.getPassword())) {
			map.put("success", false);
			map.put("errorMessage", "현재 비밀번호가 일치하지 않습니다.");
			return map;
		}
		service.passwordUpdate(user, newPassword);
		map.put("success", true);
		return map;
	}
	
	//배치정보 저장
	@RequestMapping(value="/myinfo/setting/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> setBatchProcessingService(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		Map<String, Object> spamData = (Map<String, Object>) requestData.get("spam");
		int spamStatus = (int) spamData.get("status");
		String spamValue = (String) spamData.get("value");
		
		Map<String, Object> timeData = (Map<String, Object>) requestData.get("time");
		int timeStatus = (int) timeData.get("status");
		int timeNumValue = (int) timeData.get("numValue");
		String timeUnitValue = (String) timeData.get("unitValue");
		
		Map<String, Object> wordData = (Map<String, Object>) requestData.get("word");
		int wordStatus = (int) wordData.get("status");
		String wordValue = (String) wordData.get("value");
		
		UserBatchProcessingInfo userInfo = new UserBatchProcessingInfo();
		userInfo.setId(user.getId());
		userInfo.setSpamWord(spamValue);
		userInfo.setImportantWord(wordValue);
		
		if(timeUnitValue.contains("day")) {
			userInfo.setTimeDeadline(timeNumValue);
			userInfo.setTimeDeadlineUnit("day");
		}else if(timeUnitValue.contains("week")) {
			userInfo.setTimeDeadline(timeNumValue);
			userInfo.setTimeDeadlineUnit("week");
		}else if(timeUnitValue.contains("month")) {
			userInfo.setTimeDeadline(timeNumValue);
			userInfo.setTimeDeadlineUnit("month");
		}else if(timeUnitValue.contains("year")) {
			userInfo.setTimeDeadline(timeNumValue);
			userInfo.setTimeDeadlineUnit("year");
		}
		
		
		userInfo.setImportantWordSetting(wordStatus);
		userInfo.setSpamWordSetting(spamStatus);
		userInfo.setTimeDeadlineSetting(timeStatus);
		
		service.userBatchInfoUpdate(userInfo);
		
		map.put("success", true);

		return map;
		
		
	}
	
	//배치정보 가져오기
	@RequestMapping(value="/myinfo/setting/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> getBatchInfo(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		UserBatchProcessingInfo userInfo = service.userBatchInfoGet(user.getId());
		
		Map<String, Object> spam = new HashMap<String, Object>();
		Map<String, Object> time = new HashMap<String, Object>();
		Map<String, Object> word = new HashMap<String, Object>();
		spam.put("value", userInfo.getSpamWord());
		spam.put("status", userInfo.getSpamWordSetting());
		
		time.put("numValue", userInfo.getTimeDeadline());
		time.put("unitValue", userInfo.getTimeDeadlineUnit());
		time.put("status", userInfo.getTimeDeadlineSetting());
		
		word.put("value", userInfo.getImportantWord());
		word.put("status", userInfo.getImportantWordSetting());
		
		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put("spam", spam);
		subMap.put("time", time);
		subMap.put("word", word);
		map.put("setting", subMap);
		map.put("success", true);

		return map;
	}
	
	//삭제메일갯수 초기화(서버테스트용)
	@RequestMapping(value="/reset/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> resetMailAccountCountTotal(HttpSession session) {
			
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		service.userMailCountReset(user);
		
		map.put("success", true);
		return map;
	}

}
