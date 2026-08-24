package com.kh.healthgate.notice.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.common.template.FileRenamePolicy;
import com.kh.healthgate.notice.model.vo.NoticeFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
public class NoticeFilesController {
	
	// 공지사항 작성용 컨트롤러
	@PostMapping("/notices/files")
	public ResponseEntity<NoticeFile> insertNoticeFiles(NoticeFile nf, MultipartFile upfile, 
											  HttpSession session,
											  HttpServletRequest request) {
       
		// 넘어온 첨부파일이 있을 경우
		// > 파일명 수정작업 후 서버로 업로드 (공통코드), originName, savedName 필드값을 셋팅
		if(upfile != null) {
			
			// System.out.println(upfile.getOriginalFilename());
			
			String originName = upfile.getOriginalFilename();
			
			String savedName 
						= FileRenamePolicy.saveFile(upfile, session, 
													"/resources/notice_upfiles/");
			
			nf.setOriginName(originName);
			nf.setSavedName(savedName);
		}
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(nf);
	}
	
	// 첨부파일 다운로드용 컨트롤러
	@GetMapping("/notices/download/{savedName}/{originName}")
	public ResponseEntity<Resource> upfileDownload(@PathVariable String savedName, 
												   @PathVariable String originName,
												   @PathVariable String savedPath,
												   HttpSession session) throws IOException {
		
		// 다운로드할 파일의 물리적인 경로
		savedPath = session.getServletContext().getRealPath("/resources/notice_upfiles/");
		
		// 파일을 그냥 응답데이터로는 못보내고, 응답데이터로 내보낼 수 있게끔 포장
		Resource resource = new FileSystemResource(savedPath + savedName);
		
		// 파일이 제대로 존재하는지를 검사 후 응답데이터로 보내기
		if(!resource.exists()) {
			// > 해당 파일이 존재하지 않을 경우
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body(null);
			
		} else {
			// > 해당 파일이 존재할 경우
			
			// 이번에는 응답데이터가 파일로 넘어가야하는 특이케이스이기 때문에 설정이 이것저것 붙는다!!
			// 우선 한글 파일명 깨짐을 방지
			String encodedName = URLEncoder.encode(originName, "UTF-8");
			// > 사용자가 보기 좋게 원본파일명으로 다운로드를 하기 위함
			
			return ResponseEntity.status(HttpStatus.OK)
								 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
								
								 .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(savedPath + savedName)))
								
								 .body(resource);
		}		
	}
	
	// 공지사항 수정용 컨트롤러
	@PostMapping("/notices/{noticeId}/files")
	public ResponseEntity<NoticeFile> updateNoticeFiles(NoticeFile nf,
													    MultipartFile reupfile,
													    HttpSession session) {
		
		System.out.println(nf);
	
		// 기존 첨부파일이 없을 경우 "null" --> null
		if((nf.getOriginName().equals("null")) && (nf.getSavedName().equals("null"))) {
			
			nf.setOriginName(null);
			nf.setSavedName(null);
		}
	
		
		// 새로 넘어온 첨부파일이 있는지 먼저 검사
		if(reupfile != null) {
			// 넘어온 첨부파일이 있을 경우
			
			String originName = reupfile.getOriginalFilename();
			
			String savedName = FileRenamePolicy.saveFile(reupfile, session, 
															"/resources/notice_upfiles/");
			
			nf.setOriginName(originName);
			nf.setSavedName(savedName);
		}
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(nf);
	}
	
	
	
}
