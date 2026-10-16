package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.EscalaPlantao;
import com.marceloaleixo.centrocirurgico.enums.StatusEscala;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EscalaPlantaoRepository extends JpaRepository<EscalaPlantao, Long> {

    long countByDataPlantaoBetween(LocalDate inicio, LocalDate fim);

    long countByDataPlantaoBetweenAndStatus(LocalDate inicio, LocalDate fim, StatusEscala status);

    @EntityGraph(attributePaths = {"colaborador", "colaborador.funcao", "colaborador.equipe"})
    List<EscalaPlantao> findByDataPlantaoBetweenOrderByDataPlantaoAsc(LocalDate inicio, LocalDate fim);

    @EntityGraph(attributePaths = {"colaborador", "colaborador.funcao", "colaborador.equipe"})
    List<EscalaPlantao> findByColaboradorIdAndDataPlantaoBetweenOrderByDataPlantaoAsc(
            Long colaboradorId, LocalDate inicio, LocalDate fim);

    boolean existsByColaboradorIdAndDataPlantao(Long colaboradorId, LocalDate dataPlantao);

    @EntityGraph(attributePaths = {"colaborador", "colaborador.funcao", "colaborador.equipe"})
    Optional<EscalaPlantao> findByColaboradorIdAndDataPlantao(Long colaboradorId, LocalDate dataPlantao);

    boolean existsByColaboradorIdAndDataPlantaoAndStatusNot(
            Long colaboradorId, LocalDate dataPlantao, StatusEscala status);

    @Query("select e from EscalaPlantao e where e.id = :id")
    @EntityGraph(attributePaths = {"colaborador", "colaborador.funcao", "colaborador.equipe"})
    Optional<EscalaPlantao> findDetalhadaById(@Param("id") Long id);
}
