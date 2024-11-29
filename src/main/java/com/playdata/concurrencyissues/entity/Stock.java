package com.playdata.concurrencyissues.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long quantity;

    @Version
    private Long version; // 낙관적 Lock을 위한 버정 정보

    public Stock(Long quantity, Long productId, Long id) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(Long quantity) {
        if(this.quantity - quantity < 0) {
            throw new RuntimeException("재고는 0 미만이 될 수 없다.");
        }
        this.quantity -= quantity;
    }

}
