package com.kh.healthgate.notice.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.notice.model.dao.NoticeDao;
import com.kh.healthgate.notice.model.vo.Notice;

@Service
public class NoticeService {

	@Autowired
	private NoticeDao noticeDao;
	
	public Page<Notice> selectNoticeList(Pageable pageable) {
		
		return noticeDao.findByStatusOrderByNoticeIdDesc("Y", pageable);
	}

	public Page<Notice> selectSearchList(String keyword, Pageable pageable) {
		
		return noticeDao.findByTitleContainingAndStatusOrderByNoticeIdDesc(keyword,"Y",pageable);
	}
	
	@Transactional
	public Notice insertNotice(Notice n) {
		
		return noticeDao.save(n);
	}
    
	@Transactional
	public int increaseCount(Long noticeId) {
		
		return noticeDao.increaseCount(noticeId);
	}

	public Notice selectNotice(Long noticeId) {
		
		return noticeDao.findById(noticeId).orElse(null);
	}
	
	@Transactional
	public int deleteNotice(Long noticeId) {
		
		return noticeDao.deleteNotice(noticeId);
	}
    
	@Transactional
	public Notice updateNotice(Notice n) {
		
		return noticeDao.save(n);
	}

}
