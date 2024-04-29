package me.benny.fcp.point;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ExpiredPointSummary {

    String userId;
    BigInteger amount; //만료 금액

    @QueryProjection //생성자를 Q클래스로 변환시켜주는 어노테이션
    public ExpiredPointSummary(
            String userId,
            BigInteger amount
    ){
        this.userId = userId;
        this.amount = amount;
    }
}
