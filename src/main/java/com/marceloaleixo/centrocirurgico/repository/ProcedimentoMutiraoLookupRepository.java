package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProcedimentoMutiraoLookupRepository extends JpaRepository<Procedimento,Long> { List<Procedimento> findByAtivoTrueOrderByNomeAsc(); }
