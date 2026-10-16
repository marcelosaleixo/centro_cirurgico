package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.FuncaoProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FuncaoProfissionalRepository extends JpaRepository<FuncaoProfissional, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    Optional<FuncaoProfissional> findByNomeIgnoreCase(String nome);
}
