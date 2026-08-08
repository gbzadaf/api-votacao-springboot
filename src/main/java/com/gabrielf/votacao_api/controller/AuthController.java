package com.gabrielf.votacao_api.controller;

import com.gabrielf.votacao_api.dto.request.LoginRequest;
import com.gabrielf.votacao_api.dto.response.LoginResponse;
import com.gabrielf.votacao_api.security.JwtService;
import com.gabrielf.votacao_api.security.UsuarioDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
        String token = jwtService.gerarToken(usuarioDetails);

        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));

    }

}
