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
import io.swagger.v3.oas.annotations.Parameter;

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
								 .body("invalidReason");
		}
		
		// insert(update)
		Consultation result = consultationService.saveConsultation(c); 
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //insertConsultation

}
