package com.gabrielf.votacao_api.repository;

import com.gabrielf.votacao_api.domain.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VotoRepository extends JpaRepository<Voto, UUID> {

    boolean existsByUsuarioIdAndEnqueteId(UUID usuarioId, UUID enqueteId);

    long countByEnqueteId(UUID enqueteId);

}
