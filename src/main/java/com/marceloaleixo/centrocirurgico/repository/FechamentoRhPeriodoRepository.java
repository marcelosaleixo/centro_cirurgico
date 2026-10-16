package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.FechamentoRhPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface FechamentoRhPeriodoRepository extends JpaRepository<FechamentoRhPeriodo, Long> {
    Optional<FechamentoRhPeriodo> findByPeriodoInicioAndPeriodoFim(LocalDate inicio, LocalDate fim);
}
