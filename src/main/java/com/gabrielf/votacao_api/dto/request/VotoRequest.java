package com.gabrielf.votacao_api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VotoRequest(

        @NotNull(message = "opcaoId é obrigatório")
        UUID opcaoId
) {
}
