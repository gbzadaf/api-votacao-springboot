package com.gabrielf.votacao_api.controller;

import com.gabrielf.votacao_api.dto.request.LoginRequest;
import com.gabrielf.votacao_api.dto.response.LoginResponse;
import com.gabrielf.votacao_api.security.JwtService;
import com.gabrielf.votacao_api.security.UsuarioDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e geração de token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Autentica o usuário com email e senha, retornando um token JWT válido por 1 hora. Rota pública."
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, token retornado")
    @ApiResponse(responseCode = "401", description = "Email ou senha inválidos")
    public ResponseEntity<LoginResponse> login (@Valid @RequestBody LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
        String token = jwtService.gerarToken(usuarioDetails);

        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));

    }

}
