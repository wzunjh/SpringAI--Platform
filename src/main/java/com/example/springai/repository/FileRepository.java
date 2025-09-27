package com.example.springai.repository;

import org.springframework.core.io.Resource;

public interface FileRepository {

//    保存文件并记录chatid与文件的映射关系

    boolean save(String chatId, Resource resource);

//    根据chatid获取文件

    Resource getFile(String chatId);

}
