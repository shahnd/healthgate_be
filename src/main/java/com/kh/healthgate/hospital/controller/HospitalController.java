package com.kh.healthgate.hospital.controller;

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

import com.kh.healthgate.common.model.vo.PageInfo;
import com.kh.healthgate.common.template.Pagination;
import com.kh.healthgate.hospital.model.service.HospitalService;
import com.kh.healthgate.hospital.model.vo.Hospital;


@CrossOrigin
@RestController
public class HospitalController {
   
	@Autowired
	private HospitalService hospitalService;
	
	// 검진가능 병원 조회용 컨트롤러
	@GetMapping("/hospitals")
	public ResponseEntity<HashMap<String,Object>> selectHospitalList(
			            @RequestParam(value="cpage", defaultValue="1") int currentPage) {
		
		int boardLimit = 5;
		int pageLimit = 5;
		
		// > Pageable 객체 셋팅
		Pageable pageable = PageRequest.of(currentPage - 1, 5);
		
		// > Pageable 을 넘기면서 조회
		Page<Hospital> page = hospitalService.selectHospitalList(pageable);
		
		// > 각각의 응답데이터 셋팅
		List<Hospital> list = page.getContent();
		long listCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)listCount, currentPage, 
													pageLimit, boardLimit);
		
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("list", list); 
		hm.put("pi", pi); 
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(hm);
	}
	
    
	// 검진가능 병원 검색용 컨트롤러
	@GetMapping("/hospitals/search")
	public ResponseEntity<HashMap<String,Object>> searchHospitalList(
			          @RequestParam(value="cpage", defaultValue="1") int currentPage,
			          @RequestParam(value ="name", required = false) String name,
			          @RequestParam(value ="address", required = false) String address,
			          @RequestParam(value ="isGeneralExamAvailable", required = false) Boolean isGeneralExamAvailable,
			          @RequestParam(value ="isStomachCancerExamAvailable", required = false) Boolean isStomachCancerExamAvailable,
			          @RequestParam(value ="isColonCancerExamAvailable", required = false) Boolean isColonCancerExamAvailable,
			          @RequestParam(value = "isLiverCancerExamAvailable", required = false) Boolean isLiverCancerExamAvailable,
			          @RequestParam(value = "isLungCancerExamAvailable", required = false) Boolean isLungCancerExamAvailable) {
		
		int boardLimit = 5;
		int pageLimit = 5;
		
		// > Pageable 객체 셋팅
		Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
		
		// > Pageable 을 넘기면서 조회
		Page<Hospital> page = hospitalService.selectSearchList(name, 
				                                               address,
															   isGeneralExamAvailable,
															   isStomachCancerExamAvailable,
															   isColonCancerExamAvailable,
															   isLiverCancerExamAvailable,
															   isLungCancerExamAvailable,
				                                               pageable);
		
		// > 각각의 응답데이터 셋팅
		List<Hospital> list = page.getContent();
		long searchCount = page.getTotalElements();
		
		PageInfo pi = Pagination.getPageInfo((int)searchCount, currentPage, pageLimit, boardLimit);
		
		// pi 와 list 를 HashMap 으로 묶어서 하나의 응답데이터로 넘김
		HashMap<String, Object> hm = new HashMap<>();
		
		hm.put("pi", pi);
		hm.put("list", list);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(hm);
	}

	
	// 검진가능 병원 상세조회용 컨트롤러
	@GetMapping("/hospitals/{id}")
	public ResponseEntity<Hospital> selectHospital(@PathVariable int id) {
		
		Hospital h = hospitalService.selectHospital(id);
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(h);
	}
	
	// 검진가능 병원 수정용 상세조회용 컨트롤러
	@GetMapping("/hospitals/{id}/form")
	public ResponseEntity<Hospital> selectHospitalForm(@PathVariable int id) {
		
		Hospital h = hospitalService.selectHospital(id);
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(h);
	}
	
	// 검진가능 병원 등록용 컨트롤러
	@PostMapping("/hospitals/new")
	public ResponseEntity<String> insertHospital(Hospital h) {
		
		// 서비스 호출 
		Hospital insertHo = hospitalService.insertHospital(h);
		
		String message = (insertHo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(message);
	}
	
	// 검진가능 병원 삭제용 컨트롤러
	@DeleteMapping("/hospitals/{id}")
	public ResponseEntity<String> deleteHospital(@PathVariable int id) {
		
		// 서비스 호출
		int result = hospitalService.deleteHospital(id);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(message);
	}
	
}
