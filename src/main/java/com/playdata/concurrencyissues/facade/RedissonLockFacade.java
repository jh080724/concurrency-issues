package com.playdata.concurrencyissues.facade;

import com.playdata.concurrencyissues.repository.RedisLockRepository;
import com.playdata.concurrencyissues.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// 파사드 패턴: 여러가지 하위 클래스들의 공통적인 기능을 정의하는 상위 수준의 린터페이스를 제공하는 패턴.
// Optimistic: Lock을 사용할 때 업데이트 실패 시 버전 정보를 다시 조회 후 재시도를 위한 객체
@Component
@Slf4j
@RequiredArgsConstructor
public class RedissonLockFacade {
    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {

        // RedissonClient 를 활용한 Lock 객체
        RLock lock = redissonClient.getLock(id.toString());

        try {
            // 락획득 시도
            boolean flag = lock.tryLock(
                    10, // 락이 이미 점유되었다면 최대 10초 동안 락을 기다림.
                    5,  // 락을 점유 했다면 5초 동안 유지, 5초 후에는 자동 락 해제(데드락 방지)
                    TimeUnit.SECONDS    // 시간 단위 설정
            );

            // lock 획득에 실패했다면
            if (!flag) {
                log.info("lock 획득 실패!");
                return;
            }

            // lock 획득 성공 -> 비즈니스 로직 수행
            stockService.decrease(id, quantity);

        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            // 로직이 모두 정상적으로 진행된 후에는 락 해제
            lock.unlock();
        }

    }
}
