package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StockServiceTest {
    // 우리가 만든 로직이 정상적으로 작동하는 지 확인을 위해서 테스트 코드 작성

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    // 테스트를 하기 위해서 데이터베이스에 재고가 있어야 함으로
    // 테스트가 실행되기 전에 데이터를 생성
    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    // 테스트 케이스가 종료되면 모든 상품(Item)을 삭제
    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    // 우리가 작성한 재고로직에 대한 테스트 케이스를 작성
    @Test
    public void 재고감소() {
        // stockId :1, 감소시킬 수량 1
        stockService.decrease(1L, 1L);

        // 수량을 100개 저장했고 1개를 감소시키면 stock의 수량은 99개가 남아있어야 한다.
        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 99개가 남아있는지 확인하기 위해
        assertEquals(99, stock.getQuantity());
    }



    /*
    재고감소 로직에서 발생할 수 있는 문제점에 대해 알아보자
    재고감소() 테스트는 요청이 1개씩 들어오는 테스트
    요청이 동시에 여러 개가 들어온다면
     */

    // 동시에 요청이 100개 오는 테스트 작성
    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        // 동시에 여러 개의 요청을 보내기 위해 멀티쓰레드 사용
        int threadCount = 100;

        // 멀티쓰레드를 이용해야 하기 때문에 ExecutorService 사용
        // ExecutorService 는 비동기로 실행하는 작업을 단순화하여 사용할 수 있게 도와주는 자바의 API 이다.
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 100개의 요청이 끝날 때까지 기다려야 함으로 CountDownLatch 활용
        // CountDownLatch 는 다른 쓰레드에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        // for문을 활용해서 100개의 요청을 보낸다.
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 모든 요청이 완료된다면 stockRepository를 활용해서 stock을 가지고 온 다음에 수량을 비교한다.
        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 예상 동작 - 처음에 100개 저장하고 1개씩 100번 감소해서 0개가 될 것이다.
        // 100 - (1 * 100) = 0
        assertEquals(0L, stock.getQuantity());

        // 예상과 다르게 88개 남아있음
        // 그 이유는 레이스 컨디션(Race Condition) 일어나서
        // 레이스 컨디션(Race Condition) 은 둘 이상의 스레드가 공유 데이터에 액세스할 수 있고 동시에 변경하려고 할 때 발생하는 문제
        // -> 해결하기 위해서 하나의 스레드가 작업이 완료된 이후에 다른 스레드가 데이터에 접근을 할 수 있도록 하자.
    }
}