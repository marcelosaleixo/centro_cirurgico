package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.SalaCirurgica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalaCirurgicaRepository extends JpaRepository<SalaCirurgica, Long> {
    List<SalaCirurgica> findByAtivaTrueOrderByNomeAsc();
}
