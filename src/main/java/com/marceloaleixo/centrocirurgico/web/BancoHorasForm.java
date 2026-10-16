package com.marceloaleixo.centrocirurgico.web;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BancoHorasForm {
    private Long id;
    private Long colaboradorId;
    private LocalDate dataBanco;
    private BigDecimal quantidadeHoras = new BigDecimal("12.00");
    private String motivo;
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public Long getColaboradorId(){return colaboradorId;} public void setColaboradorId(Long v){colaboradorId=v;}
    public LocalDate getDataBanco(){return dataBanco;} public void setDataBanco(LocalDate v){dataBanco=v;}
    public BigDecimal getQuantidadeHoras(){return quantidadeHoras;} public void setQuantidadeHoras(BigDecimal v){quantidadeHoras=v;}
    public String getMotivo(){return motivo;} public void setMotivo(String v){motivo=v;}
}
