package com.kh.healthgate.common.template;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

public class FileRenamePolicy {
public static String saveFile(MultipartFile upfile, 
							String savedPath) {
		
        // 1. 원본파일명 뽑아오기
		String originName = upfile.getOriginalFilename(); 
		
		// 2. 시간 형식을 문자열로 뽑아내기
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss")
								.format(new Date()); 
		
		// 3. 뒤에 붙을 5자리 랜덤수 뽑기
		int ranNum = (int)(Math.random() * 90000 + 10000); 
		
		// 4. 원본파일명으로부터 확장자명 뽑기
		String extension = "";
		if (originName != null && originName.contains(".")) {
            extension = originName.substring(originName.lastIndexOf("."));
        }
		
		// 5. 2 + 3 + 4 모두 이어 붙이기
		String savedName = currentTime + ranNum + extension;
		
        // 폴더 생성 
		File targetDir = new File(savedPath);
		if (!targetDir.exists()) {
		    targetDir.mkdirs(); // 디렉토리 없으면 하위 폴더까지 일괄 생성
		}
		
		// 7. 경로와 수정파일명 합체 후 파일 업로드 하기
		try {
			upfile.transferTo(new File(targetDir, savedName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return savedName;
	}
}
