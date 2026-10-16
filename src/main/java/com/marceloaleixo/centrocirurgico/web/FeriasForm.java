package com.marceloaleixo.centrocirurgico.web;
import java.time.LocalDate;
public class FeriasForm {
    private Long colaboradorId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String observacao;
    public Long getColaboradorId(){return colaboradorId;} public void setColaboradorId(Long v){colaboradorId=v;}
    public LocalDate getDataInicio(){return dataInicio;} public void setDataInicio(LocalDate v){dataInicio=v;}
    public LocalDate getDataFim(){return dataFim;} public void setDataFim(LocalDate v){dataFim=v;}
    public String getObservacao(){return observacao;} public void setObservacao(String v){observacao=v;}
}
