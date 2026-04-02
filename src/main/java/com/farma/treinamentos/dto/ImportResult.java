package com.farma.treinamentos.dto;

import java.util.List;

public class ImportResult {
    private int totalRows;
    private int successRows;
    private int failedRows;
    private List<String> errors;

    public ImportResult() {
        this.totalRows = 0;
        this.successRows = 0;
        this.failedRows = 0;
    }

    public void incrementTotalRows() {
        this.totalRows++;
    }

    public void incrementSuccessRows() {
        this.successRows++;
    }

    public void incrementFailedRows() {
        this.failedRows++;
    }

    public int getTotalRows() { return totalRows; }
    public int getSuccessRows() { return successRows; }
    public int getFailedRows() { return failedRows; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}