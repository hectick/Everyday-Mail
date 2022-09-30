package com.mkcy.everydaymail.account.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

import com.mkcy.everydaymail.account.AdvertisingSender;
import com.mkcy.everydaymail.account.MailAccount;

public class IMAPEmailConnection {
	private Session session;
	private Store store;
	private Folder folder;
	private String protocol = "imaps";
	private String file = "INBOX";
	
	private MailAccount account;
	private List<AdvertisingSender> adSenderList;
	private List<AdvertisingSender> newAdSenderList;
	private List<String> unsubscribeWordsList;
	
	public IMAPEmailConnection() {
		//리스트 초기화
		adSenderList = new ArrayList<AdvertisingSender>();
		newAdSenderList = new ArrayList<AdvertisingSender>();
		unsubscribeWordsList = new ArrayList<String>();
	}
	
	public void setUnsubscribeWordsList(List<String> list) {
		unsubscribeWordsList = list;
	}
	
	public void setAdSenderList(List<AdvertisingSender> list) {
		adSenderList = list;
	}
	
	public List<AdvertisingSender> getNewadSenderList(){
		return newAdSenderList;
	}
	
	public List<AdvertisingSender> getAdSenderList() {
		return adSenderList;
	}
	
	public boolean isLoggedIn() {
		return store.isConnected();
	}
	
	public void login(MailAccount mailAccount) throws Exception{
		this.account = mailAccount;
		
		URLName url = new URLName(protocol, account.getMailHost(), 993, file, account.getMailAddr(), account.getMailPw());
		if (session == null) {
			Properties props = null;
			try {
				props = System.getProperties();
			} catch (SecurityException sex) {
				props = new Properties();
			}
			session = Session.getInstance(props, null);
		}
		store = session.getStore(url);
		store.connect();
		folder = store.getFolder("inbox"); 
		folder.open(Folder.READ_WRITE); 
	}
	
	public void logout() throws MessagingException {
		folder.close(false);
		store.close();
		store = null;
		session = null;
	}
	
	public int getMessageCount() {
		int messageCount = 0;
		try {
			messageCount = folder.getMessageCount();
		} catch (MessagingException me) {
			me.printStackTrace();
		}
		return messageCount;
	}
	
	public Message[] getMessages(boolean onlyNotRead) throws MessagingException{
		if(onlyNotRead) {
			return folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			
		}else {
			return folder.getMessages();
		}
	}
	
