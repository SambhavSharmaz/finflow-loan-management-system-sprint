package com.capgemini.documentservice.service;

import com.capgemini.documentservice.dto.DocumentResponse;
import com.capgemini.documentservice.entity.Document;
import com.capgemini.documentservice.repository.DocumentRepository;
import com.dropbox.core.v2.DbxClientV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitOperations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private RabbitOperations rabbitTemplate;

    @Mock
    private DbxClientV2 dbxClient;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(documentRepository, rabbitTemplate, dbxClient, "/finflow-test");
    }

    private Document createDocument() {
        Document doc = new Document();
        doc.setId(1L);
        doc.setApplicationId(100L);
        doc.setFileName("test.pdf");
        doc.setFileUrl("http://dropbox.com/test.pdf");
        doc.setStatus("UPLOADED");
        return doc;
    }

    @Test
    void verify_Success() {
        Document doc = createDocument();
        Document verifiedDoc = createDocument();
        verifiedDoc.setStatus("VERIFIED");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(documentRepository.save(any(Document.class))).thenReturn(verifiedDoc);

        DocumentResponse response = documentService.verify(1L);

        assertNotNull(response);
        assertEquals("VERIFIED", response.getStatus());
        assertEquals(100L, response.getApplicationId());
        verify(rabbitTemplate).convertAndSend(eq("documentUploadQueue"), anyString());
    }

    @Test
    void verify_NotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> documentService.verify(999L));
    }

    @Test
    void get_Success() {
        Document doc = createDocument();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));

        DocumentResponse response = documentService.get(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test.pdf", response.getFileName());
        assertEquals("http://dropbox.com/test.pdf", response.getFileUrl());
    }

    @Test
    void get_NotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> documentService.get(999L));
    }

    @Test
    void delete_Success() {
        Document doc = createDocument();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        doNothing().when(documentRepository).delete(doc);

        assertDoesNotThrow(() -> documentService.delete(1L));
        verify(documentRepository).delete(doc);
    }

    @Test
    void delete_NotFound() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> documentService.delete(999L));
    }

    @Test
    void getByApplicationId_Success() {
        Document doc1 = createDocument();
        Document doc2 = createDocument();
        doc2.setId(2L);
        doc2.setFileName("doc2.pdf");

        when(documentRepository.findByApplicationId(100L)).thenReturn(List.of(doc1, doc2));

        List<DocumentResponse> responses = documentService.getByApplicationId(100L);

        assertEquals(2, responses.size());
        assertEquals("test.pdf", responses.get(0).getFileName());
        assertEquals("doc2.pdf", responses.get(1).getFileName());
    }

    @Test
    void getByApplicationId_Empty() {
        when(documentRepository.findByApplicationId(999L)).thenReturn(List.of());

        List<DocumentResponse> responses = documentService.getByApplicationId(999L);

        assertTrue(responses.isEmpty());
    }
}
