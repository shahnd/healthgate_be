package com.kh.healthgate.notice.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.notice.model.dao.NoticeDao;
import com.kh.healthgate.notice.model.dao.NoticeFileDao;
import com.kh.healthgate.notice.model.vo.Notice;
import com.kh.healthgate.notice.model.vo.NoticeFile;

@Service
public class NoticeService {

	@Autowired
	private NoticeDao noticeDao;
	
	@Autowired
	private NoticeFileDao noticeFileDao;
	
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
	public NoticeFile insertNoticeFile(NoticeFile nf) {
		
		return noticeFileDao.save(nf);
	}

    
	@Transactional
	public int increaseCount(Long noticeId) {
		
		return noticeDao.increaseCount(noticeId);
	}

	public Notice selectNotice(Long noticeId) {
		
		return noticeDao.findById(noticeId).orElse(null);
	}
	
	public NoticeFile selectNoticeFile(Long noticeId) {
	
		return noticeFileDao.selectNoticeFile(noticeId);
	}
	
	@Transactional
	public int deleteNotice(Long noticeId) {
		
		return noticeDao.deleteNotice(noticeId);
	}
    
	@Transactional
	public Notice updateNotice(Notice n) {
		
		return noticeDao.save(n);
	}
    
	@Transactional
	public NoticeFile updateNoticeFile(NoticeFile nf) {
		
		return noticeFileDao.save(nf);
	}

	public NoticeFile selectNoticeFileId(Long noticeFileId) {
		
		return noticeFileDao.selectNoticeFile(noticeFileId);
	}

}
