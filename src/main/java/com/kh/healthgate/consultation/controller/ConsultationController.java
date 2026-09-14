package com.kh.healthgate.consultation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.common.template.XssDefencePolicy;
import com.kh.healthgate.consultation.model.service.ConsultationService;
import com.kh.healthgate.consultation.model.vo.Consultation;
import com.kh.healthgate.employee.model.service.EmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.EmployeeRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="상담 관리", description="보건 상담 목록 조회, 일지 작성, 상세 조회, 수정 기능을 제공합니다.")
@CrossOrigin
@RestController
@RequestMapping("consultation/consultations")
public class ConsultationController {

//	@Value("${jwt.secret}")
//	private String secretKey;
	
	@Autowired
	private ConsultationService consultationService;
	
	@Autowired
	private EmployeeService employeeService;
	
	// 상담 전체 조회 (리스트 형식)
	@Operation(summary = "상담 일지 목록 조회",
	        description = "기간 조건으로 상담 일지 목록을 조회. 보건 관리자는 전체, 그 외 직원은 본인 관련 내역만 조회.")
	@GetMapping("")
	public ResponseEntity<List<Consultation>> selectAllConsultation(@RequestParam String startMonth,
																    @RequestParam String endMonth,
																    @Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
		
		
		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id();
		boolean isAdmin = "HEALTH_ADMIN".equals(authenticatedEmployee.role());
		
		// 'YYYY-MM' -> LocalDate
		LocalDate startDate = LocalDate.parse(startMonth + "-01");
		LocalDate endDate = LocalDate.parse(endMonth + "-01").plusMonths(1);
		List<Consultation> list;
		
		// 권한 검증
		if (isAdmin) {
			// 상담사
			list = consultationService.selectAllConsultation(startDate, endDate);
		} else {
			// 일반 유저
			list = consultationService.selectConsultationByUserId(startDate, endDate, userId);
		}
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
	} //selectAllConsultation
	
	
	// 상담 단건 조회
	@Operation(summary = "상담 일지 단건 조회",
	        description = "특정 상담 건의 작성 내용과 상태를 조회. 본인 상담 건이 아닐 경우 보건 관리자만 접근 가능.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "조회 성공"),
	    @ApiResponse(responseCode = "403", description = "본인 상담 건이 아니며 관리자 권한도 없음"),
	    @ApiResponse(responseCode = "404", description = "존재하지 않는 상담 건")
	})
	@GetMapping("{id}")
	public ResponseEntity<Consultation> selectConsultation(@PathVariable Long id,
														   @Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id();
		boolean isAdmin = "HEALTH_ADMIN".equals(authenticatedEmployee.role());
		
		// 예약번호와 일치한 행 조회
		Consultation c = consultationService.selectReservation(id);
		
		// 권한 검증
		if(c == null) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .build();
		}
		if (!isAdmin && (c.getEmployee() == null || !c.getEmployee().getId().equals(userId))) {
			
				return ResponseEntity.status(HttpStatus.FORBIDDEN) // FORBIDDEN : 권한 없음
									 .build();
		}
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(c);
	} //selectConsultation
	
	
	// 상담 일지 작성/수정
	@Operation(summary = "상담 일지 작성/수정",
	        description = "보건 관리자가 상담 내용을 작성하거나 수정. 내용 미입력 또는 미완료 상태에서는 저장이 제한.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "저장 성공"),
	    @ApiResponse(responseCode = "400", description = "상담 내용 미입력"),
	    @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
	})
	@PutMapping("{id}")
	public ResponseEntity<String> saveConsultation(@PathVariable Long id,
												   @RequestBody Consultation c,
												   @Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id();

		Employee e = employeeService.selectEmployee(userId);
		c.setManager(e);
		c.setId(id);
		
		// 권한 검증
		if (!EmployeeRole.HEALTH_ADMIN.equals(e.getRole())) {
			if(c.getEmployee() == null || !c.getEmployee().getId().equals(userId))
				return ResponseEntity.status(HttpStatus.FORBIDDEN) // FORBIDDEN : 권한 없음
									 .body("forbidden");
		}
		
		// 상담 내용 XSS 방어
		if(c.getContent() != null && !c.getContent().isEmpty()) {
			
			c.setContent(XssDefencePolicy.defence(c.getContent()));
		} else {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("emptyContent");
		}
		
		// insert(update)
		Consultation result = consultationService.saveConsultation(c); 
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //insertConsultation

}
