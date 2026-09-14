package com.kh.healthgate.consultation.controller;

import java.net.URI;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.common.template.XssDefencePolicy;
import com.kh.healthgate.consultation.model.service.ConsultationService;
import com.kh.healthgate.consultation.model.vo.Consultation;
import com.kh.healthgate.consultation.model.vo.ConsultationStatus;
import com.kh.healthgate.employee.model.vo.Employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="상담 예약 관리", description="보건 상담 예약 신청, 조회, 취소 기능을 제공")
@CrossOrigin
@RestController
@RequestMapping("consultation/reservations")
public class ReservationController {

//	@Value("${jwt.secret}")
//	private String secretKey;
	
	@Autowired
	private ConsultationService consultationService;
	
	// 예약 전체 조회(캘린더 형식, 선택된 월별)
	@Operation(summary = "보건 상담 예약 목록 조회",
	        description = "월별 캘린더 형식으로 예약 현황을 조회. 보건관리자는 전체, 그 외 직원은 본인 예약만 조회.")
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
	 @Operation(summary = "보건 상담 예약 상세 조회",
		        description = "특정 예약 건의 상세 내역을 조회. 본인 예약이 아닐 경우 관리자만 접근 가능.")
	 @ApiResponses({
		    @ApiResponse(responseCode = "200", description = "조회 성공"),
		    @ApiResponse(responseCode = "403", description = "본인 예약이 아니며 관리자 권한도 없음"),
		    @ApiResponse(responseCode = "404", description = "존재하지 않는 예약")
		})
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
	@Operation(summary="날짜별 예약 현황 조회",
			   description="예약 신청/수정 시 특정 날짜의 예약 가능한 차시를 확인하기 위해 사용")
	@GetMapping(value="", params="scheduledDate")
	public ResponseEntity<List<Consultation>> reservationSelectByDate(@RequestParam LocalDate scheduledDate) {
		
		// 조회 조건은 연,월,일 이므로 LocalDate 넘기면서 단건 조회
        List<Consultation> list = consultationService.reservationSelectByDate(scheduledDate);
        
        return ResponseEntity.status(HttpStatus.OK)
        					 .body(list);
	}
	
	
	// 예약 신청
	@Operation(summary = "보건 상담 예약 신청",
	        description = "날짜/시간(차시)을 선택해 상담을 신청. 주말/공휴일/중복 일정은 신청이 제한.")
	@ApiResponses({
        @ApiResponse(responseCode = "201", description = "신청 성공"),
        @ApiResponse(responseCode = "400", description = "신청 사유 누락 등 잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "동일 날짜/차시 중복 예약")
    })
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
	@Operation(summary = "보건 상담 예약 수정",
	        description = "기존 예약의 날짜/차시/사유를 수정. 기존 일정이 당일이거나 처리 완료된 예약은 수정이 제한됨.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "수정 성공"),
	    @ApiResponse(responseCode = "400", description = "잘못된 요청 (사유 누락 시 'bad_request')"),
	    @ApiResponse(responseCode = "403", description = "권한 없음 (본인 예약이 아니며 관리자 권한도 없음)"),
	    @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 ('not_found')"),
	    @ApiResponse(responseCode = "409", description = "수정 불가 조건: 동일 날짜/차시 중복 예약('duplicated') 또는 당일·처리 완료된 건('not_modifiable')")
	})
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
	@Operation(summary = "예약 취소",
	        description = "예약을 취소 상태로 전환(소프트 삭제). 기존 일정이 당일이거나 처리 완료된 예약은 취소가 제한됨.")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "취소 성공"),
	    @ApiResponse(responseCode = "403", description = "본인 예약이 아니며 관리자 권한도 없음"),
	    @ApiResponse(responseCode = "404", description = "존재하지 않는 예약"),
	    @ApiResponse(responseCode = "409", description = "당일이거나 이미 처리 완료된 건이라 취소 불가")
	})
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
