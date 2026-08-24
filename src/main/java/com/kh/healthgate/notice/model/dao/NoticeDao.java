package com.kh.healthgate.notice.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.notice.model.vo.Notice;

public interface NoticeDao extends JpaRepository<Notice, Long> {

	// 공지사항 목록 조회
	// > SELECT * FROM NOTICE WHERE STATUS = ? ORDER BY NOTICE_ID DESC
	Page<Notice> findByStatusOrderByNoticeIdDesc(String status, Pageable pageable); 

	// 공지사항 검색
	// SELECT * FROM NOTICE
	// WHERE TITLE LIKE '%' || ? || '%' AND STATUS = ?
	//  ORDER BY NOTICE_ID DESC
	Page<Notice> findByTitleContainingAndStatusOrderByNoticeIdDesc(String keyword, String status, Pageable pageable);

	
	@Modifying
	@Query("""
			   UPDATE Notice n
				  SET n.count = n.count + 1
				WHERE n.noticeId = :noticeId
				  AND n.status ='Y'   
			""")
	int increaseCount(@Param("noticeId") Long noticeId);
    
	@Modifying
	@Query("""
			   UPDATE Notice n
			      SET n.status = 'N'
			    WHERE n.noticeId = :noticeId
			      AND n.status = 'Y'  
			""")
	int deleteNotice(@Param("noticeId") Long noticeId);
}
