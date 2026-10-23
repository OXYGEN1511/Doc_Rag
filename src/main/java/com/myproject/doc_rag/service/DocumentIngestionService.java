package com.myproject.doc_rag.service;

import com.myproject.doc_rag.config.AppProperties;
import com.myproject.doc_rag.entity.DocumentMetaData;
import com.myproject.doc_rag.entity.DocumentStatus;
import com.myproject.doc_rag.exception.DocumentProcessingException;
import com.myproject.doc_rag.repository.DocumentMetaDataRepo;
import org.springframework.ai.vectorstore.VectorStore;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService {
    private static final Logger logs = LoggerFactory.getLogger(DocumentIngestionService.class);
    private final VectorStore vectorStore;
    private final DocumentMetaDataRepo documentMetadataRepo;
    private final AppProperties appProperties;


    public int ingest(DocumentMetaData documentMetadata, List<Document> parsedDocs) {
        logs.info("Ingesting document [id={}, name={}, pages={}]", documentMetadata.getId(),documentMetadata.getFilename(), documentMetadata.getFileSize());

        try{
            documentMetadata.setStatus(DocumentStatus.PROCESSING);
            documentMetadata.setTotalPages(parsedDocs.size());

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                    .withChunkSize(appProperties.getRag().getChunkize()).withMinChunkSizeChars(appProperties.getRag()
                    .getMinChunkSizeChars()).withMaxNumChunks(appProperties.getRag()
                            .getMinChunkLengthToEmbed()).withMaxNumChunks(appProperties
                            .getRag().getMaxNumChunks())
                    .withKeepSeparator(true).build();

            List<Document> chunks = tokenTextSplitter.apply(parsedDocs);
            if(chunks.isEmpty()){
                documentMetadata.setStatus(DocumentStatus.FAILED);
                documentMetadata.setErrorMessage("Document appears to be empty or unscannable");
                documentMetadataRepo.save(documentMetadata);
                return 0;
            }

            List<Document> enrichedChunks = new ArrayList<>();
            for(int i =0; i< chunks.size();i++){
                Document chunk = chunks.get(i);
                Map<String,Object> enrichedMetadata = new HashMap<>(chunk.getMetadata());
                enrichedMetadata.put("documentId", documentMetadata.getId());
                enrichedMetadata.put("filename", documentMetadata.getFilename());
                enrichedMetadata.put("contentType",documentMetadata.getContentType());
                enrichedMetadata.put("chunkIndex",i);
                enrichedMetadata.put("userId",documentMetadata.getUser().getId().toString());
               Object pageNumber = chunk.getMetadata().get("page_number");
               if(pageNumber == null){
                   pageNumber = chunk.getMetadata().get("pageNumber");
               }
               if(pageNumber!=null){
                   enrichedMetadata.put("pageNumber",pageNumber);
               }
               Document enrichedDoc = new Document(chunk.getText(), enrichedMetadata);
               enrichedChunks.add(enrichedDoc);
            }
            logs.info("Writing {} vector chunks to PgVectorStore for document: {}", enrichedChunks.size(), documentMetadata.getFilename());
            vectorStore.add(enrichedChunks);

            documentMetadata.setStatus(DocumentStatus.INDEXED);
            documentMetadata.setTotalChunks(enrichedChunks.size());
            documentMetadata.setErrorMessage(null);
            documentMetadataRepo.save(documentMetadata);
            logs.info("Successfully indexed document [id={}, name={}, chunks={}]", documentMetadata.getId(), documentMetadata.getFilename(), enrichedChunks.size());

            return enrichedChunks.size();

        } catch (Exception ex){
            logs.error("Failed to ingest document into vector store: {}", documentMetadata.getFilename(), ex);
            documentMetadata.setStatus(DocumentStatus.FAILED);
            documentMetadata.setErrorMessage(ex.getMessage());
            documentMetadataRepo.save(documentMetadata);
            throw new DocumentProcessingException("Failed to index document: " + ex.getMessage(), ex);
        }

    }
}
