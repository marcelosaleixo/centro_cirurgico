package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.Ferias;
import com.marceloaleixo.centrocirurgico.enums.StatusFerias;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeriasRepository extends JpaRepository<Ferias, Long> {
    @EntityGraph(attributePaths = {"colaborador", "aprovadoPor"})
    List<Ferias> findAllByOrderByDataInicioDesc();

    @EntityGraph(attributePaths = {"colaborador", "aprovadoPor"})
    List<Ferias> findByColaboradorIdOrderByDataInicioDesc(Long colaboradorId);

    Optional<Ferias> findFirstByColaboradorIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndStatusInOrderByDataInicioAsc(
            Long colaboradorId, LocalDate data, LocalDate dataFim, List<StatusFerias> status);

    @Query("select (count(f) > 0) from Ferias f where f.colaborador.id = :colaboradorId " +
           "and f.status in :statusAtivos and f.dataInicio <= :dataFim and f.dataFim >= :dataInicio " +
           "and (:idAtual is null or f.id <> :idAtual)")
    boolean existeSobreposicao(@Param("colaboradorId") Long colaboradorId,
                               @Param("dataInicio") LocalDate dataInicio,
                               @Param("dataFim") LocalDate dataFim,
                               @Param("statusAtivos") List<StatusFerias> statusAtivos,
                               @Param("idAtual") Long idAtual);
}
