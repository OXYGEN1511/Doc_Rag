package com.myproject.doc_rag.controller;

//

//import io.swagger.v3.oas.annotations.Operation;
import com.myproject.doc_rag.dto.ApiResponse;
import com.myproject.doc_rag.dto.DocumentResponseDto;
import com.myproject.doc_rag.entity.User;
import com.myproject.doc_rag.service.DocumentMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController()
@RequestMapping("/api/v1/documents")
@Tag(
        name = "Document Management",
        description = "Endpoints for uploading, listing and managing documents and their vectors embeddings."
)
@RequiredArgsConstructor
public class DocumentController {

    private  final DocumentMetadataService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload and index a document(PDF, DOCX, TEXT, MD, CSV)",
            description = "This api is used to upload and index documents files."

    )
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ){
        User user = (User) authentication.getPrincipal();
        DocumentResponseDto documentResponseDto  =  this.documentService.uploadAndProcess(file,user);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<DocumentResponseDto>builder()
                .success(true).data(documentResponseDto).timestamp(LocalDateTime.now()).messsage("Document created Successfully")
                .build());

    }


}
