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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.common.model.vo.PageInfo;
import com.kh.healthgate.common.template.Pagination;
import com.kh.healthgate.hospital.model.service.HospitalService;
import com.kh.healthgate.hospital.model.vo.Hospital;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@Tag(name="병원 관리 API", description ="병원 정보 검색+조회, 등록, 수정, 삭제")
public class HospitalController {
   
	@Autowired
	private HospitalService hospitalService;
	
	// 검진가능 병원 검색용 + 목록 조회 컨트롤러
	@Operation(summary="병원 검색 + 조회", description="검색 결과 와 페이지 번호 (cpage) 에 해당하는 목록을 조회합니다. "
			                                        + "응답 : {list : 게시글 목록, pi : 페이지정보}")
	@ApiResponse(responseCode="200", description="조회 성공",
	            content=@Content(mediaType="application/json",
					             examples=@ExampleObject(value="""
					            		         {
					            		            "list" : [{}, {}, {}],
					            		            "pi" : {
					            		                "listCount" : 1,
					            		                "currentpage" : 1,
					            		                "pageLimit"  : 5,
					            		                "boardLimit"  : 5,
					            		                "maxPage"  : 5,
					            		                "startPage"  : 1,
					            		                "endPage"  : 5
					            		            }
					            		         }
					            		""")))
	@GetMapping("/hospitals")
	public ResponseEntity<HashMap<String,Object>> searchHospitalList(
			           @RequestParam(value="cpage", defaultValue="1") int currentPage,
			           @RequestParam(value = "keywordName", required = false)String keywordName,
			           @RequestParam(value = "keywordAddress", required = false)String keywordAddress,
			           @RequestParam(value = "isGeneral", required = false)Boolean isGeneral,
			           @RequestParam(value = "isStomachCancer", required = false)Boolean isStomachCancer,
			           @RequestParam(value = "isColonCancer", required = false)Boolean isColonCancer,
			           @RequestParam(value = "isLiverCancer", required = false)Boolean isLiverCancer,
			           @RequestParam(value = "isLungCancer", required = false)Boolean isLungCancer) {
		
		int boardLimit = 5;
		int pageLimit = 5;
		
		// > Pageable 객체 셋팅
		Pageable pageable = PageRequest.of(currentPage - 1, boardLimit);
		
		// > Pageable 을 넘기면서 조회
		Page<Hospital> page = hospitalService.selectSearchList(keywordName, 
				                                               keywordAddress,
													           isGeneral,
													           isStomachCancer,
													           isColonCancer,
													           isLiverCancer,
													           isLungCancer,
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
	
	// 검진가능 병원 수정용 컨트롤러
	@Operation(summary="병원 수정", description="병원 정보를 수정합니다.")
	@PutMapping("/hospitals/{hospitalId}")
	public ResponseEntity<String> updateBoard(@PathVariable Long hospitalId,
			@RequestBody Hospital h) {
		
		// 서비스 호출
		Hospital updateHo = hospitalService.updateHospital(h);
		
		String message = (updateHo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
							 .body(message);
	}

	
	// 검진가능 병원 상세조회용 컨트롤러
	@Operation(summary="병원 상세조회", description="병원 정보를 상세조회합니다.")
	@GetMapping("/hospitals/{hospitalId}")
	public ResponseEntity<Hospital> selectHospital(@PathVariable Long hospitalId) {
		
		Hospital h = hospitalService.selectHospital(hospitalId);
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(h);
	}
	
	// 검진가능 병원 등록(생성)용 컨트롤러
	@Operation(summary="병원 등록", description="병원을 등록합니다.")
	@PostMapping("/hospitals/new")
	public ResponseEntity<String> insertHospital(@RequestBody Hospital h) {
		
		h.setStatus("Y");
		// 서비스 호출 
		Hospital insertHo = hospitalService.insertHospital(h);
		
		String message = (insertHo != null) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(message);
	}
	
	// 검진가능 병원 삭제용 컨트롤러
	@Operation(summary="병원 삭제", description="병원 정보를 삭제합니다. status를 'Y' 에서 'N' 변경해서 조회되지 않게 삭제처리")
	@DeleteMapping("/hospitals/{hospitalId}")
	public ResponseEntity<String> deleteHospital(@PathVariable Long hospitalId) {
		
		// 서비스 호출
		int result = hospitalService.deleteHospital(hospitalId);
		
		String message = (result > 0) ? "success" : "fail";
		
		return ResponseEntity.status(HttpStatus.OK)
				             .body(message);
	}
	
}
