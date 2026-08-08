package com.gabrielf.votacao_api.service;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.entity.OpcaoVoto;
import com.gabrielf.votacao_api.domain.entity.Usuario;
import com.gabrielf.votacao_api.domain.entity.Voto;
import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import com.gabrielf.votacao_api.dto.request.VotoRequest;
import com.gabrielf.votacao_api.exception.BusinessException;
import com.gabrielf.votacao_api.exception.ResourceNotFoundException;
import com.gabrielf.votacao_api.repository.OpcaoVotoRepository;
import com.gabrielf.votacao_api.repository.UsuarioRepository;
import com.gabrielf.votacao_api.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final OpcaoVotoRepository  opcaoVotoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnqueteService enqueteService;


    @Transactional
    public void registrarVoto (UUID enqueteId, UUID usuarioId, VotoRequest request) {
        Enquete enquete = enqueteService.buscarEntidadePorId(enqueteId);

        if (enquete.getStatus() != StatusEnquete.ABERTA) {
            throw new BusinessException("Não é possível votar. A enquete está " + enquete.getStatus() + ".");

        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + usuarioId));

        OpcaoVoto opcao = opcaoVotoRepository.findById(request.opcaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Opção de voto não encontrada: " + request.opcaoId()));

        if (!opcao.getEnquete().getId().equals(enqueteId)) {
            throw new BusinessException("A opção informada não pertence a esta enquete.");

        }

        // 1ª camada, validação de aplicação, "fail fast"
        if (votoRepository.existsByUsuarioIdAndEnqueteId(usuario.getId(), enqueteId)) {
            throw new BusinessException("Usuário já votou nesta enquete.");

        }

        Voto voto = Voto.builder()
                .enquete(enquete)
                .opcao(opcao)
                .usuario(usuario)
                .build();

        // 2ª camada, a UNIQUE constraint do banco garante isso sob concorrência;
        // se estourar aqui, o GlobalExceptionHandler converte em 409 automaticamente)
        votoRepository.save(voto);

        //atualiza o total de votos da opção de forma atômica (UPDATE ... SET x = x + 1)
        opcaoVotoRepository.incrementarQuantidadeVotos(opcao.getId());

    }

}
