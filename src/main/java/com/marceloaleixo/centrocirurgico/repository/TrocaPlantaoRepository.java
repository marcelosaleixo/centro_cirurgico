package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.TrocaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusTrocaPlantao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrocaPlantaoRepository extends JpaRepository<TrocaPlantao, Long> {
    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcao", "aceitante", "aceitante.funcao", "aprovadoPor"})
    List<TrocaPlantao> findAllByOrderByDataSolicitacaoDesc();

    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcao", "aceitante", "aceitante.funcao", "aprovadoPor"})
    List<TrocaPlantao> findBySolicitanteIdOrAceitanteIdOrderByDataSolicitacaoDesc(Long solicitanteId, Long aceitanteId);

    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcao", "aceitante", "aceitante.funcao", "aprovadoPor"})
    List<TrocaPlantao> findByStatusAprovacaoOrderByDataSolicitacaoAsc(StatusTrocaPlantao status);

    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcao", "aceitante", "aceitante.funcao", "aprovadoPor"})
    @Query("select t from TrocaPlantao t where " +
           "(t.dataPlantaoOriginal between :inicio and :fim) or " +
           "(t.dataPlantaoDestino between :inicio and :fim) " +
           "order by t.dataPlantaoOriginal asc")
    List<TrocaPlantao> buscarTrocasDoPeriodo(@Param("inicio") LocalDate inicio,
                                              @Param("fim") LocalDate fim);

    @Override
    @EntityGraph(attributePaths = {"solicitante", "solicitante.funcao", "aceitante", "aceitante.funcao", "aprovadoPor"})
    Optional<TrocaPlantao> findById(Long id);
}
