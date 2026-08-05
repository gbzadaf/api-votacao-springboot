package com.gabrielf.votacao_api.service;

import com.gabrielf.votacao_api.domain.entity.Usuario;
import com.gabrielf.votacao_api.dto.request.UsuarioRequest;
import com.gabrielf.votacao_api.dto.response.UsuarioResponse;
import com.gabrielf.votacao_api.exception.BusinessException;
import com.gabrielf.votacao_api.exception.ResourceNotFoundException;
import com.gabrielf.votacao_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criar (UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com este email.");

        }
        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .build();

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);

    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId (UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));

        return UsuarioResponse.fromEntity(usuario);
    }
}
