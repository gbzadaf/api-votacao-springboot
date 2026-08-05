package com.gabrielf.votacao_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EnqueteRequest(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 150)
        String titulo,

        @NotBlank(message = "Pergunta é obrigatória")
        @Size(max = 500)
        String pergunta,

        @NotEmpty(message = "A enquete precisa ter pelo menos 2 opções")
        @Size(min = 2, message = "A enquete precisa ter pelo menos 2 opções")
        @Valid
        List<@NotBlank(message = "Texto da opção não pode ser vazio") String> opcoes

) {
}
