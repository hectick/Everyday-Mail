package com.mkcy.everydaymail.account.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mkcy.everydaymail.account.AdvertisingSender;
import com.mkcy.everydaymail.account.service.MailAccountService;
import com.mkcy.everydaymail.user.User;

@RestController
@RequestMapping(value="/api/account")
public class MailAccountController {
	@Resource(name="mailAccountService")
	MailAccountService service;
	
	//메일 계정 추가
	@RequestMapping(value="/register/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> addMailAccount(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
	//받아야 할 정보들 : index_id, mail_host, mail_addr, mail_pw, register_date, count_deleted_mails	
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인 세션 만료");
			return map;
		}
			
		String id = user.getId();
		String mailAddr = (String)requestData.get("mailAddr");
		String mailPw = (String)requestData.get("mailPw");
			
		//mailHost 추가
		String mailHost = "";
		if(mailAddr.contains("@naver.com")) {
			mailHost = "imap.naver.com";
		}else if(mailAddr.contains("@gmail.com")) {
			mailHost = "imap.gmail.com";
		}else if(mailAddr.contains("@daum.net")) {
			mailHost = "imap.daum.net";
		}else if(mailAddr.contains("@nate.com")) {
			mailHost = "imap.nate.com";
		}else if(mailAddr.contains("@kakao.com")) {
			mailHost = "imap.kakao.com";
		}
			
		//mail addr이 디비에 존재하는지 검증
		if(service.isExistMail(mailAddr) == true) {
			map.put("success", false);
			map.put("errorMessage", "사이트에 이미 등록된 계정입니다.");
			return map;
		}
				
		//mail이 유효한지 검증
		boolean valid = service.checkMailValidation(mailHost, mailAddr, mailPw);
		if(valid == false) {
			map.put("success", false);
			map.put("errorMessage", "메일함에 접속할 수 없습니다. 메일주소, 비밀번호, imap 설정을 확인해 주세요.");
			return map;
		}
				
		//계정 등록
		service.addMailAccount(id, mailHost, mailAddr, mailPw);
		map.put("success", true);
		
		return map;
	}
	
	//계정들 목록 불러오기 
	@RequestMapping(value="/accounts/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> getAccountList(HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
			
		List<String> accountAddrs = null;
		accountAddrs = service.getUserMailAccountsAddressAll(user);
			
		if(accountAddrs == null || accountAddrs.isEmpty()) {
			accountAddrs = new ArrayList<String>();
			map.put("accountsList", accountAddrs);
			map.put("success", true);
			return map;
		}else {
			map.put("accountsList", accountAddrs);
			map.put("success", true);
			return map;
		}
	}
	
	
	//메일 스캔
	@RequestMapping(value="/scanning/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> scanMailAccounts(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		int scanMode = (int)requestData.get("scanningMode");
		if(scanMode ==  1) {
			//광고메일 삭제
			service.deleteAdvertisingMails(user);
			map.put("success", true);
		}else if(scanMode == 2) {
			//특정 기간 삭제
			List<String> date = (List<String>)requestData.get("value");
			String startDate = date.get(0);
			String endDate = date.get(1);
			service.deleteMailsInSpecificPeriod(user, startDate, endDate);
			map.put("success", true);
			
		}else if(scanMode == 3) {
			//특정 발신자 삭제
			String sender = (String) requestData.get("value");
			service.deleteMailsFromSpecificSender(user, sender);
			map.put("success", true);
			
		}else if(scanMode == 4) {
			//특정 문자열 삭제
			String text = (String) requestData.get("value");
			service.deleteMailsContainingSpecificText(user, text);
			map.put("success", true);
			
		}else {
			map.put("success", false);
			map.put("errorMessage", "MODE ERROR");
		}

		return map;
	}
	
	
	//광고메일정보 가져오기
	@RequestMapping(value="/{email:.+}/", method=RequestMethod.GET, produces = "application/json; charset=utf8")
	public Map<String, Object> getAdvertisingSenders(@PathVariable String email, HttpSession session) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		List<AdvertisingSender> adSenders = service.getAdvertisingSender(user.getId(), email);
		if(adSenders.isEmpty() || adSenders == null) {
			adSenders = new ArrayList<AdvertisingSender>();
			map.put("adSender", adSenders);
			map.put("success", true);
			return map;
		}else {
			map.put("adSender", adSenders);
			map.put("success", true);
			return map;
		}	
	}
	
	//메일계정 삭제
	@RequestMapping(value="/delete/", method=RequestMethod.POST, produces = "application/json; charset=utf8")
	public Map<String, Object> deleteMailAccount(@RequestBody HashMap<String, Object> requestData, HttpSession session) {
			
		Map<String, Object> map = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			map.put("success", false);
			map.put("errorMessage", "로그인이 필요합니다.");
			return map;
		}
		
		String email = (String)requestData.get("email");
		service.deleteMailAccount(user.getId(), email);
		
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
		
		List<String> accountAddrs = new ArrayList<String>();
		accountAddrs = service.getUserMailAccountsAddressAll(user);
		for(int i = 0; i < accountAddrs.size(); i++) {
			service.resetMailAccountCount(user.getId(), accountAddrs.get(i));
		}
		
		map.put("success", true);
		return map;
	}
}
