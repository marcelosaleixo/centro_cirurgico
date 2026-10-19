package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.EventoMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
public interface EventoMutiraoRepository extends JpaRepository<EventoMutirao,Long> {
 @EntityGraph(attributePaths={"dias"}) List<EventoMutirao> findAllByOrderByDataInicioDesc();
}
