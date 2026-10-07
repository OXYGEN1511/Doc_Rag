package com.myproject.doc_rag.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private Boolean success;
    private String messsage;
    private T data;
    private LocalDateTime timestamp;

}
