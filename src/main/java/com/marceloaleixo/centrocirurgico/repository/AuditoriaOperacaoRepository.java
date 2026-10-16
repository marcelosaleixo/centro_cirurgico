package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.AuditoriaOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;

public interface AuditoriaOperacaoRepository extends JpaRepository<AuditoriaOperacao, Long> {
    long countByOcorridoEmAfter(OffsetDateTime instante);
    long countByOcorridoEmBetween(OffsetDateTime inicio, OffsetDateTime fim);
}
