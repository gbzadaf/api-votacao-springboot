package com.gabrielf.votacao_api.service;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.entity.OpcaoVoto;
import com.gabrielf.votacao_api.domain.entity.Usuario;
import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import com.gabrielf.votacao_api.dto.request.VotoRequest;
import com.gabrielf.votacao_api.exception.BusinessException;
import com.gabrielf.votacao_api.exception.ResourceNotFoundException;
import com.gabrielf.votacao_api.repository.OpcaoVotoRepository;
import com.gabrielf.votacao_api.repository.UsuarioRepository;
import com.gabrielf.votacao_api.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;
    @Mock
    private OpcaoVotoRepository opcaoVotoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EnqueteService enqueteService;

    @InjectMocks
    private VotoService votoService;

    private UUID enqueteId;
    private UUID usuarioId;
    private UUID opcaoId;
    private Enquete enquete;
    private Usuario usuario;
    private OpcaoVoto opcao;

    @BeforeEach
    void setUp() {
        enqueteId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        opcaoId = UUID.randomUUID();

        usuario = Usuario.builder().id(usuarioId).build();

        enquete = Enquete.builder()
                .id(enqueteId)
                .status(StatusEnquete.ABERTA)
                .build();

        opcao = OpcaoVoto.builder()
                .id(opcaoId)
                .enquete(enquete)
                .quantidadeVotos(0L)
                .build();
    }

    @Test
    void deveRegistrarVotoComSucessoQuandoTudoValido() {
        VotoRequest request = new VotoRequest(opcaoId);

        when(enqueteService.buscarEntidadePorId(enqueteId)).thenReturn(enquete);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(opcaoVotoRepository.findById(opcaoId)).thenReturn(Optional.of(opcao));
        when(votoRepository.existsByUsuarioIdAndEnqueteId(usuarioId, enqueteId)).thenReturn(false);

        votoService.registrarVoto(enqueteId, usuarioId, request);

        verify(votoRepository, times(1)).save(any());
        verify(opcaoVotoRepository, times(1)).incrementarQuantidadeVotos(opcaoId);
    }

    @Test
    void naoDeveRegistrarVotoQuandoEnqueteNaoEstaAberta() {
        enquete.setStatus(StatusEnquete.ENCERRADA);
        VotoRequest request = new VotoRequest(opcaoId);

        when(enqueteService.buscarEntidadePorId(enqueteId)).thenReturn(enquete);

        assertThatThrownBy(() -> votoService.registrarVoto(enqueteId, usuarioId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ENCERRADA");

        // garante que, se a enquete não está aberta, NADA foi salvo no banco
        verify(votoRepository, never()).save(any());
        verify(opcaoVotoRepository, never()).incrementarQuantidadeVotos(any());
    }

    @Test
    void naoDeveRegistrarVotoQuandoUsuarioJaVotouNestaEnquete() {
        VotoRequest request = new VotoRequest(opcaoId);

        when(enqueteService.buscarEntidadePorId(enqueteId)).thenReturn(enquete);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(opcaoVotoRepository.findById(opcaoId)).thenReturn(Optional.of(opcao));
        when(votoRepository.existsByUsuarioIdAndEnqueteId(usuarioId, enqueteId)).thenReturn(true);

        assertThatThrownBy(() -> votoService.registrarVoto(enqueteId, usuarioId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já votou");

        verify(votoRepository, never()).save(any());
    }

    @Test
    void naoDeveRegistrarVotoQuandoOpcaoNaoPertenceAEnquete() {
        Enquete outraEnquete = Enquete.builder().id(UUID.randomUUID()).status(StatusEnquete.ABERTA).build();
        OpcaoVoto opcaoDeOutraEnquete = OpcaoVoto.builder().id(opcaoId).enquete(outraEnquete).build();

        VotoRequest request = new VotoRequest(opcaoId);

        when(enqueteService.buscarEntidadePorId(enqueteId)).thenReturn(enquete);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(opcaoVotoRepository.findById(opcaoId)).thenReturn(Optional.of(opcaoDeOutraEnquete));

        assertThatThrownBy(() -> votoService.registrarVoto(enqueteId, usuarioId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não pertence a esta enquete");
    }

    @Test
    void deveLancarExcecaoQuandoOpcaoNaoExiste() {
        VotoRequest request = new VotoRequest(opcaoId);

        when(enqueteService.buscarEntidadePorId(enqueteId)).thenReturn(enquete);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(opcaoVotoRepository.findById(opcaoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> votoService.registrarVoto(enqueteId, usuarioId, request))
                .isInstanceOf(ResourceNotFoundException.class);

    }
}
