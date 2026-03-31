package com.capgemini.documentservice.controller;

import com.capgemini.documentservice.dto.ApiResponse;
import com.capgemini.documentservice.dto.DocumentResponse;
import com.capgemini.documentservice.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @Test
    void verify_Success() {
        DocumentResponse docResponse = new DocumentResponse();
        docResponse.setId(1L);
        docResponse.setApplicationId(100L);
        docResponse.setStatus("VERIFIED");

        when(documentService.verify(1L)).thenReturn(docResponse);

        ApiResponse<DocumentResponse> response = documentController.verify(1L);

        assertTrue(response.isSuccess());
        assertEquals("Document verified.", response.getMessage());
        assertEquals("VERIFIED", response.getData().getStatus());
    }

    @Test
    void get_Success() {
        DocumentResponse docResponse = new DocumentResponse();
        docResponse.setId(1L);
        docResponse.setFileName("test.pdf");

        when(documentService.get(1L)).thenReturn(docResponse);

        ApiResponse<DocumentResponse> response = documentController.get(1L);

        assertTrue(response.isSuccess());
        assertEquals("Document fetched.", response.getMessage());
        assertEquals("test.pdf", response.getData().getFileName());
    }

    @Test
    void delete_Success() {
        doNothing().when(documentService).delete(1L);

        ApiResponse<String> response = documentController.delete(1L);

        assertTrue(response.isSuccess());
        assertEquals("Document deleted.", response.getMessage());
        verify(documentService).delete(1L);
    }

    @Test
    void getByApplicationId_Success() {
        DocumentResponse doc1 = new DocumentResponse();
        doc1.setId(1L);
        doc1.setFileName("doc1.pdf");

        DocumentResponse doc2 = new DocumentResponse();
        doc2.setId(2L);
        doc2.setFileName("doc2.pdf");

        when(documentService.getByApplicationId(100L)).thenReturn(List.of(doc1, doc2));

        ApiResponse<List<DocumentResponse>> response = documentController.getByApplicationId(100L);

        assertTrue(response.isSuccess());
        assertEquals("Documents fetched.", response.getMessage());
        assertEquals(2, response.getData().size());
    }
}
