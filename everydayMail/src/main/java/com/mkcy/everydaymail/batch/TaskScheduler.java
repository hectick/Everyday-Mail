package com.mkcy.everydaymail.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mkcy.everydaymail.user.UserBatchProcessingInfo;
import com.mkcy.everydaymail.user.dao.UserBatchProcessingInfoDao;

@Component
public class TaskScheduler {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	UserBatchProcessingInfoDao batchDao;
	
	@Autowired
	BatchService service;
	
	//매일 새벽 2시에 실행(서버시간으로)
	@Scheduled(cron="0 0 2 * * *")
    public void MailDeleteSchduler() throws InterruptedException {

		log.info("BATCH SERVICE START");
		//user 일괄처리 정보 가져오기
		List<UserBatchProcessingInfo> batchList = batchDao.userBatchProcessingInfoSelectAll();
		
		//on/off 확인 및 아래함수들로 보내기
		for(int i = 0; i < batchList.size(); i++) {
			UserBatchProcessingInfo turn = batchList.get(i);
			
			if(turn.getTimeDeadlineSetting() == 1) {
				service.deleteMailsExceededSpecificPeriod(turn);
			}
			
			if(turn.getSpamWordSetting() == 1) {
				service.deleteMailsContainingSpamWord(turn);
			}
		}
		log.info("BATCH SERVICE END");
		
    }

}
