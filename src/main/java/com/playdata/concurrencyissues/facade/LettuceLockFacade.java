package com.playdata.concurrencyissues.facade;

import com.playdata.concurrencyissues.repository.RedisLockRepository;
import com.playdata.concurrencyissues.service.OptimisticLockStockService;
import com.playdata.concurrencyissues.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

// 파사드 패턴: 여러가지 하위 클래스들의 공통적인 기능을 정의하는 상위 수준의 린터페이스를 제공하는 패턴.
// Optimistic: Lock을 사용할 때 업데이트 실패 시 버전 정보를 다시 조회 후 재시도를 위한 객체
@Component
@Slf4j
@RequiredArgsConstructor
public class LettuceLockFacade {
    private final RedisLockRepository repository;
    private final StockService stockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {

        while (!repository.lock(id)) {

            log.info("lock 획득 실패");
            Thread.sleep(500);
        }

        // lock을 획득한 후에 실행되는 부분
        log.info("lock 획득!");
        stockService.decrease(id, quantity);
        repository.unlock(id);
        log.info("lock 해제!");

    }
}
