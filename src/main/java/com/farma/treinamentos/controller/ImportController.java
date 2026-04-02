package com.farma.treinamentos.controller;

import com.farma.treinamentos.dto.ImportResult;
import com.farma.treinamentos.service.CsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final CsvImportService csvImportService;

    public ImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/csv/utilizadores")
    public ResponseEntity<ImportResult> importUsers(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        String userId = "system";
        String ip = "0.0.0.0";
        return ResponseEntity.ok(csvImportService.importUsers(file, userId, ip));
    }
}