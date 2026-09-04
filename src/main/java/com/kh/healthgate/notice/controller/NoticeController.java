package com.kh.healthgate.notice.controller;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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
import com.kh.healthgate.employee.model.service.EmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.notice.model.service.NoticeService;
import com.kh.healthgate.notice.model.vo.Notice;
import com.kh.healthgate.notice.model.vo.NoticeFile;
import com.kh.healthgate.notice.util.NoticeSaveFile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@CrossOrigin
@RestController
public class NoticeController {
    
	public static final String SECRET_KEY = "Hello123ThisisHellPangWeWantToBreakTime";
	
	@Autowired
	public NoticeService noticeService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private NoticeSaveFile noticeSaveFile;
	 
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
	@PostMapping("/notices/new")
	public ResponseEntity<String> insertNotice(Notice n, 
			                                   MultipartFile upfile, 
											   HttpServletRequest request) {
		// 작성자 (로그인한 회원) 정보 뽑기
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 존재하지 않습니다.");
		}
		
		String jwtTokenString = authHeader.substring(7).trim();
		
		// 토큰 값이 empty, "null", "undefined"인 경우 예외 발생 전에 사전 차단
		if (jwtTokenString.isEmpty() || "null".equalsIgnoreCase(jwtTokenString) || "undefined".equalsIgnoreCase(jwtTokenString)) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
		}
		
		Claims claims;
		try {
		Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		} catch (Exception e) {
			// 잘못된 JWT 형식이 들어와도 500 에러 대신 401 Unauthorized 반환
			e.printStackTrace();
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 검증 실패");
		}
		
		Long id = claims.get("id", Long.class);
		
		Employee emp = employeeService.selectEmployee(id);
		
		n.setEmployee(emp);
		
        n.setStatus("Y");
		
		// 서비스 호출
		Notice insertNo = noticeService.insertNotice(n);
		
		// 넘어온 첨부파일이 있을 경우
		// > 파일명 수정작업 후 서버로 업로드 (공통코드), originName, savedName, extension, savedPath 필드값을 셋팅
		if(upfile != null && !upfile.isEmpty()) {
		    
			
			NoticeFile nf = noticeSaveFile.saveFile(upfile);
			
			if (nf != null) {
		        nf.setNotices(insertNo); // 게시글 번호 매핑
		        noticeService.insertNoticeFile(nf);
		    }
		}
		
		String message = (insertNo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
		
	}
	
	// 공지사항 상세조회용 컨트롤러
	@GetMapping("/notices/{noticeId}")
	public ResponseEntity<HashMap<String, Object>> selectNotice(@PathVariable Long noticeId) {
		
		// 조회수 증가
		int result = noticeService.increaseCount(noticeId);
		
		if(result > 0) {
			// > 조회수 증가에 성공한 경우
			
			Notice n = noticeService.selectNotice(noticeId);
			
			NoticeFile nf = noticeService.selectNoticeFile(noticeId);
			
			// DB 조회 결과 첨부파일이 없어서 null인 경우 빈 객체 생성
			if (nf == null) {
			    nf = new NoticeFile();
			}
			
			// Map 객체 생성 후 데이터 담기
	        HashMap<String, Object> map = new HashMap<>();
	        map.put("notice", n);
	        map.put("noticeFile", nf);
	        
			return ResponseEntity.status(HttpStatus.OK)
								 .body(map);
			
		} else {
			// > 조회수 증가에 실패한 경우
			
			return ResponseEntity.status(HttpStatus.OK)
								 .body(null);
		}
	}
	

	// 수정용 공지사항 상세 조회용 컨트롤러 - (다시 상세조회하면 조회수 증가될수있으므로)
	@GetMapping("/notices/{noticeId}/form")
	public ResponseEntity<HashMap<String, Object>> selectNoticeForm(@PathVariable Long noticeId) {
		
		Notice n = noticeService.selectNotice(noticeId);
		
		NoticeFile nf = noticeService.selectNoticeFile(noticeId);
		
		// Map 객체 생성 후 데이터 담기
        HashMap<String, Object> map = new HashMap<>();
        map.put("notice", n);
        map.put("noticeFile", nf);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(map);
	}
	
	// 공지사항 수정용 컨트롤러
	@PostMapping("/notices/{noticeId}/edit")
	public ResponseEntity<String> updateNotice(@PathVariable Long noticeId,
											   Notice n, 
											   @RequestParam(value = "noticeFileId", required = false) Long noticeFileId,
                                               @RequestParam(value = "reupfile", required = false) MultipartFile reupfile,
										       HttpSession session,
										       HttpServletRequest request) {
		
		// 작성자 (로그인한 회원) 정보 뽑기
		String authHeader = request.getHeader("Authorization");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 존재하지 않습니다.");
		}
		
		String jwtTokenString = authHeader.substring(7).trim();
		
		// 토큰 값이 empty, "null", "undefined"인 경우 예외 발생 전에 사전 차단
		if (jwtTokenString.isEmpty() || "null".equalsIgnoreCase(jwtTokenString) || "undefined".equalsIgnoreCase(jwtTokenString)) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
		}
		
		Claims claims;
		try {
		Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		} catch (Exception e) {
			// 잘못된 JWT 형식이 들어와도 500 에러 대신 401 Unauthorized 반환
			e.printStackTrace();
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 검증 실패");
		}
		
		Long id = claims.get("id", Long.class);
		
		Employee emp = employeeService.selectEmployee(id);
		
		n.setEmployee(emp);
	    Notice existingNotice = noticeService.selectNotice(noticeId);
	    if (existingNotice == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("fail");
	    }

	    existingNotice.setTitle(n.getTitle());
	    existingNotice.setContent(n.getContent());
	
		Notice updateNo = noticeService.updateNotice(n);
		
		// 새로 넘어온 첨부파일이 있는지 먼저 검사
		if(reupfile != null && !reupfile.isEmpty()) {
			
			NoticeFile nf = noticeService.selectNoticeFile(noticeId);
			
			if (nf != null) {
		        nf.setNotices(updateNo); // 게시글 번호 매핑
		        noticeService.insertNoticeFile(nf);
		    }
		}
		
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
	
	// 첨부파일 다운로드용 컨트롤러
	@GetMapping("/notices/download/{noticeFileId}")
	public ResponseEntity<Resource> upfileDownload(@PathVariable Long noticeFileId,
												   HttpSession session) throws IOException {
		
		NoticeFile noticeFile = noticeService.selectNoticeFileId(noticeFileId); 
	    
		// 2. 파일 정보가 DB에 없거나 실제 저장명이 없으면 404 리턴
	    if (noticeFile == null || noticeFile.getSavedName() == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	    
	    String savedPath = noticeSaveFile.getSavedPath(); 
	    Path filePath = Paths.get(savedPath, noticeFile.getSavedName());
	    
		// 파일을 그냥 응답데이터로는 못보내고, 응답데이터로 내보낼 수 있게끔 포장
		Resource resource = new FileSystemResource(filePath.toFile());
		
		// 파일이 제대로 존재하는지를 검사 후 응답데이터로 보내기
		if(!resource.exists()) {
			// > 해당 파일이 존재하지 않을 경우
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body(null);
			
		} else {
			// > 해당 파일이 존재할 경우

			// 우선 한글 파일명 깨짐을 방지
			String originName = noticeFile.getOriginName();
			String encodedName = URLEncoder.encode(originName, "UTF-8").replaceAll("\\+", "%20");
			// > 사용자가 보기 좋게 원본파일명으로 다운로드를 하기 위함
			
			return ResponseEntity.status(HttpStatus.OK)
								 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
								
								 .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
								
								 .body(resource);
		}		
	}

		
}
