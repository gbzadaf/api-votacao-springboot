package com.gabrielf.votacao_api.dto.response;

import com.gabrielf.votacao_api.domain.entity.OpcaoVoto;

import java.util.UUID;

public record OpcaoVotoResponse(
        UUID id,
        String texto,
        Long quantidadeVotos

) {
    public static OpcaoVotoResponse fromEntity (OpcaoVoto opcao) {
        return new OpcaoVotoResponse(
                opcao.getId(),
                opcao.getTexto(),
                opcao.getQuantidadeVotos()
        );
    }
}
