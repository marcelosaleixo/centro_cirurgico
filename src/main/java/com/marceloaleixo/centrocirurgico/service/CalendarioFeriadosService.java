package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.Feriado;
import com.marceloaleixo.centrocirurgico.repository.FeriadoRepository;
import com.marceloaleixo.centrocirurgico.enums.RegraFeriado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarioFeriadosService {
    private final FeriadoRepository repository;
    public CalendarioFeriadosService(FeriadoRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public Map<String, String> feriadosDoPeriodo(LocalDate inicio, LocalDate fim) {
        Map<String, String> map = new LinkedHashMap<>();
        if (inicio == null || fim == null || fim.isBefore(inicio)) return map;
        for (int ano = inicio.getYear(); ano <= fim.getYear(); ano++) {
            adicionar(map, LocalDate.of(ano, 1, 1), "Confraternização Universal", inicio, fim);
            adicionar(map, LocalDate.of(ano, 4, 21), "Tiradentes", inicio, fim);
            adicionar(map, LocalDate.of(ano, 5, 1), "Dia do Trabalho", inicio, fim);
            adicionar(map, LocalDate.of(ano, 9, 7), "Independência do Brasil", inicio, fim);
            adicionar(map, LocalDate.of(ano, 10, 12), "Nossa Senhora Aparecida", inicio, fim);
            adicionar(map, LocalDate.of(ano, 11, 2), "Finados", inicio, fim);
            adicionar(map, LocalDate.of(ano, 11, 15), "Proclamação da República", inicio, fim);
            adicionar(map, LocalDate.of(ano, 11, 20), "Dia Nacional de Zumbi e da Consciência Negra", inicio, fim);
            adicionar(map, LocalDate.of(ano, 12, 25), "Natal", inicio, fim);
            adicionar(map, pascoa(ano).minusDays(2), "Sexta-feira Santa", inicio, fim);
        }
        for (Feriado f : repository.findByAtivoTrueAndDataBetweenOrderByDataAscNomeAsc(inicio, fim)) {
            juntar(map, f.getData(), f.getNome());
        }
        for (Feriado f : repository.findByAtivoTrueOrderByDataAscNomeAsc()) {
            if (!f.isRecorrenteAnual()) continue;
            for (int ano = inicio.getYear(); ano <= fim.getYear(); ano++) {
                try {
                    LocalDate d = f.getData().withYear(ano);
                    if (!d.isBefore(inicio) && !d.isAfter(fim)) juntar(map, d, f.getNome());
                } catch (java.time.DateTimeException ignored) { }
            }
        }
        return map;
    }

    @Transactional(readOnly = true)
    public String nomeFeriado(LocalDate data) {
        return data == null ? null : feriadosDoPeriodo(data, data).get(data.toString());
    }

    /** Retorna a regra configurada para feriados cadastrados que incidam na data. */
    @Transactional(readOnly = true)
    public String descricaoRegraOperacional(LocalDate data) {
        if (data == null) return null;
        List<String> descricoes = new java.util.ArrayList<>();
        for (Feriado f : repository.findByAtivoTrueOrderByDataAscNomeAsc()) {
            LocalDate dataAplicavel = f.getData();
            boolean corresponde = data.equals(dataAplicavel);
            if (!corresponde && f.isRecorrenteAnual()
                    && data.getMonthValue() == dataAplicavel.getMonthValue()
                    && data.getDayOfMonth() == dataAplicavel.getDayOfMonth()) {
                corresponde = true;
            }
            if (!corresponde) continue;
            String descricao = f.getNome() + ": " + f.getRegraOperacional().name().replace('_', ' ');
            if (f.getRegraOperacional() == RegraFeriado.PAGAMENTO_DIFERENCIADO
                    && f.getPercentualAdicional() != null) {
                descricao += " (adicional de " + f.getPercentualAdicional().stripTrailingZeros().toPlainString() + "%)";
            }
            if (!descricoes.contains(descricao)) descricoes.add(descricao);
        }
        return descricoes.isEmpty() ? null : String.join("; ", descricoes);
    }

    public boolean isFimDeSemana(LocalDate data) {
        return data != null && (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY);
    }

    private void adicionar(Map<String, String> map, LocalDate data, String nome, LocalDate inicio, LocalDate fim) {
        if (!data.isBefore(inicio) && !data.isAfter(fim)) juntar(map, data, nome);
    }
    private void juntar(Map<String, String> map, LocalDate data, String nome) {
        map.merge(data.toString(), nome, (a, b) -> a.contains(b) ? a : a + " / " + b);
    }
    private LocalDate pascoa(int ano) {
        int a=ano%19,b=ano/100,c=ano%100,d=b/4,e=b%4,f=(b+8)/25,g=(b-f+1)/3;
        int h=(19*a+b-d-g+15)%30,i=c/4,k=c%4,l=(32+2*e+2*i-h-k)%7,m=(a+11*h+22*l)/451;
        int mes=(h+l-7*m+114)/31,dia=((h+l-7*m+114)%31)+1;
        return LocalDate.of(ano,mes,dia);
    }
}
