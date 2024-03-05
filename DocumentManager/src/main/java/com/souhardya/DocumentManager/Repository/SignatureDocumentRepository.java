package com.souhardya.DocumentManager.Repository;

import com.souhardya.DocumentManager.SignatureDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignatureDocumentRepository extends JpaRepository<SignatureDocument, Long > {

    @Query("SELECT new SignatureDocument (d.id,d.name,d.size) FROM SignatureDocument d")
    List<SignatureDocument> findAll();

}
