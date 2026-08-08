package com.gabrielf.votacao_api.dto.response;

public record LoginResponse(
        String token,
        String tipo

) {

    public LoginResponse(String token) {
        this(token, "Bearer");
    }

}
