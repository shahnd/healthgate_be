package com.kh.healthgate.safety.controller;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.safety.dto.SafetyDocumentCreateRequest;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
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
}
