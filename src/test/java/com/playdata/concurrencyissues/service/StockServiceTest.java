package com.playdata.concurrencyissues.service;

import com.playdata.concurrencyissues.entity.Stock;
import com.playdata.concurrencyissues.facade.LettuceLockFacade;
import com.playdata.concurrencyissues.facade.OptimisticLockFacade;
import com.playdata.concurrencyissues.facade.RedissonLockFacade;
import com.playdata.concurrencyissues.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private RedissonLockFacade redissonLockFacade;
//    private LettuceLockFacade lettuceLockFacade;
//    private OptimisticLockFacade optimisticLockFacade;
//    private OptimisticLockStockService stockService;
//    private StockService stockService;
//    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    // 각 테스트가 실행되기 전 이 메서드를 먼저 실행해라.
    @BeforeEach
    public void setUp() {
        stockRepository.save(new Stock(1L, 1L, 100L));
    }

    // 각 테스트가 실행된 후 이 메서드를 실행하라.
    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("완전 평범한 재고 감소 로직")
    void simpleDecreaseTest() {

//        stockService.decrease(1L, 1L);
//
//        Stock stock = stockRepository.findById(1L).orElseThrow();
//        assertEquals(99L, stock.getQuantity());

    }

    @Test
    void 동시에_100개의_주문_요청() throws InterruptedException {
        int threadCount = 100;

        // 비동기로 실행하는 작업을 간단하게 실핼할 수 있게 도와주는 ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 다른 스레드에서 특정 작업이 완료될 때 까지 대기하도록 돌와주는 동기화 도구
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 100 번의 작업 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 각 스레드마다 재고 감소 메서드 호출
                    redissonLockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 카운트다운 1개 감소
                    latch.countDown();
                }
            });
        }

        // 재고 확인을 위한 로직을 밑에다가 쓸건데, 카운트가 0이 될때가지 다음 코드를 실행하지 않게 해줌.
        latch.await();

        // 100번의 요청 이후 재고를 확인해 보자.
        // 100번의 재고 감소 요청을 넣었으니 당연히 재고는 0이 아닐까?
        Stock stock = stockRepository.findByProductId(1L).orElseThrow();
        assertEquals(0L, stock.getQuantity());
    }

}