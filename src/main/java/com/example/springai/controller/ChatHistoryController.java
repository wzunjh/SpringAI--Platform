package com.example.springai.controller;

import com.example.springai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {

    private final ChatHistoryRepository chatHistoryRepository;

    @GetMapping("/{type}")
    public List<String> getChatIds(@PathVariable("type") String type){

        return chatHistoryRepository.getChatIds(type);

    }




}
