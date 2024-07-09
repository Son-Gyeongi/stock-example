package com.example.stock.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity // 데이터베이스 테이블
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 Id
    private Long productId;

    // 상품 수량
    private Long quantity;

    // 기본 생성자
    public Stock() {
    }

    // 상품 Id, 상품 수량을 받는 생성자
    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // 테스트 케이스에서 수량 확인을 위해서 getter 생성
    public Long getQuantity() {
        return quantity;
    }

    // 재고 감소 시키는 메서드
    public void decrease(Long quantity) {
        // 현재 가지고 있는 수량에서 감소시킬 수량을 뺐을 때 0 미만이면 안된다.
        if (this.quantity - quantity < 0) {
            // 0 미만일 경우 Exception 발생
            throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
        }

        // 0개 미만이 아니라면 현재 수량을 갱신 시켜준다.
        this.quantity -= quantity;
    }
}
