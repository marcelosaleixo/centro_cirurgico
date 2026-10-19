package com.marceloaleixo.centrocirurgico.repository;
import com.marceloaleixo.centrocirurgico.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PacienteMutiraoLookupRepository extends JpaRepository<Paciente,Long> { List<Paciente> findTop300ByOrderByNomeAsc(); }
