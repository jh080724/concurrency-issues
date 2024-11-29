package com.playdata.concurrencyissues.service;

import com.playdata.concurrencyissues.entity.Stock;
import com.playdata.concurrencyissues.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class OptimisticLockStockService {
    private final StockRepository stockRepository;

    // 주문이 들어오면 재고를 감소시키는 메소드
    public void decrease(Long id, Long quantity){
        // Stock 조회
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);

        // 재고 감소 진행
        stock.decrease(quantity);

        // 재고 수정된 값을 저장
        stockRepository.save(stock);

    }
}
