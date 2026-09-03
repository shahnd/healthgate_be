package com.kh.healthgate.safety.seed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.role;
import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;
import com.kh.healthgate.safety.service.SafetyDocumentService;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "com.kh.healthgate.safety.seed.enabled",
        havingValue = "true")
public class SafetyDocumentSeeder implements ApplicationRunner {
    private final EmployeeDao employeeDao;
    private final SafetyDocumentService safetyDocumentService;
    private final SafetyDocumentSeedProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        List<Path> pdfFiles = findPdfFiles();
        if (pdfFiles.isEmpty()) {
            return;
        }

        Optional<Employee> employee = employeeDao.findFirstByRoleAndStatusOrderByIdAsc(
                role.HEALTH_ADMIN,
                "Y");
        if (employee.isEmpty()) {
            log.warn("안전문서 시딩을 건너뜁니다. 재직 중인 보건 관리자가 없습니다.");
            return;
        }

        pdfFiles.forEach(path -> seed(path, employee.get()));
    }

    private List<Path> findPdfFiles() {
        Path seedDirectory = Path.of(properties.getDirectory());
        if (!Files.isDirectory(seedDirectory)) {
            log.warn("안전문서 시딩 디렉터리가 없습니다. directory={}", seedDirectory);
            return List.of();
        }

        try (var paths = Files.list(seedDirectory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::isPdf)
                    .sorted()
                    .toList();
        } catch (IOException exception) {
            log.error("안전문서 시딩 디렉터리를 읽지 못했습니다. directory={}", seedDirectory, exception);
            return List.of();
        }
    }

    private void seed(Path path, Employee employee) {
        String filename = path.getFileName().toString();
        String title = StringUtils.stripFilenameExtension(filename);

        try {
            safetyDocumentService.create(
                    title,
                    null,
                    new FileSystemResource(path),
                    filename,
                    MediaType.APPLICATION_PDF_VALUE,
                    Files.size(path),
                    employee);
            log.info("안전문서를 시딩했습니다. filename={}", filename);
        } catch (SafetyDocumentException exception) {
            if (exception.problemType() == SafetyDocumentProblem.DUPLICATE_FILE) {
                log.info("이미 등록된 안전문서 시딩을 건너뜁니다. filename={}", filename);
                return;
            }
            log.error("안전문서 시딩에 실패했습니다. filename={}", filename, exception);
        } catch (RuntimeException | IOException exception) {
            log.error("안전문서 시딩에 실패했습니다. filename={}", filename, exception);
        }
    }

    private boolean isPdf(Path path) {
        return path.getFileName().toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".pdf");
    }
}
