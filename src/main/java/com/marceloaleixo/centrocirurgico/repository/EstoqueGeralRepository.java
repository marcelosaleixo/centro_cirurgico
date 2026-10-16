package com.marceloaleixo.centrocirurgico.repository;

import com.marceloaleixo.centrocirurgico.entity.EstoqueGeral;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface EstoqueGeralRepository extends JpaRepository<EstoqueGeral, Long> {
    Optional<EstoqueGeral> findByItemId(Long itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EstoqueGeral e where e.item.id = :itemId")
    Optional<EstoqueGeral> bloquearPorItemId(@Param("itemId") Long itemId);
}
