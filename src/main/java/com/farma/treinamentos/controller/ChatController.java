package com.farma.treinamentos.controller;

import com.farma.treinamentos.dto.request.ChatMessageRequest;
import com.farma.treinamentos.model.ChatMessage;
import com.farma.treinamentos.service.ChatService;
import com.farma.treinamentos.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatController {

    private final ChatService chatService;
    private final JwtService jwtService;

    public ChatController(ChatService chatService, JwtService jwtService) {
        this.chatService = chatService;
        this.jwtService = jwtService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(chatService.getHistory(userId));
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChatMessageRequest request) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(chatService.sendMessage(userId, request.message()));
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtService.extractUserId(token);
        chatService.clearHistory(userId);
        return ResponseEntity.ok().build();
    }
}
