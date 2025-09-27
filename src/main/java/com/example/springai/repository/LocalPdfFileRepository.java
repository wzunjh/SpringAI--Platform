package com.example.springai.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPdfFileRepository implements FileRepository{

    private final Properties chatFiles = new Properties();


    @Override
    public boolean save(String chatId, Resource resource) {
        String filename = resource.getFilename();
        File target = new File(Objects.requireNonNull(filename));
        if (!target.exists()){
            try {
                Files.copy(resource.getInputStream(),target.toPath());
            } catch (IOException e) {
                log.error("Failed to save PDF resource",e);
                return  false;
            }
        }
        chatFiles.put(chatId,filename);
        return false;
    }

    @Override
    public Resource getFile(String chatId) {
        return new FileSystemResource(chatFiles.getProperty(chatId));
    }


//  持久化处理
    @PostConstruct
    private void init() {
        FileSystemResource pdfResource = new FileSystemResource("chat-pdf.properties");
        if (pdfResource.exists()) {
            try {
                chatFiles.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 使用 Qdrant：去掉 SimpleVectorStore 的加载
    }

    @PreDestroy
    private void persistent() {
        try {
            chatFiles.store(new FileWriter("chat-pdf.properties"), LocalDateTime.now().toString());
            // 使用 Qdrant：去掉 SimpleVectorStore 的保存
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
