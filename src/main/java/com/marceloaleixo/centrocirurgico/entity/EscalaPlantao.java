package com.marceloaleixo.centrocirurgico.entity;

import jakarta.persistence.*;
import java.time.*;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;

@Entity
@Table(name = "escala_plantao", uniqueConstraints = @UniqueConstraint(name = "uq_escala_colaborador_data", columnNames = {"colaborador_id", "data_plantao"}))
public class EscalaPlantao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;
    @Column(name = "data_plantao", nullable = false)
    private LocalDate dataPlantao;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio = LocalTime.of(7, 0);
    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim = LocalTime.of(19, 0);
    @Column(name = "hora_inicio_intervalo")
    private LocalTime horaInicioIntervalo;
    @Column(name = "hora_fim_intervalo")
    private LocalTime horaFimIntervalo;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_plantao", nullable = false, length = 10)
    private TipoPlantao tipoPlantao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusEscala status = StatusEscala.PROGRAMADO;
    @Column(length = 500)
    private String observacao;

    public EscalaPlantao() {
    }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public Colaborador getColaborador() { return this.colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }
    public LocalDate getDataPlantao() { return this.dataPlantao; }
    public void setDataPlantao(LocalDate dataPlantao) { this.dataPlantao = dataPlantao; }
    public LocalTime getHoraInicio() { return this.horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return this.horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public LocalTime getHoraInicioIntervalo() { return horaInicioIntervalo; }
    public void setHoraInicioIntervalo(LocalTime v) { this.horaInicioIntervalo = v; }
    public LocalTime getHoraFimIntervalo() { return horaFimIntervalo; }
    public void setHoraFimIntervalo(LocalTime v) { this.horaFimIntervalo = v; }
    public TipoPlantao getTipoPlantao() { return this.tipoPlantao; }
    public void setTipoPlantao(TipoPlantao tipoPlantao) { this.tipoPlantao = tipoPlantao; }
    public StatusEscala getStatus() { return this.status; }
    public void setStatus(StatusEscala status) { this.status = status; }
    public String getObservacao() { return this.observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
