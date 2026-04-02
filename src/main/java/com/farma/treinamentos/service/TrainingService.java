package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.request.TreinamentoRequest;
import com.farma.treinamentos.dto.response.TreinamentoResponse;
import com.farma.treinamentos.exception.ResourceNotFoundException;
import com.farma.treinamentos.model.Departamento;
import com.farma.treinamentos.model.Treinamento;
import com.farma.treinamentos.repository.DepartamentoRepository;
import com.farma.treinamentos.repository.TreinamentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingService {

    private final TreinamentoRepository treinamentoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final AuditService auditService;

    public TrainingService(
            TreinamentoRepository treinamentoRepository,
            DepartamentoRepository departamentoRepository,
            AuditService auditService) {
        this.treinamentoRepository = treinamentoRepository;
        this.departamentoRepository = departamentoRepository;
        this.auditService = auditService;
    }

    public Page<TreinamentoResponse> findAll(Pageable pageable) {
        return treinamentoRepository.findByAtivoTrue(pageable).map(this::toResponse);
    }

    public List<TreinamentoResponse> findAll() {
        return treinamentoRepository.findByAtivoTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public TreinamentoResponse findById(String id) {
        Treinamento treinamento = treinamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado"));
        return toResponse(treinamento);
    }

    public List<TreinamentoResponse> findByDepartment(String departamentoId) {
        return treinamentoRepository.findAtivosByDepartamento(departamentoId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<TreinamentoResponse> findObrigatorios() {
        return treinamentoRepository.findObrigatorios().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TreinamentoResponse create(TreinamentoRequest request, String ip) {
        Treinamento treinamento = new Treinamento();
        treinamento.setTitulo(request.titulo());
        treinamento.setDescricao(request.descricao());
        treinamento.setCargaHoraria(request.cargaHoraria());
        treinamento.setTipo(request.tipo());
        treinamento.setNomePlataforma(request.nomePlataforma());
        treinamento.setLinkPlataforma(request.linkPlataforma());
        treinamento.setConteudoProgramatico(request.conteudoProgramatico());
        treinamento.setObrigatorio(Boolean.TRUE.equals(request.obrigatorio()));
        treinamento.setAtivo(true);

        if (request.departamentoId() != null) {
            Departamento departamento = departamentoRepository.findById(request.departamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
            treinamento.setDepartamento(departamento);
        }

        Treinamento saved = treinamentoRepository.save(treinamento);
        
        auditService.log(null, ip, "treinamentos", saved.getId(), 
                com.farma.treinamentos.model.enums.AcaoAudit.INSERT, null, saved, "Criação de treinamento");
        
        return toResponse(saved);
    }

    @Transactional
    public TreinamentoResponse update(String id, TreinamentoRequest request, String ip) {
        Treinamento treinamento = treinamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado"));

        if (request.titulo() != null) treinamento.setTitulo(request.titulo());
        if (request.descricao() != null) treinamento.setDescricao(request.descricao());
        if (request.cargaHoraria() != null) treinamento.setCargaHoraria(request.cargaHoraria());
        if (request.tipo() != null) treinamento.setTipo(request.tipo());
        if (request.nomePlataforma() != null) treinamento.setNomePlataforma(request.nomePlataforma());
        if (request.linkPlataforma() != null) treinamento.setLinkPlataforma(request.linkPlataforma());
        if (request.conteudoProgramatico() != null) treinamento.setConteudoProgramatico(request.conteudoProgramatico());
        if (request.obrigatorio() != null) {
            treinamento.setObrigatorio(request.obrigatorio());
        }
        if (request.departamentoId() != null) {
            Departamento departamento = departamentoRepository.findById(request.departamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
            treinamento.setDepartamento(departamento);
        }

        Treinamento saved = treinamentoRepository.save(treinamento);
        
        auditService.log(null, ip, "treinamentos", saved.getId(), 
                com.farma.treinamentos.model.enums.AcaoAudit.UPDATE, null, saved, "Atualização de treinamento");
        
        return toResponse(saved);
    }

    @Transactional
    public void delete(String id, String ip) {
        Treinamento treinamento = treinamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treinamento não encontrado"));
        
        treinamento.setAtivo(false);
        treinamentoRepository.save(treinamento);
        
        auditService.log(null, ip, "treinamentos", id, 
                com.farma.treinamentos.model.enums.AcaoAudit.DELETE, treinamento, null, "Desativação de treinamento");
    }

    private TreinamentoResponse toResponse(Treinamento treinamento) {
        return new TreinamentoResponse(
            treinamento.getId(),
            treinamento.getTitulo(),
            treinamento.getDescricao(),
            treinamento.getCargaHoraria(),
            treinamento.getTipo(),
            treinamento.getNomePlataforma(),
            treinamento.getLinkPlataforma(),
            treinamento.isObrigatorio(),
            treinamento.getDepartamento() != null ? treinamento.getDepartamento().getNome() : null,
            treinamento.isAtivo(),
            treinamento.getCreatedAt()
        );
    }
}