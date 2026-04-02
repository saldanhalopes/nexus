package com.farma.treinamentos.controller;

import com.farma.treinamentos.dto.request.UtilizadorRequest;
import com.farma.treinamentos.dto.response.UtilizadorResponse;
import com.farma.treinamentos.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilizadores")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UtilizadorResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilizadorResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<UtilizadorResponse>> findByDepartment(@PathVariable String departamentoId) {
        return ResponseEntity.ok(userService.findByDepartment(departamentoId));
    }

    @GetMapping("/cargo/{cargoId}")
    public ResponseEntity<List<UtilizadorResponse>> findByCargo(@PathVariable String cargoId) {
        return ResponseEntity.ok(userService.findByCargo(cargoId));
    }

    @PostMapping
    public ResponseEntity<UtilizadorResponse> create(
            @Valid @RequestBody UtilizadorRequest request, 
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(userService.create(request, getClientIp(httpRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilizadorResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UtilizadorRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(userService.update(id, request, getClientIp(httpRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, HttpServletRequest httpRequest) {
        userService.delete(id, getClientIp(httpRequest));
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}