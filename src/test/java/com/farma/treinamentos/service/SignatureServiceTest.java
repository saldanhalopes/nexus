package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.request.SignatureRequest;
import com.farma.treinamentos.model.AssinaturaEletronica;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.model.enums.TipoAssinatura;
import com.farma.treinamentos.repository.AssinaturaEletronicaRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignatureServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AssinaturaEletronicaRepository signatureRepository;

    @Mock
    private UtilizadorRepository utilizadorRepository;

    @InjectMocks
    private SignatureService signatureService;

    private Utilizador testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new Utilizador();
        testUser.setId("user-123");
        testUser.setNomeCompleto("John Doe");
        testUser.setSenhaHash("encryptedPassword");
    }

    @Test
    void createSignature_Success() {
        SignatureRequest request = new SignatureRequest();
        request.setPassword("password123");
        request.setTargetId("matrix-456");
        request.setTipo(TipoAssinatura.ELABORACAO);
        request.setSignificado("Elaborado por");

        when(passwordEncoder.matches("password123", "encryptedPassword")).thenReturn(true);
        when(signatureRepository.save(any(AssinaturaEletronica.class))).thenAnswer(i -> i.getArguments()[0]);

        AssinaturaEletronica result = signatureService.createSignature(request, testUser, "127.0.0.1");

        assertNotNull(result);
        assertEquals("John Doe", result.getNomeSignatario());
        assertEquals("Elaborado por", result.getSignificado());
        assertNotNull(result.getConteudoHash());
        verify(passwordEncoder).matches("password123", "encryptedPassword");
        verify(signatureRepository).save(any(AssinaturaEletronica.class));
    }

    @Test
    void createSignature_WrongPassword_ThrowsException() {
        SignatureRequest request = new SignatureRequest();
        request.setPassword("wrongPassword");

        when(passwordEncoder.matches("wrongPassword", "encryptedPassword")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> 
            signatureService.createSignature(request, testUser, "127.0.0.1")
        );

        assertTrue(exception.getMessage().contains("Senha incorreta"));
    }

    @Test
    void verifySignature_Success() {
        when(utilizadorRepository.findById("user-123")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encryptedPassword")).thenReturn(true);
        when(signatureRepository.save(any(AssinaturaEletronica.class))).thenAnswer(i -> i.getArguments()[0]);

        AssinaturaEletronica result = signatureService.verifySignature("user-123", "password123", "127.0.0.1", "Responsável", "Motivo");

        assertNotNull(result);
        assertEquals("John Doe", result.getNomeSignatario());
        assertEquals("Responsável", result.getSignificado());
    }
}
