package com.gabrielf.votacao_api.service;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.entity.OpcaoVoto;
import com.gabrielf.votacao_api.domain.entity.Usuario;
import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import com.gabrielf.votacao_api.dto.request.EnqueteRequest;
import com.gabrielf.votacao_api.dto.response.EnqueteResponse;
import com.gabrielf.votacao_api.dto.response.ResultadoEnqueteResponse;
import com.gabrielf.votacao_api.exception.BusinessException;
import com.gabrielf.votacao_api.exception.ResourceNotFoundException;
import com.gabrielf.votacao_api.repository.EnqueteRepository;
import com.gabrielf.votacao_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnqueteService {

    private final EnqueteRepository enqueteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public EnqueteResponse criar (UUID usuarioId, EnqueteRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));

        Enquete enquete = Enquete.builder()
                .titulo(request.titulo())
                .pergunta(request.pergunta())
                .usuario(usuario)
                .build();

        List<OpcaoVoto> opcoes = request.opcoes().stream()
                .map(texto -> OpcaoVoto.builder().texto(texto).enquete(enquete).quantidadeVotos(0L).build())
                .toList();

        enquete.setOpcoes(opcoes);

        Enquete enqueteSalva = enqueteRepository.save(enquete);  // cascade salva as opções junto
        return EnqueteResponse.fromEntity(enquete);

    }

    @Transactional(readOnly = true)
    public Page<EnqueteResponse> listar (StatusEnquete status, Pageable pageable) {
        Page<Enquete> page = (status != null)
                ? enqueteRepository.findByStatus(status, pageable)
                : enqueteRepository.findAll(pageable);

        return page.map(EnqueteResponse::fromEntity);

    }

    @Transactional(readOnly = true)
    public EnqueteResponse buscarPorId (UUID id) {
        Enquete enquete = buscarEntidadePorId(id);
        return EnqueteResponse.fromEntity(enquete);

    }

    @Transactional
    public EnqueteResponse encerrar (UUID id, UUID usuarioId) {
        Enquete enquete = buscarEntidadePorId(id);

        if (!enquete.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Apenas o criador da enquete pode encerrá-la.");

        }

        if (enquete.getStatus() != StatusEnquete.ABERTA) {
            throw new BusinessException("Só é possível encerrar uma enquete que está ABERTA.");

        }

        enquete.setStatus(StatusEnquete.ENCERRADA);
        enquete.setDataEncerramento(LocalDateTime.now());

        return EnqueteResponse.fromEntity(enquete);

    }

    @Transactional(readOnly = true)
    public ResultadoEnqueteResponse calcularResultado (UUID enqueteId) {
        Enquete enquete = buscarEntidadePorId(enqueteId);
        return ResultadoEnqueteResponse.fromEntity(enquete);
    }



    //DRY
    protected Enquete buscarEntidadePorId (UUID id) {
        return enqueteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada: " + id));
    }
}
