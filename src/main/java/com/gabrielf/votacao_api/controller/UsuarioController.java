package com.gabrielf.votacao_api.controller;

import com.gabrielf.votacao_api.dto.request.UsuarioRequest;
import com.gabrielf.votacao_api.dto.response.UsuarioResponse;
import com.gabrielf.votacao_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Cadastro e consulta de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(
            summary = "Cadastrar um novo usuário",
            description = "Cria um usuário com nome, email e senha. A senha é armazenada com hash (BCrypt). Rota pública."
    )
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "409", description = "Já existe um usuário cadastrado com este email")
    public ResponseEntity<UsuarioResponse> criar (@Valid @RequestBody UsuarioRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(request));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por id")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    public ResponseEntity<UsuarioResponse> buscarPorId (@PathVariable  UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));

    }

}
