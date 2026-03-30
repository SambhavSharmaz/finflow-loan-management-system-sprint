package com.capgemini.documentservice.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.capgemini.documentservice.dto.DocumentResponse;
import com.capgemini.documentservice.entity.Document;
import com.capgemini.documentservice.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private RabbitTemplate rabbitTemplate;
    private DbxClientV2 dbxClient;
    private String dropboxFolder;

    public DocumentService(
            DocumentRepository documentRepository,
            RabbitTemplate rabbitTemplate,
            DbxClientV2 dbxClient,
            @Value("${document.dropbox.folder:/finflow}") String dropboxFolder) {
        this.documentRepository = documentRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.dbxClient = dbxClient;
        this.dropboxFolder = dropboxFolder;
    }

    public DocumentResponse upload(Long applicationId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String originalFileName = getOriginalFileName(file);
        String dropboxPath = buildDropboxPath(applicationId, originalFileName);

        try (InputStream inputStream = file.getInputStream()) {
            FileMetadata metadata = dbxClient.files()
                    .uploadBuilder(dropboxPath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);

            String fileUrl = dbxClient.files()
                    .getTemporaryLink(metadata.getPathLower())
                    .getLink();

            Document document = new Document();
            document.setApplicationId(applicationId);
            document.setFileName(originalFileName);
            document.setFileUrl(fileUrl);
            document.setStatus("UPLOADED");

            Document savedDocument = documentRepository.save(document);
            String message = applicationId + ",DOCS_PENDING";
            rabbitTemplate.convertAndSend("documentUploadQueue", message);
            return map(savedDocument);
        } catch (IOException | DbxException exception) {
            throw new IllegalStateException("Could not upload document to Dropbox. Reason: " + exception.getMessage(), exception);
        }
    }

    public DocumentResponse verify(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found."));

        document.setStatus("VERIFIED");
        Document savedDocument = documentRepository.save(document);
        String message = savedDocument.getApplicationId() + ",DOCS_VERIFIED";
        rabbitTemplate.convertAndSend("documentUploadQueue", message);
        return map(savedDocument);
    }

    public DocumentResponse get(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found."));
        return map(document);
    }

    public void delete(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found."));
        documentRepository.delete(document);
    }

    public List<DocumentResponse> getByApplicationId(Long applicationId) {
        List<Document> documents = documentRepository.findByApplicationId(applicationId);
        List<DocumentResponse> responses = new java.util.ArrayList<>();
        for (Document document : documents) {
            responses.add(map(document));
        }
        return responses;
    }

    private DocumentResponse map(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setApplicationId(document.getApplicationId());
        response.setFileName(document.getFileName());
        response.setFileUrl(document.getFileUrl());
        response.setStatus(document.getStatus());
        return response;
    }

    private String getOriginalFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            return "document";
        }
        return originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String buildDropboxPath(Long applicationId, String fileName) {
        String folder = dropboxFolder;
        if (folder == null || folder.isBlank()) {
            folder = "/finflow";
        }

        if (!folder.startsWith("/")) {
            folder = "/" + folder;
        }

        if (folder.endsWith("/")) {
            folder = folder.substring(0, folder.length() - 1);
        }

        return folder + "/" + applicationId + "_" + System.currentTimeMillis() + "_" + fileName;
    }
}
