
package br.edu.ifsudestemg.eventsAPI.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_movimentacao_ingresso_tipo",
                        columnNames = {"ingresso_id", "tipo"}
                )
        }
)
public class MovimentacaoCarteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull
    private LocalDateTime dataHora;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;

    @ManyToOne
    @JoinColumn(name = "ingresso_id")
    private Ingresso ingresso;

    @PrePersist
    public void registrarDataHora() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}
