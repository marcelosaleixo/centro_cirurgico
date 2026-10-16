package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    Optional<Equipe> findByNomeIgnoreCase(String nome);
}
