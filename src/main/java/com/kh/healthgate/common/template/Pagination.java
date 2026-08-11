package com.kh.healthgate.common.template;

import com.kh.healthgate.common.model.vo.PageInfo;

public class Pagination {

    public static PageInfo getPageInfo(int listCount, int currentPage,
									   int pageLimit, int boardLimit) {

        // 1. 총 페이지 갯수 계산
		int maxPage = (int)Math.ceil((double)listCount / boardLimit);

        // 2. 현재화면의 시작페이지 계산
		int startPage = (currentPage - 1) / pageLimit * pageLimit + 1;

        // 3. 현재화면의 끝페이지 계산
		int endPage = startPage + pageLimit - 1;
		if(endPage > maxPage) {
			endPage = maxPage;
		}
		
		return new PageInfo(listCount, currentPage, pageLimit, boardLimit,
							maxPage, startPage, endPage);
		
	}
}
