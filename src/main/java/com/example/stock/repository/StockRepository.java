package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

    /*
    ⓑ-1. 데이터베이스를 이용하여 해결하기
      - MySQL을 활용한 방법 1 [Pessimistic Lock]
      - 실제로 데이터에 락을 걸어서 정합성을 맞추는 방법
      - Exclusive Lock을 걸게 되면 다른 트랜잭션에서는 락이 해제되기 전에 락을 걸고 데이터를 가져갈 수 없다.
     */
    // 락을 걸고 데이터를 가져오는 메서드 작성
    // Spring Data Jpa 에서는 @Lock 어노테이션을 통해서 손쉽게 PESSIMISTIC Lock을 구현할 수 있다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock as s where s.id = :id") // native 쿼리를 활용해서 쿼리 작성
    Stock findByIdWithPessimisticLock(Long id);
}
