package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.BancoHoras;
import com.marceloaleixo.centrocirurgico.enums.StatusBancoHoras;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BancoHorasRepository extends JpaRepository<BancoHoras, Long> {
    boolean existsByColaboradorIdAndDataBancoAndStatus(Long colaboradorId, LocalDate dataBanco, StatusBancoHoras status);
    boolean existsByColaboradorIdAndDataBancoAndStatusAndIdNot(Long colaboradorId, LocalDate dataBanco, StatusBancoHoras status, Long id);
    @EntityGraph(attributePaths = {"colaborador", "lancadoPor"})
    List<BancoHoras> findAllByOrderByDataBancoDesc();
    @EntityGraph(attributePaths = {"colaborador", "lancadoPor"})
    List<BancoHoras> findByColaboradorIdOrderByDataBancoDesc(Long colaboradorId);
    Optional<BancoHoras> findByColaboradorIdAndDataBancoAndStatus(Long colaboradorId, LocalDate dataBanco, StatusBancoHoras status);
}
