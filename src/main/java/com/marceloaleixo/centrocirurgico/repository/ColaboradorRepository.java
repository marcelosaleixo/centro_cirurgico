package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    long countByAtivoTrue();
    long countByAtivoFalse();
    @EntityGraph(attributePaths = {"funcao", "equipe"})
    List<Colaborador> findByAtivoTrueOrderByNomeAsc();
    List<Colaborador> findByEquipeIdAndAtivoTrueOrderByNomeAsc(Long equipeId);
    List<Colaborador> findByFuncaoIdAndAtivoTrueOrderByNomeAsc(Long funcaoId);
    Optional<Colaborador> findByCpf(String cpf);
    Optional<Colaborador> findByEmailIgnoreCase(String email);
    boolean existsByEquipeIdAndAtivoTrue(Long equipeId);
    long countByEquipeId(Long equipeId);

    @EntityGraph(attributePaths = {"funcao", "equipe"})
    @Query("select c from Colaborador c")
    List<Colaborador> findAllComRelacionamentos();
}
