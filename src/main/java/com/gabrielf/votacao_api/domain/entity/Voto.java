package com.gabrielf.votacao_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "votos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_voto_usuario_enquete",
                columnNames = {"usuario_id", "enquete_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private Enquete enquete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opcao_id", nullable = false)
    private OpcaoVoto opcao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_voto", nullable = false, updatable = false)
    private LocalDateTime dataVoto;


    @PrePersist
    protected void onCreate() {
        this.dataVoto = LocalDateTime.now();
    }
}
