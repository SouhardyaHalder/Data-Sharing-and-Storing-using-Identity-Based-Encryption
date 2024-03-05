package com.souhardya.DocumentManager;

import com.souhardya.DocumentManager.Repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DocumentManagerApplicationTests {


	@Autowired
	private  DocumentRepository documentRepository;

	@Autowired
	private TestEntityManager testEntityManager;
	@Test
	@Rollback(false)
	void testInsertDocument() throws IOException {
		File file=new File("D:\\Files\\IBE Report.docx");
		Document document=new Document();
		document.setName(file.getName());

		byte[] bytes= Files.readAllBytes((file.toPath()));
		long fileSize=bytes.length;
		document.setContent(bytes);
		document.setSize(fileSize);
		document.setUploadTime(new Date());
		Document savesDoc=documentRepository.save(document);
		Document existDoc=testEntityManager.find(Document.class,savesDoc.getId());

		assertThat(existDoc.getSize()).isEqualTo(fileSize);


	}

}
