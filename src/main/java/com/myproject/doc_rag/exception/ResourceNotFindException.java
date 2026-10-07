package com.myproject.doc_rag.exception;

public class ResourceNotFindException extends RuntimeException{

    public ResourceNotFindException(String message){
        super(message);
    }

    public ResourceNotFindException(){
        super("Resource you are looking not found");
    }


    public ResourceNotFindException(String message ,Throwable ex ){
        super(message,ex);
    }



}
