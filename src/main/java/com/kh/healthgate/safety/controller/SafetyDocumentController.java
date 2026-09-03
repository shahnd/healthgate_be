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

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.safety.dto.SafetyDocumentCreateRequest;
import com.kh.healthgate.safety.dto.SafetyDocumentFile;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.dto.SafetyDocumentUpdateRequest;
import com.kh.healthgate.safety.service.SafetyDocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/safety-documents")
@RequiredArgsConstructor
public class SafetyDocumentController {
    private final SafetyDocumentService safetyDocumentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SafetyDocumentResponse> create(
            @Valid @ModelAttribute SafetyDocumentCreateRequest request,
            AuthenticatedEmployee loggedInEmployee) {
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

    @GetMapping("/{id}")
    public SafetyDocumentResponse get(@PathVariable Long id) {
        return safetyDocumentService.get(id);
    }

    @GetMapping
    public PagedModel<SafetyDocumentResponse> getList(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Direction.DESC) Pageable pageable) {
        return new PagedModel<>(safetyDocumentService.getList(pageable));
    }

    @PatchMapping("/{id}")
    public SafetyDocumentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody SafetyDocumentUpdateRequest request,
            AuthenticatedEmployee loggedInEmployee) {
        return safetyDocumentService.update(
                id,
                request.title(),
                request.description(),
                loggedInEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            AuthenticatedEmployee loggedInEmployee) {
        safetyDocumentService.delete(id, loggedInEmployee);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getFile(
            @PathVariable Long id,
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
