package com.gabrielf.votacao_api.dto.response;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.enums.StatusEnquete;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EnqueteResponse(
        UUID id,
        String titulo,
        String pergunta,
        StatusEnquete status,
        LocalDateTime dataCriacao,
        LocalDateTime dataEncerramento,
        UUID usuarioId,
        List<OpcaoVotoResponse> opcoes

) {
    public static EnqueteResponse fromEntity(Enquete enquete) {
        List<OpcaoVotoResponse> opcoesDTO = enquete.getOpcoes().stream()
                .map(OpcaoVotoResponse::fromEntity)
                .toList();

        return new EnqueteResponse(
                enquete.getId(),
                enquete.getTitulo(),
                enquete.getPergunta(),
                enquete.getStatus(),
                enquete.getDataCriacao(),
                enquete.getDataEncerramento(),
                enquete.getUsuario().getId(),
                opcoesDTO
        );

    }
}
