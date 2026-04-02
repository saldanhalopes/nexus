package com.farma.treinamentos.repository;

import com.farma.treinamentos.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUtilizadorIdOrderByCreatedAtAsc(String utilizadorId);
    void deleteByUtilizadorId(String utilizadorId);
}
