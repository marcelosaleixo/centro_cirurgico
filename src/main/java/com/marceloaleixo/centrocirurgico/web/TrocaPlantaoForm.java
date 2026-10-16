package com.marceloaleixo.centrocirurgico.web;
import java.time.LocalDate;
public class TrocaPlantaoForm {
    private Long solicitanteId, aceitanteId;
    private LocalDate dataPlantaoOriginal, dataPlantaoDestino;
    private String motivo;
    public Long getSolicitanteId(){return solicitanteId;} public void setSolicitanteId(Long v){solicitanteId=v;}
    public Long getAceitanteId(){return aceitanteId;} public void setAceitanteId(Long v){aceitanteId=v;}
    public LocalDate getDataPlantaoOriginal(){return dataPlantaoOriginal;} public void setDataPlantaoOriginal(LocalDate v){dataPlantaoOriginal=v;}
    public LocalDate getDataPlantaoDestino(){return dataPlantaoDestino;} public void setDataPlantaoDestino(LocalDate v){dataPlantaoDestino=v;}
    public String getMotivo(){return motivo;} public void setMotivo(String v){motivo=v;}
}
