package com.capgemini.documentservice.service;

import com.capgemini.documentservice.dto.DocumentResponse;
import com.capgemini.documentservice.entity.Document;
import com.capgemini.documentservice.repository.DocumentRepository;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.mock.web.MockMultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private RabbitOperations rabbitTemplate;

    private final DbxClientV2 dbxClient =
            new DbxClientV2(DbxRequestConfig.newBuilder("document-service-test").build(), "test-token");

    @Test
    void verifyShouldMarkDocumentAsVerified() {
        DocumentService documentService =
                new DocumentService(documentRepository, rabbitTemplate, dbxClient, "/finflow");

        Document document = new Document();
        document.setId(1L);
        document.setApplicationId(10L);
        document.setFileName("id-proof.pdf");
        document.setFileUrl("http://example.com/file");
        document.setStatus("UPLOADED");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DocumentResponse response = documentService.verify(1L);

        assertEquals("VERIFIED", response.getStatus());
        assertEquals(10L, response.getApplicationId());
        verify(rabbitTemplate).convertAndSend("documentUploadQueue", "10,DOCS_VERIFIED");
    }

    @Test
    void verifyShouldThrowWhenDocumentDoesNotExist() {
        DocumentService documentService =
                new DocumentService(documentRepository, rabbitTemplate, dbxClient, "/finflow");

        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> documentService.verify(99L)
        );

        assertEquals("Document not found.", exception.getMessage());
    }

    @Test
    void uploadShouldThrowWhenFileIsEmpty() {
        DocumentService documentService =
                new DocumentService(documentRepository, rabbitTemplate, dbxClient, "/finflow");

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> documentService.upload(1L, file)
        );

        assertEquals("File is empty.", exception.getMessage());
        verify(documentRepository, never()).save(any(Document.class));
    }
}
