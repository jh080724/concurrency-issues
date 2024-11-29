package com.playdata.concurrencyissues.facade;

import com.playdata.concurrencyissues.service.OptimisticLockStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OptimisticLock;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

// 파사드 패턴: 여러가지 하위 클래스들의 공통적인 기능을 정의하는 상위 수준의 린터페이스를 제공하는 패턴.
// Optimistic: Lock을 사용할 때 업데이트 실패 시 버전 정보를 다시 조회 후 재시도를 위한 객체
@Component
@Slf4j
@RequiredArgsConstructor
public class OptimisticLockFacade {
    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {

        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);

                // 재고감소 업데이트를 성공했다면 반복문 종료
                break;

            } catch (ObjectOptimisticLockingFailureException e) {
                // 버전 정보를 가지고 DB에 접근했는데, 버전이 일치하지 않아 업데이트에 실패했을때 발생하는 에러
                log.error("업데이트 실패! : {}", e.getMessage());
                Thread.sleep(100);
            }
        }
    }
}
