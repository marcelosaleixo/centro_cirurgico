package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.Feriado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    List<Feriado> findAllByOrderByDataAscNomeAsc();
    List<Feriado> findByAtivoTrueAndDataBetweenOrderByDataAscNomeAsc(LocalDate inicio, LocalDate fim);
    List<Feriado> findByAtivoTrueOrderByDataAscNomeAsc();
}
