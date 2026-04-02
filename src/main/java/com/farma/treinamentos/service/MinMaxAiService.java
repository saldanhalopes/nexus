package com.farma.treinamentos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MinMaxAiService {

    private static final Logger log = LoggerFactory.getLogger(MinMaxAiService.class);

    @Value("${antygravity.api.url:https://api.minimax.chat/v1}")
    private String apiBaseUrl;
    
    @Value("${antygravity.api.key:}")
    private String apiKey;
    
    @Value("${antygravity.api.model:minimax-m2.5}")
    private String model;

    public String sendMessage(String message, String userId) {
        log.info("Sending message to MinMax AI for user {}", userId);
        
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("MinMax API key not configured, returning fallback response");
            return getFallbackResponse(message);
        }

        try {
            String systemPrompt = getSystemPrompt();
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", message)
            );
            requestBody.put("messages", messages);
            
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = apiBaseUrl + "/chat/completions";
            log.debug("Calling MinMax API at {}", url);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = (Map<String, Object>) response.getBody();
                List<?> choices = body != null ? (List<?>) body.get("choices") : null;
                if (choices != null && !choices.isEmpty()) {
                    Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                    if (choice != null) {
                        Map<?, ?> messageResp = (Map<?, ?>) choice.get("message");
                        if (messageResp != null) {
                            String content = (String) messageResp.get("content");
                            log.info("Received response from MinMax AI");
                            return content;
                        }
                    }
                }
            }
            
            return getFallbackResponse(message);
            
        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Error calling MinMax API: {}", e.getMessage(), e);
            return getFallbackResponse(message);
        } catch (Exception e) {
            log.error("Unexpected error in MinMax AI service: {}", e.getMessage(), e);
            return getFallbackResponse(message);
        }
    }

    private String getSystemPrompt() {
        return """
            Você é um assistente especializado em gestão de competências e treinamentos corporativos.
            Seu papel é ajudar colaboradores e gestores a entenderem:
            
            - Matrizes de competências por cargo
            - Treinamentos obrigatórios e opcionais
            - Status e progresso de treinamentos
            - Requisitos de compliance e certificação
            - Políticas de desenvolvimento profissional
            
            Responda de forma objetiva, clara e amigável. Quando apropriado, sugira próximos passos.
            Se não souber a resposta, seja honesto e redirecione para o RH ou administrador do sistema.
            """;
    }

    private String getFallbackResponse(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("treinamento") || lowerMessage.contains("curso")) {
            return "Para informações sobre treinamentos, acesso o menu 'Meus Treinamentos' no sistema. " +
                   "Lá você podrá ver seus treinamentos pendentes, em andamento e concluídos. " +
                   "Se precisar de ajuda, entre em contato com o RH.";
        } else if (lowerMessage.contains("competência") || lowerMessage.contains("habilidade")) {
            return "As competências são definidas por cargo. Cada cargo possui um conjunto de competências " +
                   "necessárias com níveis mínimos requeridos. Consulte a matriz de competências no menu apropriado.";
        } else if (lowerMessage.contains("cargo") || lowerMessage.contains("função")) {
            return "Os cargos são gerenciados pelo RH/Administração. Cada cargo possui competências específicas " +
                   "e treinamentos associados. Fale com seu gestor para mais informações.";
        } else if (lowerMessage.contains("certificado") || lowerMessage.contains("certificação")) {
            return "Os certificados são emitidos após a conclusão dos treinamentos com nota mínima. " +
                   "Você pode fazer upload do comprovante na seção de treinamentos concluídos.";
        }
        
        return "Olá! Sou o assistente de gestão de competências. Posso ajudar com dúvidas sobre " +
               "treinamentos, competências, cargos e políticas do sistema. " +
               "Em que posso ajudar?";
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
