package com.farma.treinamentos.service;

import com.farma.treinamentos.model.*;
import com.farma.treinamentos.model.enums.StatusTreinamento;
import com.farma.treinamentos.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingCascadeService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TrainingCascadeService.class);

    private final CargoCompetenciaRepository cargoCompetenciaRepository;
    private final CompetenciaTreinamentoRepository competenciaTreinamentoRepository;
    private final RegistroTreinamentoRepository registroTreinamentoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final AuditService auditService;

    public TrainingCascadeService(
            CargoCompetenciaRepository cargoCompetenciaRepository,
            CompetenciaTreinamentoRepository competenciaTreinamentoRepository,
            RegistroTreinamentoRepository registroTreinamentoRepository,
            UtilizadorRepository utilizadorRepository,
            AuditService auditService) {
        this.cargoCompetenciaRepository = cargoCompetenciaRepository;
        this.competenciaTreinamentoRepository = competenciaTreinamentoRepository;
        this.registroTreinamentoRepository = registroTreinamentoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.auditService = auditService;
    }

    @Transactional
    public List<RegistroTreinamento> applyRoleTrainingsToUser(String userId, String roleId, String ip) {
        log.info("Applying trainings for user {} with role {}", userId, roleId);

        Utilizador utilizador = utilizadorRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        List<CargoCompetencia> competencias = cargoCompetenciaRepository.findByCargoId(roleId);
        
        if (competencias.isEmpty()) {
            log.info("No competencies found for role {}", roleId);
            return List.of();
        }

        List<RegistroTreinamento> createdTrainings = new ArrayList<>();

        for (CargoCompetencia cargoCompetencia : competencias) {
            Long competenciaId = cargoCompetencia.getCompetencia().getId();
            
            List<CompetenciaTreinamento> competenciasTreinamentos = 
                competenciaTreinamentoRepository.findByCompetenciaId(competenciaId);

            for (CompetenciaTreinamento ct : competenciasTreinamentos) {
                Treinamento treinamento = ct.getTreinamento();
                
                boolean exists = registroTreinamentoRepository
                    .findByUtilizadorIdAndTreinamentoId(utilizador.getId(), treinamento.getId())
                    .isPresent();

                if (!exists) {
                    RegistroTreinamento registro = new RegistroTreinamento();
                    registro.setUtilizador(utilizador);
                    registro.setTreinamento(treinamento);
                    registro.setStatus(StatusTreinamento.PENDENTE);
                    registro.setNotaMinima(70.0);
                    
                    RegistroTreinamento saved = registroTreinamentoRepository.save(registro);
                    createdTrainings.add(saved);
                    
                    log.info("Created training {} for user {}", treinamento.getTitulo(), utilizador.getEmail());
                }
            }
        }

        if (!createdTrainings.isEmpty()) {
            auditService.log(utilizador.getId(), ip, "registros_treinamento", utilizador.getId(),
                    com.farma.treinamentos.model.enums.AcaoAudit.INSERT, null, createdTrainings,
                    "Cascata de treinamentos aplicada para cargo " + roleId);
        }

        log.info("Created {} trainings for user {}", createdTrainings.size(), userId);
        return createdTrainings;
    }
}
