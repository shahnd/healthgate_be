package com.kh.healthgate.safety.ai.index;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import com.kh.healthgate.file.storage.FileStorage;

@ExtendWith(MockitoExtension.class)
class VectorIndexRequestedEventListenerTest {
    @Mock
    private FileStorage fileStorage;

    @Mock
    private VectorIndexFingerprintFactory fingerprintFactory;

    @Mock
    private VectorIndexManifestService manifestService;

    @Mock
    private PdfVectorIndexingPipeline indexingPipeline;

    private VectorIndexRequestedEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new VectorIndexRequestedEventListener(
                fileStorage,
                fingerprintFactory,
                manifestService,
                indexingPipeline);
    }

    @Test
    void reusesCompletedVectorIndex() {
        // given
        VectorIndexRequestedEvent event = new VectorIndexRequestedEvent("documents/manual.pdf", "checksum");
        when(fingerprintFactory.create("checksum")).thenReturn("fingerprint");
        when(manifestService.isCompleted("fingerprint")).thenReturn(true);

        // when
        listener.index(event);

        // then
        verifyNoInteractions(fileStorage, indexingPipeline);
        verify(manifestService, never()).startIndexing("fingerprint", "checksum");
    }

    @Test
    void indexesFileAndCompletesManifest() {
        // given
        VectorIndexRequestedEvent event = new VectorIndexRequestedEvent("documents/manual.pdf", "checksum");
        Resource resource = new ByteArrayResource("pdf".getBytes());
        when(fingerprintFactory.create("checksum")).thenReturn("fingerprint");
        when(fileStorage.load("documents/manual.pdf")).thenReturn(resource);
        when(indexingPipeline.index(resource, "fingerprint")).thenReturn(3);

        // when
        listener.index(event);

        // then
        verify(manifestService).startIndexing("fingerprint", "checksum");
        verify(manifestService).completeIndexing("fingerprint", 3);
    }

    @Test
    void recordsFailedManifestWhenIndexingFails() {
        // given
        VectorIndexRequestedEvent event = new VectorIndexRequestedEvent("documents/manual.pdf", "checksum");
        Resource resource = new ByteArrayResource("invalid-pdf".getBytes());
        IllegalStateException failure = new IllegalStateException("PDF 파싱 실패");
        when(fingerprintFactory.create("checksum")).thenReturn("fingerprint");
        when(fileStorage.load("documents/manual.pdf")).thenReturn(resource);
        when(indexingPipeline.index(resource, "fingerprint")).thenThrow(failure);

        // when
        listener.index(event);

        // then
        verify(manifestService).failIndexing("fingerprint", "PDF 파싱 실패");
        verify(manifestService, never()).completeIndexing(eq("fingerprint"), anyInt());
    }
}
