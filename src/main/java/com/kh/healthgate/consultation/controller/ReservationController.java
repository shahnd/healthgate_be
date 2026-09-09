package com.kh.healthgate.consultation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.common.template.XssDefencePolicy;
import com.kh.healthgate.consultation.model.service.ConsultationService;
import com.kh.healthgate.consultation.model.vo.Consultation;
import com.kh.healthgate.consultation.model.vo.ConsultationStatus;
import com.kh.healthgate.employee.model.service.EmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;
import io.swagger.v3.oas.annotations.Parameter;
import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@CrossOrigin
@RestController
@RequestMapping("consultation/reservations")
public class ReservationController {

//	@Value("${jwt.secret}")
//	private String secretKey;
	
	@Autowired
	private ConsultationService consultationService;
	
	// 예약 전체 조회(캘린더 형식, 선택된 월별)
	@GetMapping(value = "", params = {"year", "month"})
	public ResponseEntity<List<Consultation>> selectAllReservation(@RequestParam int year,
																   @RequestParam int month,
																   @Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
		
		LocalDate startMonth = LocalDate.of(year, month, 1); // 해당월 1일부터
		LocalDate endMonth = startMonth.plusMonths(1); // 다음달 1일까지
		LocalDate startDate = startMonth.minusDays(7); // 지난달 마지막주 추가
		LocalDate endDate = endMonth.plusDays(7); // 다음달 첫째주 추가
        
		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id(); // 토큰의 id 가져오기
		
		boolean isAdmin = "HEALTH_ADMIN".equals(authenticatedEmployee.role());
		
		List<Consultation> list;
		
		// 권한 체크
		if(isAdmin) {
			// 관리자 - 기간 내 모든 예약 조회
			list = consultationService.selectAllConsultation(startDate, endDate);
		} else {
			// 일반 유저 - 기간 내 본인 예약 조회
			list = consultationService.selectConsultationByUserId(startDate, endDate, userId);
		}
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
		
	} //selectAllReservation
	
	
	// 예약 단건 조회
	@GetMapping("{id}")
	public ResponseEntity<Consultation> selectReservation(@PathVariable Long id,
														  @Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {

		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id(); // 토큰의 id 가져오기
		boolean isAdmin = "HEALTH_ADMIN".equals(authenticatedEmployee.role()); // 토큰의 role 가져오기
		
		// 예약번호와 일치한 행 조회
		Consultation c = consultationService.selectReservation(id);
		
		// 조회 결과 없음
		if (c == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		// 권한 검증
		Long writerId = c.getEmployee().getId(); // 기존 신청자
		
		if(!isAdmin && !writerId.equals(userId)) {
			// 권한 없음
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(c);
	} //selectReservation
	
	
	// 예약 신청/수정을 위한 조회
	@GetMapping(value = "", params = "scheduledDate")
	public ResponseEntity<List<Consultation>> reservationSelectByDate(@RequestParam LocalDate scheduledDate) {
		
		// 조회 조건은 연,월,일 이므로 LocalDate 넘기면서 단건 조회
        List<Consultation> list = consultationService.reservationSelectByDate(scheduledDate);
        
        return ResponseEntity.status(HttpStatus.OK)
        					 .body(list);
	}
	
	
	// 예약 신청
	@PostMapping("")
	public ResponseEntity<String> insertReservation(@RequestBody Consultation c,
													@Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
		
		Long userId = authenticatedEmployee.id(); // 토큰의 id 가져오기
		// 리퀘스트바디 대신 토큰기반으로 강제 지정
		Employee writer = new Employee();
		writer.setId(userId);
		
		// createdAt 세팅
		c.setCreatedAt(LocalDateTime.now());
		c.setEmployee(writer);
		
		// 신청사유 XSS 방어
		if(c.getReason() != null && !c.getReason().isEmpty()) {
			
			c.setReason(XssDefencePolicy.defence(c.getReason()));
		} else {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("invalidReason");
		}
		
		// 중복 검증
		List<Consultation> existsList = consultationService.reservationSelectByDate(c.getScheduledDate());
		boolean isDupl = existsList.stream().anyMatch(item -> item.getScheduledTurn().equals(c.getScheduledTurn()));
		
		if(isDupl) {
			return ResponseEntity.status(HttpStatus.CONFLICT) // CONFLICT : 데이터 중복
								 .body("duplicated");
		}
		
		// insert
		Consultation result = consultationService.saveConsultation(c); 
		
		// 결과 반환
		// 실패
		if (result == null) {
			
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
        }

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/consultation/reservations/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        
		// 성공
		return ResponseEntity.created(location)
							 .body("success");
	} //insertReservation
	
	
	// 예약 수정
	@PutMapping("{id}")
	public ResponseEntity<String> updateReservation(@PathVariable Long id,
													@RequestBody Consultation c,
													@Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
		
		
		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id(); // 토큰의 id 가져오기
		boolean isAdmin = "HEALTH_ADMIN".equals(authenticatedEmployee.role()); // 관리자
		Consultation existing = consultationService.selectReservation(id); // 기존 정보 비교용
		
		if(existing == null) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body("not_found");
		}
		
		Long writerId = existing.getEmployee() != null ? existing.getEmployee().getId() : null; // 기존 신청자
		boolean isTodayOrPast = !existing.getScheduledDate().isAfter(LocalDate.now());
		
		// 권한 체크
		if(!isAdmin && (writerId == null || !writerId.equals(userId))) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
								 .body("forbidden");
		}
		
		// 상태, 오늘 체크
		if(!isAdmin && (existing.getStatus() != ConsultationStatus.RESERVED
					|| isTodayOrPast)) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body("not_modifiable");
		}
		
		
		// 신청사유 XSS 방어
		if(c.getReason() != null && !c.getReason().isEmpty()) {
			
			c.setReason(XssDefencePolicy.defence(c.getReason()));
		} else {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("bad_request");
		}
		
		c.setId(id);
		c.setEmployee(existing.getEmployee());
		// 중복 검증 / 자기 자신(예약) 제외
		List<Consultation> existsList = consultationService.reservationSelectByDate(c.getScheduledDate());
		boolean isDupl = existsList.stream().anyMatch(item -> item.getScheduledTurn().equals(c.getScheduledTurn())
														&& !item.getId().equals(id));
		
		if(isDupl) {
			return ResponseEntity.status(HttpStatus.CONFLICT) // CONFLICT : 데이터 중복
								 .body("duplicated");
		}
		
		// update
		Consultation result = consultationService.saveConsultation(c); 
		
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //updateReservation
	
	
	// 예약 취소
	@DeleteMapping("{id}")
	public ResponseEntity<String> deleteReservation(@PathVariable Long id,
													@Parameter(hidden = true) AuthenticatedEmployee authenticatedEmployee) {
	
		// 로그인 정보 추출
		Long userId = authenticatedEmployee.id(); // 토큰의 id 가져오기
		boolean isAdmin = "HEALTH_ADMIN".equals(authenticatedEmployee.role()); // 관리자
		Consultation existing = consultationService.selectReservation(id); // 기존 정보 비교용
		
		if(existing == null) {
		
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body("not_found");
		}
		
		Long writerId = existing.getEmployee() != null ? existing.getEmployee().getId() : null; // 기존 신청자
		boolean isTodayOrPast = !existing.getScheduledDate().isAfter(LocalDate.now());
		
		// 권한 체크
		if(!isAdmin && (writerId == null || !writerId.equals(userId))) {
		
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
								 .body("forbidden");
		}
		
		// 상태, 오늘 체크
		if(!isAdmin && (existing.getStatus() != ConsultationStatus.RESERVED
					|| isTodayOrPast)) {
			
			return ResponseEntity.status(HttpStatus.CONFLICT)
								 .body("not_modifiable");
		}
		
		// soft delete
		int result = consultationService.deleteReservation(id);
		
		String msg = (result > 0) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //deleteReservation
	
	
}
