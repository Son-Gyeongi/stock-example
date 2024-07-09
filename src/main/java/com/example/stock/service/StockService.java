package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // 재고 감소 로직 작성
    /*
    ⓐ. 자바에서 지원하는 방법으로 문제 해결하기
      - 자바에서는 Synchronized 를 활용하면 한 개의 스레드만 접근이 가능하도록 할 수 있다.
      - synchronized 를 메서드 선언부에 작성
      - 해당 메서드는 한 개의 스레드만 접근이 가능
      - 스프링의 @Transactional 동박 방식으로 인해 테스트 해도 실패함
        (수량 수정 후 트랜잭션 커밋 전에 다른 스레드에서 상품을 조회할 경우가 생김)
      -> 해결 @Transactional 주석 처리 후 실행

      문제점
      - 자바의 synchronized는 하나의 프로세스 안에서만 보장이 된다.
      - 서버가 한 대 일 때는 데이터의 접근이 서버가 1대여서 괜찮지만
        서버가 두 대 혹은 그 이상일 경우 데이터의 접근을 여러 대에서 할 수 있게 된다.
      - synchronized는 각 프로세스 안에서만 보장이 된다.
        결국 여러 스레드에서 동시에 데이터에 접근을 할 수 있게 되면서 레이스 컨디션이 발생
      - 실제 운영중인 서비스는 대부분 2대 이상의 서버를 사용하기 때문에 synchronized는 거의 사용하지 않는다.
      -> 이런 문제를 해결하기 위해 MySQL이 지원해주는 방법으로 레이스 컨디션 해결해보자
     */
    @Transactional
    public synchronized void decrease(Long id, Long quantity) {
        /*
        감소시킬 stockId와 감소시킬 상품 수량을 파라미터로 받음
        1. Stock 조회
        2. 재고를 감소시킨 뒤
        3. 갱신된 값을 저장하도록 함
         */

        // Stock 조회
        Stock stock = stockRepository.findById(id).orElseThrow();
        // 재고를 감소시킨 뒤
        stock.decrease(quantity);

        // 갱신된 값 저장
        stockRepository.saveAndFlush(stock);
    }
}
