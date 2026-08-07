package com.gabrielf.votacao_api.controller;

import com.gabrielf.votacao_api.dto.request.UsuarioRequest;
import com.gabrielf.votacao_api.dto.response.UsuarioResponse;
import com.gabrielf.votacao_api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar (@Valid @RequestBody UsuarioRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));

    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId (@PathVariable  UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));

    }

}
