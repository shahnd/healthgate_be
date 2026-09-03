package com.kh.healthgate.notice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.kh.healthgate.notice.model.vo.NoticeFile;

@Component
public class NoticeSaveFile {
		
		// 1. 외부 설정값(application.properties)을 읽어옴
		private static String uploadDir;
		
		@Value("${file.upload-dir}")
	    public void setUploadDir(String value) {
	        NoticeSaveFile.uploadDir = value;
	    }
		
		// OS에 따라 기본 보관 경로를 자동으로 지정하는 메서드
	    public static String getSavedPath() {
	    	return uploadDir;
	    }
	    
	    public static NoticeFile saveFile(MultipartFile file) {
	        if (file == null || file.isEmpty()) {
	            return null;
	        }

	        String originName = file.getOriginalFilename();
	        String extension = "";
	        if (originName != null && originName.contains(".")) {
	            extension = originName.substring(originName.lastIndexOf("."));
	        }

	        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	        int ranNum = (int) (Math.random() * 90000 + 10000);
	        String savedName = currentTime + ranNum + extension;
	        
	     
		    String savedPath = getSavedPath();

	        File targetDir = new File(savedPath);
	        if (!targetDir.exists()) {
	            targetDir.mkdirs();
	        }

	        try {
	            file.transferTo(new File(targetDir, savedName));
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }

	        // VO에 정보 세팅 후 반환
	        NoticeFile nf = new NoticeFile();
	        nf.setOriginName(originName);
	        nf.setSavedName(savedName);
	        nf.setSavedPath(savedPath);
	        nf.setExtension(extension);

	        return nf;
	    }	    
}
