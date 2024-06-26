package me.benny.fcp.point.reservation;


import lombok.*;
import me.benny.fcp.point.IdEntity;
import me.benny.fcp.point.wallet.PointWallet;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PointReservation extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "point_wallet_id", nullable = false)
    private PointWallet pointWallet;

    @Column(name = "amount", nullable = false, columnDefinition = "BIGINT")
    BigInteger amount; //적립금액

    @Column(name = "earned_date", nullable = false)
    LocalDate earnedDate; //언제 적립할 건지..

    @Column(name = "available_days",nullable = false)
    int availableDays;

    @Setter
    @Column(name = "is_executed", columnDefinition = "TINYINT",length = 1)
    boolean executed;


    public PointReservation(PointWallet pointWallet, BigInteger amount, LocalDate earnedDate, int availableDays) {
        this.pointWallet = pointWallet;
        this.amount = amount;
        this.earnedDate = earnedDate;
        this.availableDays = availableDays;
        this.executed = false;
    }


    public LocalDate getExpireDate() {
        return this.earnedDate.plusDays(this.availableDays);
    }
}
