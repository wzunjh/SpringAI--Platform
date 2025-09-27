package com.example.springai.controller;


import com.example.springai.entity.vo.Result;
import com.example.springai.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/pdf")
public class PdfController {

    private final FileRepository fileRepository;

    private final VectorStore vectorStore;


//    文件上传
    @RequestMapping("/upload/{chatId}")
    public Result uploadPdf(@PathVariable String chatId, @RequestParam("file") MultipartFile file){
        try {

            if (!Objects.equals(file.getContentType(), "application/pdf")){
                return Result.fail("Only PDF files are allowed");
            }

            boolean success = fileRepository.save(chatId,file.getResource());

            if (! success){
                return Result.fail("Failed to save PDF file");
            }

            this.writeToVectorStore(file.getResource());
            return Result.ok();

        } catch (Exception e) {
            log.error("Failed to upload PDF file",e);
            return Result.fail("Failed to upload PDF file");
        }
    }

//    文件下载
    @GetMapping("/file/{chatId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("chatId") String chatId){
        Resource resource = fileRepository.getFile(chatId);
        if (! resource.exists()){
            return ResponseEntity.notFound().build();
        }
        String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()),StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }





    private void writeToVectorStore(Resource resource) {

        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                resource,
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1)
                        .build()
        );

        List<Document> documents = reader.read();

        vectorStore.add(documents);


    }


}
