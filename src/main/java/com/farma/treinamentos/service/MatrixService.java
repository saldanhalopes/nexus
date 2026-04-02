package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.request.MatrizCompetenciaRequest;
import com.farma.treinamentos.dto.response.AssinaturaEletronicaResponse;
import com.farma.treinamentos.dto.response.MatrizCompetenciaResponse;
import com.farma.treinamentos.exception.ResourceNotFoundException;
import com.farma.treinamentos.model.AssinaturaEletronica;
import com.farma.treinamentos.model.Cargo;
import com.farma.treinamentos.model.Departamento;
import com.farma.treinamentos.model.MatrizCompetencia;
import com.farma.treinamentos.repository.CargoRepository;
import com.farma.treinamentos.repository.DepartamentoRepository;
import com.farma.treinamentos.repository.MatrizCompetenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatrixService {

    private final MatrizCompetenciaRepository matrizRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CargoRepository cargoRepository;
    private final AuditService auditService;
    private final SignatureService signatureService;

    public MatrixService(MatrizCompetenciaRepository matrizRepository,
                         DepartamentoRepository departamentoRepository,
                         CargoRepository cargoRepository,
                         AuditService auditService,
                         SignatureService signatureService) {
        this.matrizRepository = matrizRepository;
        this.departamentoRepository = departamentoRepository;
        this.cargoRepository = cargoRepository;
        this.auditService = auditService;
        this.signatureService = signatureService;
    }

    public List<MatrizCompetenciaResponse> findAll() {
        return matrizRepository.findByAtivaTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public MatrizCompetenciaResponse findById(String id) {
        MatrizCompetencia matriz = matrizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matriz de competência não encontrada"));
        return toResponse(matriz);
    }

    public List<MatrizCompetenciaResponse> findByDepartment(String departamentoId) {
        return matrizRepository.findByDepartamentoId(departamentoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MatrizCompetenciaResponse> findByCargo(String cargoId) {
        return matrizRepository.findByCargoId(cargoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MatrizCompetenciaResponse create(MatrizCompetenciaRequest request, String userId, String ip) {
        AssinaturaEletronica signature = signatureService.verifySignature(userId, request.senha(), ip, "Elaboração da Matriz", request.motivo());
        
        MatrizCompetencia matriz = new MatrizCompetencia();
        matriz.setNome(request.nome());
        matriz.setDescricao(request.descricao());
        matriz.setAnoReferencia(request.anoReferencia());
        matriz.setCompetencias(request.competencias());
        matriz.setAssinaturaElaboracao(signature);
        matriz.setAtiva(true);

        if (request.departamentoId() != null) {
            Departamento departamento = departamentoRepository.findById(request.departamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
            matriz.setDepartamento(departamento);
        }

        if (request.cargoId() != null) {
            Cargo cargo = cargoRepository.findById(request.cargoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));
            matriz.setCargo(cargo);
        }

        MatrizCompetencia saved = matrizRepository.save(matriz);
        
        auditService.log(userId, ip, "matrizes_competencia", saved.getId(), 
                com.farma.treinamentos.model.enums.AcaoAudit.INSERT, null, saved, "Criação de matriz");
        
        return toResponse(saved);
    }

    @Transactional
    public MatrizCompetenciaResponse update(String id, MatrizCompetenciaRequest request, String userId, String ip) {
        AssinaturaEletronica signature = signatureService.verifySignature(userId, request.senha(), ip, "Revisão da Matriz", request.motivo());

        MatrizCompetencia matriz = matrizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matriz de competência não encontrada"));

        if (request.nome() != null) matriz.setNome(request.nome());
        if (request.descricao() != null) matriz.setDescricao(request.descricao());
        matriz.setAnoReferencia(request.anoReferencia());
        if (request.competencias() != null) matriz.setCompetencias(request.competencias());
        
        matriz.setAssinaturaRevisao(signature);

        MatrizCompetencia saved = matrizRepository.save(matriz);
        
        auditService.log(userId, ip, "matrizes_competencia", saved.getId(), 
                com.farma.treinamentos.model.enums.AcaoAudit.UPDATE, null, saved, "Atualização de matriz");
        
        return toResponse(saved);
    }

    @Transactional
    public void delete(String id, String senha, String motivo, String userId, String ip) {
        signatureService.verifySignature(userId, senha, ip, "Desativar Matriz", motivo);

        MatrizCompetencia matriz = matrizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matriz de competência não encontrado"));
        
        matriz.setAtiva(false);
        matrizRepository.save(matriz);
        
        auditService.log(userId, ip, "matrizes_competencia", id, 
                com.farma.treinamentos.model.enums.AcaoAudit.DELETE, matriz, null, "Desativação de matriz");
    }

    private MatrizCompetenciaResponse toResponse(MatrizCompetencia matriz) {
        return new MatrizCompetenciaResponse(
            matriz.getId(),
            matriz.getNome(),
            matriz.getDescricao(),
            matriz.getAnoReferencia(),
            matriz.getCompetencias(),
            matriz.getDepartamento() != null ? matriz.getDepartamento().getNome() : null,
            matriz.getCargo() != null ? matriz.getCargo().getNome() : null,
            matriz.isAtiva(),
            matriz.getCreatedAt(),
            toSignatureResponse(matriz.getAssinaturaElaboracao()),
            toSignatureResponse(matriz.getAssinaturaRevisao()),
            toSignatureResponse(matriz.getAssinaturaAprovacao())
        );
    }

    private AssinaturaEletronicaResponse toSignatureResponse(AssinaturaEletronica s) {
        if (s == null) return null;
        return new AssinaturaEletronicaResponse(
            s.getId(),
            s.getNomeSignatario(),
            s.getTipo() != null ? s.getTipo().name() : null,
            s.getDataHora(),
            s.getSignificado()
        );
    }
}