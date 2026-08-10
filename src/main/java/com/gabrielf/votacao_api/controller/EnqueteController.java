package com.gabrielf.votacao_api.controller;

import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import com.gabrielf.votacao_api.dto.request.EnqueteRequest;
import com.gabrielf.votacao_api.dto.request.VotoRequest;
import com.gabrielf.votacao_api.dto.response.EnqueteResponse;
import com.gabrielf.votacao_api.dto.response.ResultadoEnqueteResponse;
import com.gabrielf.votacao_api.security.UsuarioDetails;
import com.gabrielf.votacao_api.service.EnqueteService;
import com.gabrielf.votacao_api.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enquetes")
@RequiredArgsConstructor
@Tag(name = "Enquetes", description = "Criação, listagem, votação e resultado de enquetes")
public class EnqueteController {

    private final EnqueteService enqueteService;
    private final VotoService  votoService;

    @PostMapping
    @Operation(summary = "Criar uma nova enquete", description = "Cria uma enquete com título, " +
            "pergunta e 2 ou mais opções. O criador é extraído do token JWT.")
    @ApiResponse(responseCode = "201", description = "Enquete criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: menos de 2 opções)")
    public ResponseEntity<EnqueteResponse> criar (
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @Valid @RequestBody EnqueteRequest enqueteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enqueteService.criar(usuarioDetails.getId(),
                enqueteRequest));

    }

    @GetMapping
    @Operation(summary = "Listar enquetes", description = "Lista enquetes de forma paginada, " +
            "com filtro opcional por status.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<EnqueteResponse>> listar (
            @RequestParam (required = false) StatusEnquete status,
            @ParameterObject @PageableDefault (size = 10, sort = "dataCriacao") Pageable  pageable) {
        return ResponseEntity.ok(enqueteService.listar(status, pageable));

    }

    @GetMapping("/{enqueteId}")
    @Operation(summary = "Buscar enquete por id")
    @ApiResponse(responseCode = "200", description = "Enquete encontrada")
    @ApiResponse(responseCode = "404", description = "Enquete não encontrada")
    public ResponseEntity<EnqueteResponse> buscarPorId (@PathVariable UUID enqueteId) {
        return ResponseEntity.ok(enqueteService.buscarPorId(enqueteId));

    }

    @PostMapping("/{enqueteId}/votos")
    @Operation(summary = "Registrar um voto", description = "Registra o voto do usuário autenticado em uma opção da " +
            "enquete. Só é possível votar uma vez por enquete, e apenas em enquetes ABERTA.")
    @ApiResponse(responseCode = "204", description = "Voto registrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Enquete ou opção não encontrada")
    @ApiResponse(responseCode = "409", description = "Usuário já votou nesta enquete, ou a enquete não está ABERTA")
    public ResponseEntity<Void> votar (@PathVariable UUID enqueteId,
                                       @AuthenticationPrincipal UsuarioDetails usuarioDetails,
                                       @Valid @RequestBody VotoRequest request) {
        votoService.registrarVoto(enqueteId, usuarioDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @GetMapping("/{enqueteId}/resultado")
    @Operation(summary = "Ver resultado da enquete", description = "Retorna o total de votos, ranking das opções por " +
            "quantidade de votos, percentuais e a opção vencedora.")
    @ApiResponse(responseCode = "200", description = "Resultado calculado com sucesso")
    @ApiResponse(responseCode = "404", description = "Enquete não encontrada")
    public ResponseEntity<ResultadoEnqueteResponse> resultado (@PathVariable UUID enqueteId) {
        return ResponseEntity.ok(enqueteService.calcularResultado(enqueteId));

    }

    @PatchMapping("/{enqueteId}/encerrar")
    @Operation(summary = "Encerrar enquete", description = "Encerra uma enquete ABERTA, impedindo novos votos. " +
            "Apenas o criador da enquete pode encerrá-la.")
    @ApiResponse(responseCode = "200", description = "Enquete encerrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Enquete não encontrada")
    @ApiResponse(responseCode = "409", description = "Enquete já não está ABERTA, ou usuário não é o criador")
    public ResponseEntity<EnqueteResponse> encerrar (@PathVariable UUID enqueteId,
                                                     @AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        return ResponseEntity.ok(enqueteService.encerrar(enqueteId, usuarioDetails.getId()));

    }

}
