package com.kh.healthgate.consultation.controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;
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

import com.kh.healthgate.auth.controller.AuthController;
import com.kh.healthgate.common.template.XssDefencePolicy;
import com.kh.healthgate.consultation.model.service.ConsultationService;
import com.kh.healthgate.consultation.model.vo.Consultation;
import com.kh.healthgate.employee.model.service.EmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("consultation")
public class ConsultationController {

//	@Value("${jwt.secret}")
//	private String secretKey;
	
	@Autowired
	private ConsultationService consultationService;
	
	@Autowired
	private EmployeeService employeeService;
	
	// 예약 전체 조회(캘린더 형식, 선택된 월별)
	@GetMapping("reservations/list")
	public ResponseEntity<List<Consultation>> selectAllReservation(@RequestParam LocalDate scheduledDate,
																   HttpServletRequest request) {
		
		LocalDate startMonth = scheduledDate.withDayOfMonth(1); // 해당월 1일부터
		LocalDate endMonth = startMonth.plusMonths(1); // 다음달 1일까지
		LocalDate startDate = startMonth.minusDays(7); // 지난달 마지막주 추가
		LocalDate endDate = endMonth.plusDays(7); // 다음달 첫째주 추가
        
		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
		.setSigningKey(key)
		.build()
		.parseClaimsJws(jwtTokenString)
		.getBody();
		
		Long userId = claims.get("id", Long.class); // 토큰의 id 가져오기
		String roleStr = claims.get("role", String.class); // 토큰의 role 가져오기
		
		boolean isAdmin = "HEALTH_ADMIN".equals(roleStr);
		
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
	@GetMapping("reservations/details/{id}")
	public ResponseEntity<Consultation> selectReservation(@PathVariable Long id,
			   											  HttpServletRequest request) {

		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
		.setSigningKey(key)
		.build()
		.parseClaimsJws(jwtTokenString)
		.getBody();
		
		Long userId = claims.get("id", Long.class); // 토큰의 id 가져오기
		String roleStr = claims.get("role", String.class); // 토큰의 role 가져오기
		
		// 예약번호와 일치한 행 조회
		Consultation c = consultationService.selectReservation(id);
		
		// 조회 결과 없음
		if (c == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
		// 권한 검증
		Long writerId = c.getEmployee().getId(); // 기존 신청자
		boolean isAdmin = "HEALTH_ADMIN".equals(roleStr); // 관리자
		
		if(!isAdmin && !writerId.equals(userId)) {
			// 권한 없음
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(c);
	} //selectReservation
	
	
	// 예약 신청/수정을 위한 조회
	@GetMapping("reservations/views")
	public ResponseEntity<List<Consultation>> reservationSelectByDate(@RequestParam LocalDate scheduledDate) {
		
		// 조회 조건은 연,월,일 이므로 LocalDate 넘기면서 단건 조회
        List<Consultation> list = consultationService.reservationSelectByDate(scheduledDate);
        
        return ResponseEntity.status(HttpStatus.OK)
        					 .body(list);
	}
	
	
	// 예약 신청
	@PostMapping("reservations/save")
	public ResponseEntity<String> insertReservation(@RequestBody Consultation c) {
		
		// createdAt 세팅
		c.setCreatedAt(LocalDateTime.now());
		
		// 신청사유 XSS 방어
		if(c.getReason() != null && !c.getReason().isEmpty()) {
			
			c.setReason(XssDefencePolicy.defence(c.getReason()));
		} else {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("invalidReason");
		}
		
		// insert
		Consultation result = consultationService.saveConsultation(c); 
		
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //insertReservation
	
	
	// 예약 수정
	@PutMapping("reservations/save/{id}")
	public ResponseEntity<String> updateReservation(@PathVariable Long id,
													@RequestBody Consultation c,
													HttpServletRequest request) {
		
		
		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
		.setSigningKey(key)
		.build()
		.parseClaimsJws(jwtTokenString)
		.getBody();
		
		Long userId = claims.get("id", Long.class); // 토큰의 id 가져오기
		String roleStr = claims.get("role", String.class); // 토큰의 role 가져오기
		
		boolean isAdmin = "HEALTH_ADMIN".equals(roleStr); // 관리자
		Consultation existing = consultationService.selectReservation(id); // 기존 정보 비교용
		
		if(existing == null) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body("not_found");
		}
		
		Long writerId = existing.getEmployee() != null ? existing.getEmployee().getId() : null; // 기존 신청자
		
		// 권한 체크
		if(!isAdmin && (writerId == null || !writerId.equals(userId))) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
								 .body("forbidden");
		}
		
		// 신청사유 XSS 방어
		if(c.getReason() != null && !c.getReason().isEmpty()) {
			
			c.setReason(XssDefencePolicy.defence(c.getReason()));
		} else {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								 .body("bad_request");
		}
		
		c.setId(id);
		
		// update
		Consultation result = consultationService.saveConsultation(c); 
		
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //updateReservation
	
	
	// 예약 취소
	@DeleteMapping("reservations/{id}")
	public ResponseEntity<String> deleteReservation(@PathVariable Long id,
													HttpServletRequest request) {
	
		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
		.setSigningKey(key)
		.build()
		.parseClaimsJws(jwtTokenString)
		.getBody();
		
		Long userId = claims.get("id", Long.class); // 토큰의 id 가져오기
		String roleStr = claims.get("role", String.class); // 토큰의 role 가져오기
		
		boolean isAdmin = "HEALTH_ADMIN".equals(roleStr); // 관리자
		Consultation existing = consultationService.selectReservation(id); // 기존 정보 비교용
		
		if(existing == null) {
		
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .body("not_found");
		}
		
		Long writerId = existing.getEmployee() != null ? existing.getEmployee().getId() : null; // 기존 신청자
		
		// 권한 체크
		if(!isAdmin && (writerId == null || !writerId.equals(userId))) {
		
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
								 .body("forbidden");
		}
		
		// soft delete
		int result = consultationService.deleteReservation(id);
		
		String msg = (result > 0) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //deleteReservation
	
	
	// ==========================================================================================
	
	// 상담 전체 조회 (리스트 형식)
	@GetMapping("consultations/list")
	public ResponseEntity<List<Consultation>> selectAllConsultation(@RequestParam String startMonth,
																		 @RequestParam String endMonth,
																		 HttpServletRequest request) {
		
		
		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
		Long userId = claims.get("id", Long.class);

		Employee e = employeeService.selectEmployee(userId);
		
		// 'YYYY-MM' -> LocalDate
		LocalDate startDate = LocalDate.parse(startMonth + "-01");
		LocalDate endDate = LocalDate.parse(endMonth + "-01").plusMonths(1);
		List<Consultation> list;
		
		// 권한 검증
		if (role.HEALTH_ADMIN.equals(e.getRole())) {
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
	@GetMapping("consultations/detail/{id}")
	public ResponseEntity<Consultation> selectConsultation(@PathVariable Long id,
														   HttpServletRequest request) {
		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
		.setSigningKey(key)
		.build()
		.parseClaimsJws(jwtTokenString)
		.getBody();
		
		Long userId = claims.get("id", Long.class);
		
		Employee e = employeeService.selectEmployee(userId);
		
		// 예약번호와 일치한 행 조회
		Consultation c = consultationService.selectReservation(id);
		
		// 권한 검증
		if (!role.HEALTH_ADMIN.equals(e.getRole())) {
			if(c == null || c.getEmployee() == null || !c.getEmployee().getId().equals(userId))
				return ResponseEntity.status(HttpStatus.FORBIDDEN) // FORBIDDEN : 권한 없음
									 .build();
		}
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(c);
	} //selectConsultation
	
	
	// 상담 일지 작성/수정
	@PutMapping("consultations/{id}")
	public ResponseEntity<String> saveConsultation(@PathVariable Long id,
												   @RequestBody Consultation c,
												   HttpServletRequest request) {
		// 로그인 정보 추출
		String authHeader = request.getHeader("Authorization");
		String jwtTokenString = authHeader.substring(7);
		
		Key key = Keys.hmacShaKeyFor(AuthController.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
							.setSigningKey(key)
							.build()
							.parseClaimsJws(jwtTokenString)
							.getBody();
		
		Long userId = claims.get("id", Long.class);

		Employee e = employeeService.selectEmployee(userId);
		c.setManager(e);
		c.setId(id);
		
		// 권한 검증
		if (!role.HEALTH_ADMIN.equals(e.getRole())) {
			if(c == null || c.getEmployee() == null || !c.getEmployee().getId().equals(userId))
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
