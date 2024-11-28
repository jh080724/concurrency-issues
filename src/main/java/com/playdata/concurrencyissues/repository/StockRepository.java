package com.playdata.concurrencyissues.repository;

import com.playdata.concurrencyissues.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductId(Long productId);

    // Spring Data JPA에서 제공하는 어노테이션 Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.id = ?1")
    Stock findByIdWithPessimisticLock(Long id);
}
