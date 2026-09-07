package com.kh.healthgate.safety.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springdoc.core.annotations.ParameterObject;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.common.exception.ApiProblemResponse;
import com.kh.healthgate.common.exception.AuthenticationErrorResponse;
import com.kh.healthgate.safety.dto.SafetyDocumentCreateRequest;
import com.kh.healthgate.safety.dto.SafetyDocumentActivationRequest;
import com.kh.healthgate.safety.dto.SafetyDocumentFile;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.dto.SafetyDocumentUpdateRequest;
import com.kh.healthgate.safety.service.SafetyDocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "안전문서", description = "안전문서 등록, 조회, 수정, 삭제 및 벡터 인덱싱 관리 API")
@RestController
@RequestMapping(value = "/safety-documents", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SafetyDocumentController {
    private static final String AUTHENTICATION_ERROR_EXAMPLE =
            "{\"message\":\"인증 토큰이 누락되었습니다.\"}";
    private static final String BAD_REQUEST_EXAMPLE = """
            {
              "type": "/problems/invalid-request",
              "title": "잘못된 요청",
              "status": 400,
              "detail": "요청 값을 확인해 주세요.",
              "instance": "/healthgate/safety-documents",
              "code": "INVALID_REQUEST"
            }
            """;
    private static final String FORBIDDEN_EXAMPLE = """
            {
              "type": "/problems/forbidden",
              "title": "접근 권한 없음",
              "status": 403,
              "detail": "안전문서 관리 권한이 없습니다.",
              "instance": "/healthgate/safety-documents/1",
              "code": "FORBIDDEN"
            }
            """;
    private static final String NOT_FOUND_EXAMPLE = """
            {
              "type": "/problems/safety-document-not-found",
              "title": "안전문서 없음",
              "status": 404,
              "detail": "요청한 안전문서를 찾을 수 없습니다.",
              "instance": "/healthgate/safety-documents/1",
              "code": "SAFETY_DOCUMENT_NOT_FOUND"
            }
            """;
    private static final String CONFLICT_EXAMPLE = """
            {
              "type": "/problems/safety-document-conflict",
              "title": "안전문서 충돌",
              "status": 409,
              "detail": "요청한 작업을 현재 문서 상태에서 수행할 수 없습니다.",
              "instance": "/healthgate/safety-documents/1/index",
              "code": "SAFETY_DOCUMENT_CONFLICT"
            }
            """;
    private static final String INTERNAL_SERVER_ERROR_EXAMPLE = """
            {
              "type": "about:blank",
              "title": "Internal Server Error",
              "status": 500,
              "detail": "요청을 처리하는 중 오류가 발생했습니다.",
              "instance": "/healthgate/safety-documents/1",
              "code": null
            }
            """;

    private final SafetyDocumentService safetyDocumentService;

    @Operation(
            summary = "안전문서 등록",
            description = "PDF 파일과 메타데이터를 등록합니다. 등록만으로 벡터 인덱싱을 시작하지 않습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "안전문서 등록 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = BAD_REQUEST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "안전문서 관리 권한 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = FORBIDDEN_EXAMPLE))),
            @ApiResponse(responseCode = "409", description = "동일한 내용의 문서가 이미 등록됨", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = CONFLICT_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "파일 저장 또는 서버 처리 실패", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SafetyDocumentResponse> create(
            @Valid @ModelAttribute SafetyDocumentCreateRequest request,
            @Parameter(hidden = true) AuthenticatedEmployee loggedInEmployee) {
        SafetyDocumentResponse response = safetyDocumentService.create(
                request.getTitle(),
                request.getDescription(),
                request.getFile(),
                loggedInEmployee);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .build()
                .expand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "안전문서 단건 조회", description = "문서 ID로 메타데이터와 인덱싱 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "안전문서 조회 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "안전문서를 찾을 수 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(ref = "#/components/schemas/ApiProblemResponse"),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @GetMapping("/{id}")
    public SafetyDocumentResponse get(
            @Parameter(
                    description = "안전문서 ID",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long id) {
        return safetyDocumentService.get(id);
    }

    @Operation(
            summary = "안전문서 목록 조회",
            description = "안전문서를 수정일 내림차순으로 페이지 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "안전문서 목록 조회 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @GetMapping
    public PagedModel<SafetyDocumentResponse> getList(
            @ParameterObject
            @PageableDefault(size = 10, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
        return new PagedModel<>(safetyDocumentService.getList(pageable));
    }

    @Operation(summary = "안전문서 수정", description = "문서의 제목과 설명을 수정합니다. 등록된 파일은 변경하지 않습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "안전문서 수정 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 제목 또는 설명", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = BAD_REQUEST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "안전문서 관리 권한 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = FORBIDDEN_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "안전문서를 찾을 수 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @PatchMapping("/{id}")
    public SafetyDocumentResponse update(
            @Parameter(
                    description = "안전문서 ID",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long id,
            @Valid @RequestBody SafetyDocumentUpdateRequest request,
            @Parameter(hidden = true) AuthenticatedEmployee loggedInEmployee) {
        return safetyDocumentService.update(
                id,
                request.title(),
                request.description(),
                loggedInEmployee);
    }

    @Operation(
            summary = "안전문서 활성 상태 변경",
            description = "문서의 활성 또는 비활성 상태를 변경합니다. 비활성 문서는 안전 브리핑 검색에서 제외됩니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "활성 상태 변경 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "활성 상태 값 누락", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = BAD_REQUEST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "안전문서 관리 권한 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = FORBIDDEN_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "안전문서를 찾을 수 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @PatchMapping("/{id}/activation")
    public SafetyDocumentResponse updateActivation(
            @Parameter(
                    description = "안전문서 ID",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long id,
            @Valid @RequestBody SafetyDocumentActivationRequest request,
            @Parameter(hidden = true) AuthenticatedEmployee loggedInEmployee) {
        return safetyDocumentService.updateActivation(
                id,
                request.active(),
                loggedInEmployee);
    }

    @Operation(
            summary = "안전문서 인덱싱 요청",
            description = "활성 문서의 비동기 벡터 인덱싱을 요청합니다. 최초 요청과 실패한 작업의 재시도만 허용합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "인덱싱 요청 접수",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "안전문서 관리 권한 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = FORBIDDEN_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "안전문서를 찾을 수 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE))),
            @ApiResponse(
                    responseCode = "409",
                    description = "비활성 문서이거나 인덱싱이 이미 요청 또는 완료됨",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ApiProblemResponse.class),
                            examples = @ExampleObject(value = CONFLICT_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @PostMapping("/{id}/index")
    public ResponseEntity<SafetyDocumentResponse> requestIndexing(
            @Parameter(
                    description = "안전문서 ID",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long id,
            @Parameter(hidden = true) AuthenticatedEmployee loggedInEmployee) {
        SafetyDocumentResponse response = safetyDocumentService.requestIndexing(
                id,
                loggedInEmployee);
        return ResponseEntity.accepted().body(response);
    }

    @Operation(
            summary = "안전문서 삭제",
            description = "문서 정보와 저장 파일을 삭제합니다. 생성된 벡터 인덱스와 manifest는 재사용을 위해 보존합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "안전문서 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "안전문서 관리 권한 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = FORBIDDEN_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "안전문서를 찾을 수 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = "안전문서 ID",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long id,
            @Parameter(hidden = true) AuthenticatedEmployee loggedInEmployee) {
        safetyDocumentService.delete(id, loggedInEmployee);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "안전문서 파일 조회",
            description = "PDF 파일을 브라우저에서 열거나 첨부 파일로 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "안전문서 파일 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PDF_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "401", description = "인증 필요", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthenticationErrorResponse.class),
                    examples = @ExampleObject(value = AUTHENTICATION_ERROR_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "안전문서를 찾을 수 없음", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "저장된 파일 조회 실패", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ApiProblemResponse.class),
                    examples = @ExampleObject(value = INTERNAL_SERVER_ERROR_EXAMPLE)))
    })
    @GetMapping(value = "/{id}/file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> getFile(
            @Parameter(
                    description = "안전문서 ID",
                    schema = @Schema(type = "integer", format = "int64"))
            @PathVariable Long id,
            @Parameter(description = "true이면 첨부 파일로 다운로드", example = "false")
            @RequestParam(defaultValue = "false") boolean download) {
        SafetyDocumentFile file = safetyDocumentService.getFile(id);
        ContentDisposition disposition = download
                ? ContentDisposition.attachment()
                        .filename(file.filename(), StandardCharsets.UTF_8)
                        .build()
                : ContentDisposition.inline()
                        .filename(file.filename(), StandardCharsets.UTF_8)
                        .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.resource());
    }
}
