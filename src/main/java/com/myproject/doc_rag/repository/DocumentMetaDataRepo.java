package com.myproject.doc_rag.repository;


import com.myproject.doc_rag.entity.DocumentMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentMetaDataRepo extends JpaRepository<DocumentMetaData, UUID> {
}
