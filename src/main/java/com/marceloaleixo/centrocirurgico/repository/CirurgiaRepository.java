package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.Cirurgia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface CirurgiaRepository extends JpaRepository<Cirurgia, Long> {
    @EntityGraph(attributePaths = {"paciente", "procedimento", "salaCirurgica"})
    List<Cirurgia> findTop200ByOrderByDataCirurgiaDescHoraInicioDesc();
}
