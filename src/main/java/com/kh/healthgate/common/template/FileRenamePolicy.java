package com.kh.healthgate.common.template;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

public class FileRenamePolicy {
public static String saveFile(MultipartFile upfile, 
								  HttpSession session, String path) {
		
        // 1. 원본파일명 뽑아오기
		String originName = upfile.getOriginalFilename(); // "bono.jpg"
		
		// 2. 시간 형식을 문자열로 뽑아내기
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss")
								.format(new Date()); // "20260513153954"
		
		// 3. 뒤에 붙을 5자리 랜덤수 뽑기
		int ranNum = (int)(Math.random() * 90000 + 10000); // 13159
		
		// 4. 원본파일명으로부터 확장자명 뽑기
		String ext = originName.substring(originName.lastIndexOf("."));
		
		// 5. 2 + 3 + 4 모두 이어 붙이기
		String changeName = currentTime + ranNum + ext;
		
		// 6. 업로드 하고자 하는 서버 폴더의 물리적인 경로를 알아내기
		String savePath = session.getServletContext()
								 .getRealPath(path);

		// 7. 경로와 수정파일명 합체 후 파일 업로드 하기
		try {
			upfile.transferTo(new File(savePath + changeName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return changeName;
	}
}
