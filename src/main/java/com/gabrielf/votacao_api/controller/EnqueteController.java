package com.gabrielf.votacao_api.controller;

import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import com.gabrielf.votacao_api.dto.request.EnqueteRequest;
import com.gabrielf.votacao_api.dto.request.VotoRequest;
import com.gabrielf.votacao_api.dto.response.EnqueteResponse;
import com.gabrielf.votacao_api.dto.response.ResultadoEnqueteResponse;
import com.gabrielf.votacao_api.service.EnqueteService;
import com.gabrielf.votacao_api.service.VotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enquetes")
@RequiredArgsConstructor
public class EnqueteController {

    private final EnqueteService enqueteService;
    private final VotoService  votoService;

    @PostMapping
    public ResponseEntity<EnqueteResponse> criar (
            @RequestParam UUID usuarioId,
            @Valid @RequestBody EnqueteRequest enqueteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enqueteService.criar(usuarioId, enqueteRequest));

    }

    @GetMapping
    public ResponseEntity<Page<EnqueteResponse>> listar (
            @RequestParam (required = false) StatusEnquete status,
            @PageableDefault (size = 10, sort = "dataCriacao") Pageable  pageable) {
        return ResponseEntity.ok(enqueteService.listar(status, pageable));

    }

    @GetMapping("/{enqueteId}")
    public ResponseEntity<EnqueteResponse> buscarPorId (@PathVariable UUID enqueteId) {
        return ResponseEntity.ok(enqueteService.buscarPorId(enqueteId));

    }

    @PostMapping("/{enqueteId}/votos")
    public ResponseEntity<Void> votar (@PathVariable UUID enqueteId, @Valid @RequestBody VotoRequest request) {
        votoService.registrarVoto(enqueteId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @GetMapping("/{enqueteId}/resultado")
    public ResponseEntity<ResultadoEnqueteResponse> resultado (@PathVariable UUID enqueteId) {
        return ResponseEntity.ok(enqueteService.calcularResultado(enqueteId));

    }

    @PatchMapping("/{enqueteId}/encerrar")
    public ResponseEntity<EnqueteResponse> encerrar (@PathVariable UUID enqueteId, @RequestParam UUID usuarioId) {
        return ResponseEntity.ok(enqueteService.encerrar(usuarioId, enqueteId));

    }

}
