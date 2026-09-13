package com.kh.healthgate.safety.ai.index;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import com.kh.healthgate.safety.domain.SafetyDocumentIndexingRequest;

class SafetyDocumentChunkerTest {
    @Test
    void preservesPageStructureAndAddsSourceMetadataWithoutChangingInput() {
        var first = new Document("page-1", "  적용 조건\n  - 첫 번째 수칙\n  - 두 번째 수칙  ",
                Map.of("page_number", 2));
        var second = new Document("page-2", "다음 페이지", Map.of("page_number", 4));
        var request = new SafetyDocumentIndexingRequest("storage", "안전수칙", "manual.pdf", "checksum", "fingerprint");

        var chunks = new SafetyDocumentChunker().chunk(List.of(first, second), request);

        assertThat(chunks).hasSize(2);
        assertThat(chunks.getFirst().getText()).isEqualTo("적용 조건\n  - 첫 번째 수칙\n  - 두 번째 수칙");
        assertThat(chunks.getFirst().getId()).isEqualTo("page-1");
        assertThat(chunks.getFirst().getMetadata()).containsAllEntriesOf(Map.of(
                "page_number", 2, "title", "안전수칙", "original_filename", "manual.pdf",
                "fingerprint", "fingerprint", "chunk_index", 0));
        assertThat(chunks.get(1).getMetadata()).containsEntry("page_number", 4).containsEntry("chunk_index", 1);
        assertThat(first.getMetadata()).containsOnlyKeys("page_number");
        assertThat(first.getText()).startsWith("  적용 조건");
    }
}
