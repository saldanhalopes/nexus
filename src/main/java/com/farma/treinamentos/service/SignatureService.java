package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.request.SignatureRequest;
import com.farma.treinamentos.model.AssinaturaEletronica;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.repository.AssinaturaEletronicaRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Service for handling FDA 21 CFR Part 11 compliant electronic signatures.
 * Enforces re-authentication and content integrity via hashing.
 */
@Service
public class SignatureService {

    private final PasswordEncoder passwordEncoder;
    private final AssinaturaEletronicaRepository signatureRepository;
    private final UtilizadorRepository utilizadorRepository;

    public SignatureService(PasswordEncoder passwordEncoder, 
                            AssinaturaEletronicaRepository signatureRepository,
                            UtilizadorRepository utilizadorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.signatureRepository = signatureRepository;
        this.utilizadorRepository = utilizadorRepository;
    }

    @Transactional
    public AssinaturaEletronica createSignature(SignatureRequest request, Utilizador user, String ipOrigem) {
        // 1. Verify Re-authentication (21 CFR Part 11 Requirement)
        verifyPassword(request.getPassword(), user);

        // 2. Generate Content Hash (Integrity verification)
        String contentToHash = user.getId() + ":" + request.getTargetId() + ":" + 
                              (request.getTipo() != null ? request.getTipo().name() : "N/A") + ":" + 
                              request.getSignificado();
        String hash = generateHash(contentToHash);

        // 3. Create and Persist Signature Entity
        AssinaturaEletronica signature = new AssinaturaEletronica();
        signature.setUtilizador(user);
        signature.setNomeSignatario(user.getNomeCompleto());
        signature.setTipo(request.getTipo());
        signature.setSignificado(request.getSignificado());
        signature.setConteudoHash(hash);
        signature.setIpOrigem(ipOrigem);

        return signatureRepository.save(signature);
    }

    /**
     * Convenience method for simple verification with audit record creation.
     */
    @Transactional
    public AssinaturaEletronica verifySignature(String userId, String password, String ip, String significado, String motivo) {
        Utilizador user = utilizadorRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado para assinatura."));

        SignatureRequest request = new SignatureRequest();
        request.setPassword(password);
        request.setSignificado(significado);
        request.setTargetId("N/A"); // In these generic calls, we don't always have a target ID yet

        return createSignature(request, user, ip);
    }

    public void verifyPassword(String password, Utilizador user) {
        if (!passwordEncoder.matches(password, user.getSenhaHash())) {
            throw new RuntimeException("Falha na re-autenticação: Senha incorreta para assinatura eletrônica.");
        }
    }

    /**
     * Generates a SHA-256 hash of the input string.
     */
    private String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da assinatura: " + e.getMessage());
        }
    }
}
