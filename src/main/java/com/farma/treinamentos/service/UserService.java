package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.request.UtilizadorRequest;
import com.farma.treinamentos.dto.response.UtilizadorResponse;
import com.farma.treinamentos.exception.BusinessException;
import com.farma.treinamentos.exception.ResourceNotFoundException;
import com.farma.treinamentos.model.Cargo;
import com.farma.treinamentos.model.Departamento;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.model.enums.NivelAcesso;
import com.farma.treinamentos.model.enums.StatusUtilizador;
import com.farma.treinamentos.repository.CargoRepository;
import com.farma.treinamentos.repository.DepartamentoRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import com.farma.treinamentos.util.HashUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {

    private final UtilizadorRepository utilizadorRepository;
    private final CargoRepository cargoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final AuditService auditService;
    private final TrainingCascadeService trainingCascadeService;

    public UserService(
            UtilizadorRepository utilizadorRepository,
            CargoRepository cargoRepository,
            DepartamentoRepository departamentoRepository,
            AuditService auditService,
            TrainingCascadeService trainingCascadeService) {
        this.utilizadorRepository = utilizadorRepository;
        this.cargoRepository = cargoRepository;
        this.departamentoRepository = departamentoRepository;
        this.auditService = auditService;
        this.trainingCascadeService = trainingCascadeService;
    }

    public Page<UtilizadorResponse> findAll(Pageable pageable) {
        return utilizadorRepository.findAll(pageable).map(this::toResponse);
    }

    public UtilizadorResponse findById(String id) {
        Utilizador utilizador = utilizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        return toResponse(utilizador);
    }

    public List<UtilizadorResponse> findByDepartment(String departamentoId) {
        return utilizadorRepository.findByDepartamentoId(departamentoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UtilizadorResponse> findByCargo(String cargoId) {
        return utilizadorRepository.findByCargoId(cargoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UtilizadorResponse create(UtilizadorRequest request, String ip) {
        if (utilizadorRepository.existsByMatriculaOrEmail(request.matricula(), request.email())) {
            throw new BusinessException("Matrícula ou email já cadastrado");
        }

        Utilizador utilizador = new Utilizador();
        utilizador.setMatricula(request.matricula());
        utilizador.setNomeCompleto(request.nomeCompleto());
        utilizador.setEmail(request.email());
        utilizador.setSenhaHash(HashUtil.hashPassword("123456"));
        utilizador.setStatus(StatusUtilizador.ATIVO);
        utilizador.setNivelAcesso(request.nivelAcesso() != null ? request.nivelAcesso() : NivelAcesso.COMUM);

        if (request.cargoId() != null) {
            Cargo cargo = cargoRepository.findById(request.cargoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));
            utilizador.setCargo(cargo);
            utilizador.setDepartamento(cargo.getDepartamento());
        }

        if (request.departamentoId() != null && request.cargoId() == null) {
            Departamento departamento = departamentoRepository.findById(request.departamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
            utilizador.setDepartamento(departamento);
        }

        Utilizador saved = utilizadorRepository.save(utilizador);
        
        if (request.cargoId() != null) {
            trainingCascadeService.applyRoleTrainingsToUser(saved.getId(), request.cargoId(), ip);
        }
        
        auditService.log(saved.getId(), ip, "utilizadores", saved.getId(),  
                com.farma.treinamentos.model.enums.AcaoAudit.INSERT, null, saved, "Criação de utilizador");
        
        return toResponse(saved);
    }

    @Transactional
    public UtilizadorResponse update(String id, UtilizadorRequest request, String ip) {
        Utilizador utilizador = utilizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

        Utilizador oldData = new Utilizador();
        oldData.setNomeCompleto(utilizador.getNomeCompleto());
        oldData.setEmail(utilizador.getEmail());
        oldData.setNivelAcesso(utilizador.getNivelAcesso());
        oldData.setStatus(utilizador.getStatus());

        if (request.nomeCompleto() != null) {
            utilizador.setNomeCompleto(request.nomeCompleto());
        }
        if (request.email() != null && !request.email().equals(utilizador.getEmail())) {
            if (utilizadorRepository.existsByEmail(request.email())) {
                throw new BusinessException("Email já está em uso");
            }
            utilizador.setEmail(request.email());
        }
        if (request.nivelAcesso() != null) {
            utilizador.setNivelAcesso(request.nivelAcesso());
        }
        if (request.status() != null) {
            utilizador.setStatus(request.status());
        }
        if (request.cargoId() != null) {
            Cargo cargo = cargoRepository.findById(request.cargoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado"));
            utilizador.setCargo(cargo);
            utilizador.setDepartamento(cargo.getDepartamento());
        }

        Utilizador saved = utilizadorRepository.save(utilizador);
        
        if (request.cargoId() != null) {
            trainingCascadeService.applyRoleTrainingsToUser(saved.getId(), request.cargoId(), ip);
        }
        
        auditService.log(saved.getId(), ip, "utilizadores", saved.getId(), 
                com.farma.treinamentos.model.enums.AcaoAudit.UPDATE, oldData, saved, "Atualização de utilizador");
        
        return toResponse(saved);
    }

    @Transactional
    public void delete(String id, String ip) {
        Utilizador utilizador = utilizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        
        auditService.log(null, ip, "utilizadores", id, 
                com.farma.treinamentos.model.enums.AcaoAudit.DELETE, utilizador, null, "Exclusão de utilizador");
        
        utilizadorRepository.delete(utilizador);
    }

    private UtilizadorResponse toResponse(Utilizador utilizador) {
        return new UtilizadorResponse(
            utilizador.getId(),
            utilizador.getMatricula(),
            utilizador.getNomeCompleto(),
            utilizador.getEmail(),
            utilizador.getStatus(),
            utilizador.getNivelAcesso(),
            utilizador.getCargo() != null ? utilizador.getCargo().getNome() : null,
            utilizador.getDepartamento() != null ? utilizador.getDepartamento().getNome() : null,
            utilizador.getCreatedAt(),
            utilizador.getUltimoLogin()
        );
    }
}