package com.playdata.concurrencyissues.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLockRepository {

    // 기존에는 우리가 RedisConfig 에서 직접 ConnectionFactory 와 Template 객체를 세팅.
    // 지금은 테스트 환경이니까 가장 기본 형태의 redisTemplate 을 사용하려 함.
    private final RedisTemplate<String, String> redisTemplate;

    // Redis Lock 로직 구현
    public boolean lock(Long key) {
        return redisTemplate.opsForValue()
                .setIfAbsent(
                        key.toString(), // key
                        "lock",  // value
                        Duration.ofMillis(3_000)  // Lock에 대한 Timeout 을 지정
                );
    }

    // Redis Lock 해제 메서드
    public boolean unlock(Long key) {
        return redisTemplate.delete(key.toString());
    }
}
