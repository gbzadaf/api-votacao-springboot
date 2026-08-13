package com.gabrielf.votacao_api.service;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.entity.Usuario;
import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import com.gabrielf.votacao_api.exception.BusinessException;
import com.gabrielf.votacao_api.repository.EnqueteRepository;
import com.gabrielf.votacao_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnqueteServiceTest {

    @Mock
    private EnqueteRepository enqueteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EnqueteService enqueteService;

    private UUID enqueteId;
    private UUID criadorId;
    private Enquete enquete;

    @BeforeEach
    void setUp() {
        enqueteId = UUID.randomUUID();
        criadorId = UUID.randomUUID();

        Usuario criador = Usuario.builder().id(criadorId).build();

        enquete = Enquete.builder()
                .id(enqueteId)
                .status(StatusEnquete.ABERTA)
                .usuario(criador)
                .build();
    }

    @Test
    void deveEncerrarEnqueteQuandoUsuarioEhOCriadorEEnqueteEstaAberta() {
        when(enqueteRepository.findById(enqueteId)).thenReturn(Optional.of(enquete));

        var response = enqueteService.encerrar(enqueteId, criadorId);

        assertThat(response.status()).isEqualTo(StatusEnquete.ENCERRADA);
        assertThat(enquete.getDataEncerramento()).isNotNull();
    }

    @Test
    void naoDeveEncerrarQuandoUsuarioNaoEhOCriador() {
        UUID outroUsuarioId = UUID.randomUUID();
        when(enqueteRepository.findById(enqueteId)).thenReturn(Optional.of(enquete));

        assertThatThrownBy(() -> enqueteService.encerrar(enqueteId, outroUsuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Apenas o criador");
    }

    @Test
    void naoDeveEncerrarEnqueteQueJaEstaEncerrada() {
        enquete.setStatus(StatusEnquete.ENCERRADA);
        when(enqueteRepository.findById(enqueteId)).thenReturn(Optional.of(enquete));

        assertThatThrownBy(() -> enqueteService.encerrar(enqueteId, criadorId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ABERTA");

    }
}
