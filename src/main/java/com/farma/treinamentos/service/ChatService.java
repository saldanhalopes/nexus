package com.farma.treinamentos.service;

import com.farma.treinamentos.model.ChatMessage;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.repository.ChatMessageRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final MinMaxAiService minMaxAiService;

    public ChatService(ChatMessageRepository chatMessageRepository, 
                       UtilizadorRepository utilizadorRepository, 
                       MinMaxAiService minMaxAiService) {
        this.chatMessageRepository = chatMessageRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.minMaxAiService = minMaxAiService;
    }

    public List<ChatMessage> getHistory(String userId) {
        return chatMessageRepository.findByUtilizadorIdOrderByCreatedAtAsc(userId);
    }

    @Transactional
    public Map<String, Object> sendMessage(String userId, String message) {
        log.info("Processing chat message from user {}", userId);
        
        Utilizador utilizador = utilizadorRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        ChatMessage userMessage = new ChatMessage();
        userMessage.setUtilizador(utilizador);
        userMessage.setRole(ChatMessage.ChatRole.USER);
        userMessage.setContent(message);
        chatMessageRepository.save(userMessage);

        String aiResponse = minMaxAiService.sendMessage(message, userId);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setUtilizador(utilizador);
        assistantMessage.setRole(ChatMessage.ChatRole.ASSISTANT);
        assistantMessage.setContent(aiResponse);
        chatMessageRepository.save(assistantMessage);

        Map<String, Object> result = new HashMap<>();
        result.put("userMessage", userMessage);
        result.put("assistantMessage", assistantMessage);
        result.put("response", aiResponse);

        return result;
    }

    public void clearHistory(String userId) {
        chatMessageRepository.deleteByUtilizadorId(userId);
        log.info("Cleared chat history for user {}", userId);
    }
}
