package com.farma.treinamentos.service;

import com.farma.treinamentos.dto.ImportResult;
import com.farma.treinamentos.exception.BusinessException;
import com.farma.treinamentos.model.Utilizador;
import com.farma.treinamentos.model.enums.AcaoAudit;
import com.farma.treinamentos.model.enums.StatusUtilizador;
import com.farma.treinamentos.repository.CargoRepository;
import com.farma.treinamentos.repository.UtilizadorRepository;
import com.farma.treinamentos.util.HashUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private static final Logger log = LoggerFactory.getLogger(CsvImportService.class);

    private final UtilizadorRepository utilizadorRepository;
    private final CargoRepository cargoRepository;
    private final AuditService auditService;

    public CsvImportService(UtilizadorRepository utilizadorRepository, 
                            CargoRepository cargoRepository, 
                            AuditService auditService) {
        this.utilizadorRepository = utilizadorRepository;
        this.cargoRepository = cargoRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ImportResult importUsers(MultipartFile file, String userId, String ip) {
        ImportResult result = new ImportResult();
        List<String> errors = new ArrayList<>();

        if (file.isEmpty()) {
            throw new BusinessException("Arquivo vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("text/csv")) {
            throw new BusinessException("Tipo de arquivo inválido. Use CSV");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build())) {

            List<String> headers = csvParser.getHeaderNames();
            if (!headers.contains("matricula") || !headers.contains("nomeCompleto") || 
                !headers.contains("email") || !headers.contains("cargo")) {
                throw new BusinessException("Cabeçalho inválido. Necessário: matricula, nomeCompleto, email, cargo");
            }

            for (CSVRecord record : csvParser) {
                result.incrementTotalRows();
                try {
                    String matricula = record.get("matricula");
                    String nomeCompleto = record.get("nomeCompleto");
                    String email = record.get("email");
                    String cargoNome = record.get("cargo");

                    if (matricula == null || matricula.isBlank() || 
                        nomeCompleto == null || nomeCompleto.isBlank() ||
                        email == null || email.isBlank() || cargoNome == null || cargoNome.isBlank()) {
                        throw new BusinessException("Campos obrigatórios ausentes");
                    }

                    if (utilizadorRepository.existsByMatriculaOrEmail(matricula, email)) {
                        throw new BusinessException("Matrícula ou email já cadastrado");
                    }

                    var cargo = cargoRepository.findByNome(cargoNome.trim())
                            .orElseThrow(() -> new BusinessException("Cargo não encontrado: " + cargoNome));

                    Utilizador novoUsuario = new Utilizador();
                    novoUsuario.setMatricula(matricula.trim());
                    novoUsuario.setNomeCompleto(nomeCompleto.trim());
                    novoUsuario.setEmail(email.trim().toLowerCase());
                    novoUsuario.setSenhaHash(HashUtil.hashPassword("123456"));
                    novoUsuario.setCargo(cargo);
                    novoUsuario.setDepartamento(cargo.getDepartamento());
                    novoUsuario.setStatus(StatusUtilizador.ATIVO);

                    utilizadorRepository.save(novoUsuario);

                    auditService.log(userId, ip, "utilizadores", novoUsuario.getId(),
                            AcaoAudit.IMPORT, null, novoUsuario, "Importação em massa");

                    result.incrementSuccessRows();
                } catch (Exception e) {
                    result.incrementFailedRows();
                    errors.add("Linha " + result.getTotalRows() + ": " + e.getMessage());
                    log.warn("Erro na linha {}: {}", result.getTotalRows(), e.getMessage());
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao processar arquivo CSV", e);
            throw new BusinessException("Erro ao processar arquivo CSV: " + e.getMessage());
        }

        result.setErrors(errors);
        auditService.log(userId, ip, "utilizadores", null, AcaoAudit.IMPORT, null, 
                result, "Importação CSV: " + result.getSuccessRows() + " sucesso, " + result.getFailedRows() + " falha");
        
        return result;
    }
}