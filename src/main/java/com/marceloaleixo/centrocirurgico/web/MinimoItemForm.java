package com.marceloaleixo.centrocirurgico.web;
import java.math.BigDecimal;
public class MinimoItemForm {
    private Long itemId;
    private BigDecimal quantidadeMinima = BigDecimal.ZERO;
    public Long getItemId(){return itemId;} public void setItemId(Long v){itemId=v;}
    public BigDecimal getQuantidadeMinima(){return quantidadeMinima;} public void setQuantidadeMinima(BigDecimal v){quantidadeMinima=v;}
}
