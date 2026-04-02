package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.request.LoginRequest;
import com.farma.treinamentos.dto.response.AuthResponse;
import com.farma.treinamentos.exception.BusinessException;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.model.enums.StatusUtilizador;
import com.farma.treinamentos.repository.UtilizadorRepository;
import com.farma.treinamentos.util.HashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(UtilizadorRepository utilizadorRepository, JwtService jwtService, AuditService auditService) {
        this.utilizadorRepository = utilizadorRepository;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @Value("${app.jwt.expiration:900000}")
    private long jwtExpiration;

    @Transactional
    public AuthResponse login(LoginRequest request, String ip) {
        Utilizador utilizador = utilizadorRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!HashUtil.matches(request.senha(), utilizador.getSenhaHash())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        if (utilizador.getStatus() != StatusUtilizador.ATIVO) {
            throw new BusinessException("Utilizador inativo ou bloqueado");
        }

        utilizador.setUltimoLogin(LocalDateTime.now());
        utilizadorRepository.save(utilizador);

        String token = jwtService.generateToken(utilizador);
        
        auditService.log(utilizador.getId(), ip, "utilizadores", utilizador.getId(), 
                com.farma.treinamentos.model.enums.AcaoAudit.LOGIN, null, null, "Login bem-sucedido");

        return new AuthResponse(
                token,
                "Bearer",
                jwtExpiration,
                new AuthResponse.UtilizadorResponse(
                    utilizador.getId(),
                    utilizador.getNomeCompleto(),
                    utilizador.getEmail(),
                    utilizador.getNivelAcesso().name()
                )
        );
    }

    public void logout(String userId, String ip) {
        auditService.log(userId, ip, "utilizadores", userId, 
                com.farma.treinamentos.model.enums.AcaoAudit.LOGOUT, null, null, "Logout");
    }
}