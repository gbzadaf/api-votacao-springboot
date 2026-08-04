package com.gabrielf.votacao_api.repository;

import com.gabrielf.votacao_api.domain.entity.Enquete;
import com.gabrielf.votacao_api.domain.enums.StatusEnquete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnqueteRepository extends JpaRepository<Enquete, UUID> {

    Page<Enquete> findByStatus(StatusEnquete status, Pageable pageable);

    Page<Enquete> findByUsuarioId(UUID usuarioId, Pageable pageable);
}
