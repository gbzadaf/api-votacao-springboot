package com.gabrielf.votacao_api.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "opcoes_voto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpcaoVoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    private Enquete enquete;

    @Column(nullable = false, length = 200)
    private String texto;

    @Column(name = "quantidade_votos", nullable = false)
    @Builder.Default
    private Long quantidadeVotos = 0L;

    @OneToMany(mappedBy = "opcao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Voto> votos = new ArrayList<>();


}
