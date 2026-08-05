package com.gabrielf.votacao_api.dto.response;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.entity.OpcaoVoto;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record ResultadoEnqueteResponse(
        UUID enqueteId,
        String titulo,
        long totalVotos,
        List<OpcaoResultado> ranking,
        String opcaoVencedora
) {

    public record OpcaoResultado (
            UUID opcaoId,
            String texto,
            long quantidadeVotos,
            double percentual

    ) {
        public static OpcaoResultado fromEntity (OpcaoVoto opcao, long totalVotos) {
            double percentual = totalVotos == 0
                    ? 0.0
                    : (opcao.getQuantidadeVotos() * 100) / totalVotos;

            return new OpcaoResultado(
                    opcao.getId(),
                    opcao.getTexto(),
                    opcao.getQuantidadeVotos(),
                    percentual
            );
        }
    }

    public static ResultadoEnqueteResponse fromEntity(Enquete enquete) {
        long totalVotos = enquete.getOpcoes().stream()
                .mapToLong(OpcaoVoto::getQuantidadeVotos)
                .sum();

        List<OpcaoResultado> ranking = enquete.getOpcoes().stream()
                .sorted(Comparator.comparingLong(OpcaoVoto::getQuantidadeVotos).reversed())
                .map(o -> OpcaoResultado.fromEntity(o, totalVotos))
                .toList();

        String vencedora = ranking.isEmpty() ? null : ranking.get(0).texto();

        return new ResultadoEnqueteResponse(
                enquete.getId(),
                enquete.getTitulo(),
                totalVotos,
                ranking,
                vencedora
        );

    }

}
