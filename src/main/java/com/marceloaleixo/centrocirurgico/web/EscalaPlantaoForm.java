package com.marceloaleixo.centrocirurgico.web;

import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import com.marceloaleixo.centrocirurgico.enums.TipoPlantao;
import java.time.LocalDate;
import java.time.LocalTime;

public class EscalaPlantaoForm {
    private Long id;
    private Long colaboradorId;
    private LocalDate dataPlantao;
    private LocalTime horaInicio = LocalTime.of(7, 0);
    private LocalTime horaFim = LocalTime.of(19, 0);
    private LocalTime horaInicioIntervalo;
    private LocalTime horaFimIntervalo;
    private TipoPlantao tipoPlantao;
    private StatusEscala status = StatusEscala.PROGRAMADO;
    private String observacao;

    public EscalaPlantaoForm() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getColaboradorId() { return colaboradorId; }
    public void setColaboradorId(Long colaboradorId) { this.colaboradorId = colaboradorId; }
    public LocalDate getDataPlantao() { return dataPlantao; }
    public void setDataPlantao(LocalDate dataPlantao) { this.dataPlantao = dataPlantao; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
    public LocalTime getHoraInicioIntervalo() { return horaInicioIntervalo; }
    public void setHoraInicioIntervalo(LocalTime v) { this.horaInicioIntervalo = v; }
    public LocalTime getHoraFimIntervalo() { return horaFimIntervalo; }
    public void setHoraFimIntervalo(LocalTime v) { this.horaFimIntervalo = v; }
    public TipoPlantao getTipoPlantao() { return tipoPlantao; }
    public void setTipoPlantao(TipoPlantao tipoPlantao) { this.tipoPlantao = tipoPlantao; }
    public StatusEscala getStatus() { return status; }
    public void setStatus(StatusEscala status) { this.status = status; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
