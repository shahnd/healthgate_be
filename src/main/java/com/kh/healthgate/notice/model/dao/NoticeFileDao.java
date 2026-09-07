package com.kh.healthgate.notice.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.notice.model.vo.NoticeFile;

public interface NoticeFileDao  extends JpaRepository<NoticeFile, Long>{

	@Query("""
			  SELECT nf 
			    FROM NoticeFile nf 
			  WHERE nf.notices.noticeId = :noticeId
			""")
	NoticeFile selectNoticeFile(@Param("noticeId") Long noticeId); 
	
	@Query("""
			  SELECT nf 
			    FROM NoticeFile nf 
			  WHERE nf.noticeFileId = :noticeFileId
			""")
	NoticeFile selectNoticeFileId(@Param("noticeFileId") Long noticeFileId); 
}
