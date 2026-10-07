package com.myproject.doc_rag.exception;

import com.myproject.doc_rag.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionalHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionalHandler.class);

    @ExceptionHandler(ResourceNotFindException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFindException ex){
        logger.warn("Resource not found"+ex.getCause());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder().success(false).messsage(ex.getMessage()).data(null).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<ApiResponse> handlingProcessingError(DocumentProcessingException ex){
        logger.error("Document Processing errro "+ex.getMessage() , ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ApiResponse.builder().success(false).messsage(ex.getMessage()).data(null).timestamp(LocalDateTime.now()).build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxSize(MaxUploadSizeExceededException ex){
        logger.error("Document Processing errro "+ex.getMessage() , ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ApiResponse.builder().success(false).messsage(ex.getMessage()).data(null).timestamp(LocalDateTime.now()).build());
    }
}
