package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaFechamentoRh;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AuditoriaFechamentoRhRepository extends JpaRepository<AuditoriaFechamentoRh, Long> {
    List<AuditoriaFechamentoRh> findByPeriodoInicioAndPeriodoFimOrderByCriadoEmDesc(LocalDate inicio, LocalDate fim);
}
