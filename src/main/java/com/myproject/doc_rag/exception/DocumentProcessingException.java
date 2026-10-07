package com.myproject.doc_rag.exception;

public class DocumentProcessingException extends RuntimeException{

    public DocumentProcessingException(String message){
        super(message);
    }

    public DocumentProcessingException(){
        super("Error in Processing Documents !");
    }

    public DocumentProcessingException(String message,Throwable ex){
        super(message,ex);
    }

}
