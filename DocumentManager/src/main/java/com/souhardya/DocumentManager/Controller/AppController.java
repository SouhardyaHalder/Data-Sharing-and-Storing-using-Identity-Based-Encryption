package com.souhardya.DocumentManager.Controller;

import com.souhardya.DocumentManager.Document;
import com.souhardya.DocumentManager.IBE.ElementBytePair;
import com.souhardya.DocumentManager.IBE.IBE;
import com.souhardya.DocumentManager.Repository.DocumentRepository;
import it.unisa.dia.gas.jpbc.Element;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

//import static com.souhardya.DocumentManager.IBE.IBE.ciphertextPair;

@Controller
public class AppController {
    //public String userEmail;
    public static Map<Long, ElementBytePair> ciphertextPair = new HashMap<>();

    @Autowired
    private DocumentRepository documentRepository;
    public IBE ibe=new IBE();
    public String senderEmail="";
    @GetMapping("/")
    public String viewHomepage(){
        return "login";
    }
    @GetMapping("/uploaded-files")
    public String uploadedFile(Model model){
        List<Document> listDocs=documentRepository.findAll();
        model.addAttribute("listDocs",listDocs);
        return "uploadedfiles";
    }

    @GetMapping("/logout")
    public String loggedOut(){return "login";}

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file")MultipartFile file, @RequestParam("recipientEmail") String recipientEmail,RedirectAttributes ra) throws Exception {
        String fileName= StringUtils.cleanPath(file.getOriginalFilename());
        Document document=new Document();

        document.setName(fileName);
        byte[] fileBytes=file.getBytes();

        ibe.setup(recipientEmail);
        ElementBytePair C1C2=ibe.encryption(fileBytes,recipientEmail);
        byte[] encryptedFileBytes=C1C2.getByteArray();

        //byte[] encryptedFileBytes=ibe.encryption(fileBytes,recipientEmail);
        document.setContent(encryptedFileBytes);
        document.setSize(file.getSize());
        document.setUploadTime(new Date());
        document.setReceipientEmail(recipientEmail);
        if(senderEmail!="")document.setSenderEmail(senderEmail);

        documentRepository.save(document);

        ra.addFlashAttribute("message","the file has been uploaded successfully");
        System.out.println("this is the primary key "+document.getId());
        ciphertextPair.put(document.getId(),new ElementBytePair(C1C2.getElement(), C1C2.getByteArray()));
        return "home";
    }

    @GetMapping("/download")
    public void downloadFile(@Param("id")Long id,
                             HttpServletResponse response) throws Exception {
        Optional<Document> result=documentRepository.findById(id);
        if(!result.isPresent()){
            throw new Exception("could not find the file" + id);
        }
        Document document=result.get();

        response.setContentType("application/octet-stream");
        String headerKey="Content-Disposition";
        String headerValue="attachment; filename="+document.getName();
        response.setHeader(headerKey,headerValue);


        ElementBytePair userPair=ciphertextPair.get(document.getId());
        Element C1=userPair.getElement();
        byte[] C2=userPair.getByteArray();

        ServletOutputStream outputStream = response.getOutputStream();

        byte[] decryptedFileByes=ibe.decryption(C1,C2,senderEmail);
        outputStream.write(decryptedFileByes);
        outputStream.close();
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username){
        this.senderEmail=username;
        return "home";
    }
    @GetMapping("/digitalSignature")
    public String Signature(){
        return "signature";
    }

    @GetMapping("/returnHome")
    public String returnHome(){return "home";}
}
