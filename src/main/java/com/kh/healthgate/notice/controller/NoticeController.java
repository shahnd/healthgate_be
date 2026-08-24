package com.kh.healthgate.notice.controller;


import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.common.model.vo.PageInfo;
import com.kh.healthgate.common.template.Pagination;
import com.kh.healthgate.notice.model.service.NoticeService;
import com.kh.healthgate.notice.model.vo.Notice;

import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
public class NoticeController {
    
	@Autowired
	public NoticeService noticeService;
	
	// 공지사항 목록 조회용 컨트롤러
	@GetMapping("/notices")
	public ResponseEntity<HashMap<String, Object>> selectNoticeList(
				@RequestParam(value="cpage", defaultValue="1") int currentPage) {
		
		
		int boardLimit = 5;
		int pageLimit = 5;
		
		Pageable pageable = PageRequest.of(currentPage - 1, 5);
		
		Page<Notice> page = noticeService.selectNoticeList(pageable);
	
		List<Notice> list = page.getContent();
		
		long listCount = page.getTotalElements();
	
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, 
													pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("list", list); 
		hm.put("pi", pi); 
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(hm);
	}
		
	// 공지사항 검색용 컨트롤러
	@GetMapping("/notices/search")
	public ResponseEntity<HashMap<String, Object>> searchNoticeList(
					@RequestParam(value="cpage", defaultValue="1") int currentPage,
					String keyword) {
		
		int boardLimit = 5;
		int pageLimit = 5;
		
	
		Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
		
	
		Page<Notice> page = noticeService.selectSearchList(keyword, pageable);
		
		List<Notice> list = page.getContent();
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)searchCount, currentPage, pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list", list);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(hm);
	}
	
	// 공지사항 작성용 컨트롤러
	@PostMapping("/notices")
	public ResponseEntity<String> insertNotice(Notice n) {
	
		n.setStatus("Y");
		
		// 서비스 호출
		Notice insertNo = noticeService.insertNotice(n);
		
		String message = (insertNo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	// 공지사항 상세조회용 컨트롤러
	@GetMapping("/notices/{noticeNo}")
	public ResponseEntity<Notice> selectNotice(@PathVariable Long noticeId) {
		
		// 조회수 증가
		int result = noticeService.increaseCount(noticeId);
		
		if(result > 0) {
			// > 조회수 증가에 성공한 경우
			
			Notice n = noticeService.selectNotice(noticeId);
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(n);
			
		} else {
			// > 조회수 증가에 실패한 경우
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(null);
		}
	}
	

	// 수정용 공지사항 상세 조회용 컨트롤러 - (다시 상세조회하면 조회수 증가될수있으므로)
	@GetMapping("/notices/{noticeId}/form")
	public ResponseEntity<Notice> selectNoticeForm(@PathVariable Long noticeId) {
		
		Notice n = noticeService.selectNotice(noticeId);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(n);
	}
	
	// 공지사항 수정용 컨트롤러
	@PostMapping("/notices/{noticeId}")
	public ResponseEntity<String> updateNotice(@PathVariable Long noticeId,
											  Notice n,
											  MultipartFile reupfile,
											  HttpSession session) {
		
		// 서비스 호출
		Notice updateNo = noticeService.updateNotice(n);
		
		String message = (updateNo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
	
	
	// 공지사항 삭제용 컨트롤러
	@DeleteMapping("/notices/{noticeId}")
	public ResponseEntity<String> deleteNotice(@PathVariable Long noticeId) {
		
		// 서비스 호출
		int result = noticeService.deleteNotice(noticeId);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}
}
