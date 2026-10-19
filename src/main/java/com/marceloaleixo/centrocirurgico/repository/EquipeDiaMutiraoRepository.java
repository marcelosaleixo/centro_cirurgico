package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.EquipeDiaMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EquipeDiaMutiraoRepository extends JpaRepository<EquipeDiaMutirao,Long> {
 List<EquipeDiaMutirao> findByDiaIdOrderByColaboradorNomeAsc(Long diaId);
 boolean existsByDiaIdAndColaboradorIdAndFuncaoNoMutiraoIgnoreCase(Long diaId,Long colaboradorId,String funcao);
}
