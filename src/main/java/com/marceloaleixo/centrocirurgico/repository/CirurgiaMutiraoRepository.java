package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.CirurgiaMutirao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;
import java.util.List;
public interface CirurgiaMutiraoRepository extends JpaRepository<CirurgiaMutirao,Long> {
 @Query("select c from CirurgiaMutirao c join fetch c.paciente join fetch c.procedimento where c.dia.id=:diaId order by c.horaInicio")
 List<CirurgiaMutirao> listarPorDia(@Param("diaId") Long diaId);
 @Query("select count(c)>0 from CirurgiaMutirao c where c.dia.id=:diaId and c.id<>:id and c.horaInicio < :fim and c.horaFim > :inicio")
 boolean existeConflito(@Param("diaId") Long diaId,@Param("id") Long id,@Param("inicio") LocalTime inicio,@Param("fim") LocalTime fim);
}
