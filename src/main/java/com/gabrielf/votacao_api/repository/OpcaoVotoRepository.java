package com.gabrielf.votacao_api.repository;

import com.gabrielf.votacao_api.domain.entity.OpcaoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OpcaoVotoRepository extends JpaRepository<OpcaoVoto, UUID> {

    List<OpcaoVoto> findByEnqueteIdOrderByQuantidadeVotosDesc(UUID enqueteId);

    @Modifying
    @Query("UPDATE OpcaoVoto o SET o.quantidadeVotos = o.quantidadeVotos + 1 WHERE o.id = :opcaoId")
    void incrementarQuantidadeVotos(@Param("opcaoId") UUID opcaoId);

    /* Esse @Query manda o próprio banco fazer a soma, atomicamente, numa única instrução SQL. Mesmo com 100 votos
    simultâneos na mesma opção, cada UPDATE incrementa em cima do valor mais recente. Protegendo o metodo de race
    condition.
     */
}