	public String getMailText(Message message) throws MessagingException{
		String resContent = "";
		String contentType = message.getContentType();
		try {
			if (contentType.contains("multipart")) {
	            Multipart multiPart = (Multipart) message.getContent();
	            int numberOfParts = multiPart.getCount();
	            for (int partCount = 0; partCount < numberOfParts; partCount++) {
	                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
	                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
	                    continue;
	                } else {
	                    resContent = resContent + "\n" + part.getContent().toString();
	                }
	            }
	        } else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
	            Object content = message.getContent();
	            if (content != null) {
	                resContent = content.toString();
	            }
	        }
		}catch(IOException ie) {
			ie.printStackTrace();
		}
		
		return resContent;
	}
	
	public int deleteAllMails() throws MessagingException {
		int deleteCount = 0;
		if (folder.isOpen()) {    
		    Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				messages[i].setFlag(Flags.Flag.DELETED, true);
				deleteCount++;
			}
			folder.expunge();
		}
		return deleteCount;
	}
	
	public int deleteAdvertisingMails() throws MessagingException {
		int deleteCount = 0;
		if (folder.isOpen()) {
		    Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				if(messages[i].getSubject().contains("광고)")) {
					//광고 송신자의 정보 저장(송신자 이름, 메일주소, 구독취소링크)
					extractInformationFromAdvertisingMail(messages[i]);
					
					//삭제표시
					messages[i].setFlag(Flags.Flag.DELETED, true);
					deleteCount++;
				}
			}
			folder.expunge();
		}
		return deleteCount;
	}
	
	private void extractInformationFromAdvertisingMail(Message message) throws MessagingException{

		//광고 송신자 정보 뽑기
		Address sendaddr[] = message.getFrom();
		InternetAddress addr = null;
		if ((sendaddr != null) && (sendaddr.length > 0)) {
			addr = (InternetAddress)sendaddr[0];
			String adSenderName = addr.getPersonal();
			String adSenderAddr = addr.getAddress();
			
			Boolean exist = false;
			int listIndex = -1;
			
			//광고 송신자가 이미 존재하는지 확인
			if(!adSenderList.isEmpty() && adSenderList != null) {
				for(int i = 0; i< adSenderList.size(); i++) {
					AdvertisingSender sender = adSenderList.get(i);
					String compareName = sender.getSenderName();
					String compareAddr = sender.getSenderAddr();
					if(compareAddr.equals(adSenderAddr) && compareName.equals(adSenderName)) {
						exist = true;
						listIndex = i;
						break;
					}
				}
			}
			
			if(exist == true) {
				//광고 송신자가 이미 존재할경우
				AdvertisingSender adSender = adSenderList.get(listIndex);
				int adMailCount = adSender.getMailCount() + 1;
				adSenderList.get(listIndex).setMailCount(adMailCount);
				
				//구독링크가 존재하지 않으면 구독링크 추가
				if(adSender.getUnsubscribeLink() == null) {
					String content = getMailText(message);
					String result = extractLinkFromContent(content);
					if(result != "") {
						adSenderList.get(listIndex).setUnsubscribeLink(result);
					}		
				}
				
			}else {
				//존재하지 않으면 새로 추가
				AdvertisingSender adSender = new AdvertisingSender();
				adSender.setId(account.getId());
				adSender.setMailAddr(account.getMailAddr());
				adSender.setMailCount(1);
				
				//광고 송신자의 이름과 주소를 adSender에 저장
				adSender.setSenderName(adSenderName);
				adSender.setSenderAddr(adSenderAddr);
				
				//구독해제링크 탐색 및 추가 : 메세지 가져오기
				String content = getMailText(message);
				String result = extractLinkFromContent(content);
				if(result == "") {
					adSender.setUnsubscribeLink("");
				}else {
					adSender.setUnsubscribeLink(result);
				}
				newAdSenderList.add(adSender);
				adSenderList.add(adSender);
			}
		}

	}
	
	public String extractLinkFromContent(String content) {
		String link = "";
		
		//일단 메일 내용에 구독해지 관련 내용이 있는지 확인하고, 있다면 그때 링크 검사
		int cnt = 0;
		int max = unsubscribeWordsList.size();
		boolean check = false;
		while(cnt < max) {
			if(content.contains(unsubscribeWordsList.get(cnt))) {
				check = true;
				break;
			}
			cnt++;
		}
		
		//check==true 라면  링크 세부 검사 실행
		List<String> tagList = new ArrayList<String>();
		if(check == true) {
			//<a href = "…"> … </a> 태그로 감싸진 엘리먼트를 뽑아 배열로 만들고
			Pattern regex = Pattern.compile("<a.*href.*>.*?</a>");
			Matcher matcher = regex.matcher(content);
			while(matcher.find()) {
				String tag = matcher.group();
				tagList.add(tag);
			}
		}
		
		if(tagList.isEmpty()) return link;
		else {
			unsubscribeWordsList.add("여기");
			unsubscribeWordsList.add("here");
			//태그 안의 텍스트가 구독 취소와 관련된 내용이라면 저장
			int tagCnt = 0;
			while(tagCnt < tagList.size()) {
				String text = removeTag(tagList.get(tagCnt));
				for(int i = cnt; i < max+2; i++) {
					String words = unsubscribeWordsList.get(i);
					if(text.contains(words)) {
						//링크 추출 https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]
						Pattern p = Pattern.compile("https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
					    Matcher m = p.matcher(tagList.get(tagCnt));
					    if (m.find()) {
					    	link = m.group();
					    	//System.out.println("링크추출완료" + link); //확인용 - 삭제해야함
					    	return link;
				        }
					}
				}
				tagCnt++;
			}
		}
		return link;
	}
	
	private String removeTag(String html){
		return html.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}
	
	public int deleteMailsContainingSpecificText(String specificText) throws MessagingException {
		int deleteCount = 0;
		if (folder.isOpen()) {    
		    Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				String content = getMailText(messages[i]);
				if(content.contains(specificText)) {
					messages[i].setFlag(Flags.Flag.DELETED, true);
					deleteCount++;
				}
			}
			folder.expunge();
		}
		return deleteCount;
	}
	
	public int deleteMailsFromSpecificSender(String specificSender) throws MessagingException {
		int deleteCount = 0;
		if (folder.isOpen()) {    
		    Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				//메일 발신자 분석
				Address sendaddr[] = messages[i].getFrom();
				InternetAddress addr = null;
				if ((sendaddr != null) && (sendaddr.length > 0)) {
				addr = (InternetAddress)sendaddr[0];
				}
				String senderName = addr.getPersonal();
				String senderAddr = addr.getAddress();
				if(senderName == null) {
					senderName = "";
				}
				if(senderAddr == null) {
					senderAddr = "";
				}
				
				if(senderName.contains(specificSender) || senderAddr.contains(specificSender)) {
					messages[i].setFlag(Flags.Flag.DELETED, true);
					deleteCount++;
				}
			}
			folder.expunge();
		}
		return deleteCount;
	}
	
	public int deleteMailsInSpecificPeriod(String firstDay, String lastDay) throws MessagingException {
		int deleteCount = 0;

		Date startDate;
		Date endDate;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = dateFormat.parse(firstDay);
			endDate = dateFormat.parse(lastDay);
			
			if (folder.isOpen()) {    
			    Message[] messages = folder.getMessages();
				for (int i = 0; i < messages.length; i++) {
					String date = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(messages[i].getReceivedDate());
					Date receivedDate = dateFormat.parse(date);
					
					int compare1 = receivedDate.compareTo(startDate);
					int compare2 = receivedDate.compareTo(endDate);
					
					if(compare1 < 0) { //receivedDate < startDate
						continue;
					}else if(compare1 == 0){ //startDate == receivedDate
						messages[i].setFlag(Flags.Flag.DELETED, true);
						deleteCount++;
					}else if(compare1 > 0 && compare2 < 0) { //startDate < receivedDate < endDate
						messages[i].setFlag(Flags.Flag.DELETED, true);
						deleteCount++;
					}else if(compare2 == 0) { // receivedDate == endDate
						messages[i].setFlag(Flags.Flag.DELETED, true);
						deleteCount++;
						break;
					}else { //endDate < receivedDate
						break;
					}
				}
				folder.expunge();

			}
		}catch(ParseException pe) {
			pe.printStackTrace();
		}
		return deleteCount;
	}
	
	
	/*테스트용 메소드*/
	public void readMails() throws MessagingException {
		
		if (folder.isOpen()) {    
		    Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				String content = getMailText(messages[i]);
				System.out.println(content);
			}

		}
	}
	
	/*일괄처리용 메소드*/
	public int deleteSpamWordMails(String spamWord, String importantWord) throws MessagingException {
		int deleteCount = 0;
		
		if(importantWord != null) { //중요단어설정이 되어 있다면 제외하고 삭제해야 함
			if (folder.isOpen()) {
			    Message[] messages = folder.getMessages();
				for (int i = 0; i < messages.length; i++) {
					String subject = messages[i].getSubject();
					String content = getMailText(messages[i]);
					if(!(subject.contains(importantWord)) && !(content.contains(importantWord))) {
						if(subject.contains(spamWord) || content.contains(spamWord)) {
							messages[i].setFlag(Flags.Flag.DELETED, true);
							deleteCount++;
						}
					}
				}
				folder.expunge();
			}
		}else {
			if (folder.isOpen()) {
			    Message[] messages = folder.getMessages();
				for (int i = 0; i < messages.length; i++) {
					String subject = messages[i].getSubject();
					String content = getMailText(messages[i]);
					if(subject.contains(spamWord) || content.contains(spamWord)) {
						messages[i].setFlag(Flags.Flag.DELETED, true);
						deleteCount++;
					}
				}
				folder.expunge();
			}
		}
		return deleteCount;
		
	}
	
	public int deleteTimeMails(int numValue, String unitValue, String importantWord) throws MessagingException, ParseException{
		int deleteCount = 0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		String nowDate = dateFormat.format(now);
		
		if(importantWord != null) { //중요단어설정이 되어 있다면 제외하고 삭제해야 함
			if (folder.isOpen()) {
			    Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
				for (int i = 0; i < messages.length; i++) {
					String subject = messages[i].getSubject();
					String content = getMailText(messages[i]);
					if(!(subject.contains(importantWord)) && !(content.contains(importantWord))) {
						Calendar cal = Calendar.getInstance();
						String date1_str = dateFormat.format(messages[i].getReceivedDate()); //메일 날짜
						Date date1 = dateFormat.parse(date1_str);
						cal.setTime(date1);
						
						if(unitValue.equals("day")) {
							cal.add(Calendar.DATE, numValue);
						}else if(unitValue.equals("week")) {
							cal.add(Calendar.DATE, numValue*7);
						}else if(unitValue.equals("month")) {
							cal.add(Calendar.MONTH, numValue);
						}else if(unitValue.equals("year")) {
							cal.add(Calendar.YEAR, numValue);
						}
							
						String date2 = dateFormat.format(cal.getTime()); //메일 날짜 + numValue 
						int compare = date2.compareTo(nowDate); //메일 날짜 + days <= now 면 삭제 date2 -> now 차례면 삭제
						if(compare == 0 || compare < 0) {//시간기한이 지났는지 체크
							messages[i].setFlag(Flags.Flag.DELETED, true);
							deleteCount++;
						}else if(compare > 0) {
							break;
						}
					}
				}
				folder.expunge();
			}
		}else {
			if (folder.isOpen()) {
				Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
				for (int i = 0; i < messages.length; i++) {
					Calendar cal = Calendar.getInstance();
					String date1_str = dateFormat.format(messages[i].getReceivedDate()); //메일 날짜
					Date date1 = dateFormat.parse(date1_str);		
					cal.setTime(date1);

					if(unitValue.equals("date")) {
						cal.add(Calendar.DATE, numValue);
					}else if(unitValue.equals("week")) {
						cal.add(Calendar.DATE, numValue*7);
					}else if(unitValue.equals("month")) {
						cal.add(Calendar.MONTH, numValue);
					}else if(unitValue.equals("year")) {
						cal.add(Calendar.YEAR, numValue);
					}
					
					String date2 = dateFormat.format(cal.getTime()); //메일 날짜 + numValue 
					int compare = date2.compareTo(nowDate); //메일 날짜 + days <= now 면 삭제 date2 -> now 차례면 삭제
					if(compare == 0 || compare < 0) {//시간기한이 지났는지 체크
						messages[i].setFlag(Flags.Flag.DELETED, true);
						deleteCount++;
					}else if(compare > 0) {
						break;
					}
				}
				folder.expunge();
			}
		}
		return deleteCount;
	}
}
