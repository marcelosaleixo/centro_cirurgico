package com.marceloaleixo.centrocirurgico.entity;

import com.marceloaleixo.centrocirurgico.enums.StatusBancoHoras;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "banco_horas", uniqueConstraints = @UniqueConstraint(
        name = "uq_banco_horas_colaborador_data", columnNames = {"colaborador_id", "data_banco"}))
public class BancoHoras {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    @Column(name = "data_banco", nullable = false)
    private LocalDate dataBanco;

    @Column(name = "quantidade_horas", nullable = false, precision = 5, scale = 2)
    private BigDecimal quantidadeHoras = new BigDecimal("12.00");

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusBancoHoras status = StatusBancoHoras.REGISTRADO;

    @Column(length = 500)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lancado_por_id")
    private Colaborador lancadoPor;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private OffsetDateTime criadoEm;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Colaborador getColaborador(){return colaborador;} public void setColaborador(Colaborador v){colaborador=v;}
    public LocalDate getDataBanco(){return dataBanco;} public void setDataBanco(LocalDate v){dataBanco=v;}
    public BigDecimal getQuantidadeHoras(){return quantidadeHoras;} public void setQuantidadeHoras(BigDecimal v){quantidadeHoras=v;}
    public StatusBancoHoras getStatus(){return status;} public void setStatus(StatusBancoHoras v){status=v;}
    public String getMotivo(){return motivo;} public void setMotivo(String v){motivo=v;}
    public Colaborador getLancadoPor(){return lancadoPor;} public void setLancadoPor(Colaborador v){lancadoPor=v;}
    public OffsetDateTime getCriadoEm(){return criadoEm;}
}
