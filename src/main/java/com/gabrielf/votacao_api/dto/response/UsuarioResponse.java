package com.gabrielf.votacao_api.dto.response;

import com.gabrielf.votacao_api.domain.entity.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        LocalDateTime dataCriacao

) {
    public static UsuarioResponse fromEntity (Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCriacao()
        );
    }
}
