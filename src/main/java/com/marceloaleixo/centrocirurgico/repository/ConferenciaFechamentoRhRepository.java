package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.ConferenciaFechamentoRh;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConferenciaFechamentoRhRepository extends JpaRepository<ConferenciaFechamentoRh, Long> {
    List<ConferenciaFechamentoRh> findByPeriodoInicioAndPeriodoFim(LocalDate inicio, LocalDate fim);
    Optional<ConferenciaFechamentoRh> findByPeriodoInicioAndPeriodoFimAndColaboradorId(LocalDate inicio, LocalDate fim, Long colaboradorId);
}
