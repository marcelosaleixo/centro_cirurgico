package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.FechamentoFinanceiroCirurgia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface FechamentoFinanceiroCirurgiaRepository extends JpaRepository<FechamentoFinanceiroCirurgia, Long> { Optional<FechamentoFinanceiroCirurgia> findByCirurgiaId(Long cirurgiaId); }
