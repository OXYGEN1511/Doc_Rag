package com.myproject.doc_rag.service;
import com.myproject.doc_rag.dto.DocumentResponseDto;
import com.myproject.doc_rag.entity.DocumentMetaData;
import com.myproject.doc_rag.entity.DocumentStatus;
import com.myproject.doc_rag.entity.User;
import com.myproject.doc_rag.exception.DocumentProcessingException;
import com.myproject.doc_rag.repository.DocumentMetaDataRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor //
public class DocumentMetadataService {

    private final DocumentParserService parserService;
    private final DocumentIngestionService ingestionService;
    private final ModelMapper modelMapper;
    private final DocumentMetaDataRepo documentMetaDataRepo;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logs = LoggerFactory.getLogger(DocumentMetadataService.class);

    @Transactional
    public DocumentResponseDto uploadAndProcess(MultipartFile file, User user) {

        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octat-stream";

        //document meta data create
        //storing the user detail with document metadata
        DocumentMetaData documentMetadata = DocumentMetaData
                .builder()
                .filename(fileName)
                .contentType(contentType)
                .status(DocumentStatus.UPLOADING)
                .fileSize(file.getSize())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();


        documentMetadata = documentMetaDataRepo.save(documentMetadata);

        List<Document> parsedDocs = null;

        int chunksCreated = 0;

        try {
            //parse the file
            parsedDocs = parserService.parse(file);

            //ingest service
            chunksCreated = ingestionService.ingest(documentMetadata, parsedDocs);
        } catch (
                DocumentProcessingException e
        ) {
            logs.info("Document metadata deleting due to fail processing");
            documentMetaDataRepo.delete(documentMetadata);
            throw e;
        }


        //documentMetadata.setTotalChunks(chunksCreated);
        //save the document metadata


        return DocumentResponseDto.builder()
                .id(documentMetadata.getId())
                .fileName(documentMetadata.getFilename())
                .fileSize(documentMetadata.getFileSize())
                .chunksCreated(chunksCreated)
                .status(documentMetadata.getStatus())
                .message("Document successfully processed and indexed.")
                .userId(user.getId())
                .build();


    }

}
