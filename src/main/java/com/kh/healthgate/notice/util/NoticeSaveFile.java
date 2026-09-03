package com.kh.healthgate.notice.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.kh.healthgate.notice.model.vo.NoticeFile;

public class NoticeSaveFile {
	public class FileUtil {

		// OS에 따라 기본 보관 경로를 자동으로 지정하는 메서드
	    public static String getSavedPath() {
	        String os = System.getProperty("os.name").toLowerCase();
	        
	        if (os.contains("win")) {
	            // Windows 개발 환경 경로
	            return "C:\\healthgate\\notice_upfiles\\";
	        } else {
	            // Linux / EC2 배포 환경 경로 (루트 기준 절대경로)
	            return "/var/www/uploads/notice_upfiles/";
	        }
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
}
