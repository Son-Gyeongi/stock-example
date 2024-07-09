package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /*
    ⓑ-2. 락을 걸고 재고 감소 로직 작성
      - 쿼리에서 for update 부분이 락을 걸고 데이터를 가져오는 부분
      - Pessimistic Lock 장점
        - 충돌이 빈번하게 일어난다면 Optimistic Lock 보다 성능이 좋을 수 있다.
        - Lock을 통해 업데이트를 제어하기 때문에 데이터 정합성이 보장된다.
      - Pessimistic Lock 단점
        - 별도의 락을 잡기 때문에 성능 감소가 있을 수 있다.
     */
    @Transactional
    public void decrease(Long id, Long quantity) {
        /*
        감소시킬 stockId와 감소시킬 상품 수량을 파라미터로 받음
        1. 락을 활용해서 데이터 조회
        2. 재고 감소
        3. 데이터 저장
         */
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);

        stock.decrease(quantity);

        stockRepository.save(stock);
    }
}
