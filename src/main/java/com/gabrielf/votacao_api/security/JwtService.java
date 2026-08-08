package com.gabrielf.votacao_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String gerarToken (UsuarioDetails usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getUsername()) // email do usuário
                .claim("usuarioId", usuario.getId().toString())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChaveAssinatura())
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();

    }

    public boolean tokenValido(String token, UsuarioDetails usuarioDetails) {
        String email = extrairEmail(token);
        return email.equals(usuarioDetails.getUsername()) && !tokenExpirado(token);

    }

    private boolean tokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());

    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getChaveAssinatura() {
        return Keys.hmacShaKeyFor(secret.getBytes());

    }
}
