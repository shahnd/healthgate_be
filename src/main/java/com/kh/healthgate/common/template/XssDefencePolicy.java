package com.kh.healthgate.common.template;

public class XssDefencePolicy {


    
	/**
     * XSS 공격 방지용 공통 코드 메소드
	 * @param originText => <> 등이 담겨있는 원본 문자열
	 * @return => <> 등을 안전하게 치환한 결과 문자열
	 */
	public static String defence(String originText) {
		
		String changeText = originText;
		
		changeText = changeText.replace("<", "&lt;");
		changeText = changeText.replace(">", "&gt;");
		changeText = changeText.replace("\"", "&quot;");
		changeText = changeText.replace("'", "&apos;");
		
		return changeText;
	}
	
}
