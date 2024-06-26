package me.benny.fcp.job.reservation;

import me.benny.fcp.BatchTestSupport;
import me.benny.fcp.point.Point;
import me.benny.fcp.point.PointRepository;
import me.benny.fcp.point.reservation.PointReservation;
import me.benny.fcp.point.reservation.PointReservationRepository;
import me.benny.fcp.point.wallet.PointWallet;
import me.benny.fcp.point.wallet.PointWalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class ExecutePointReservationJobConfigurationTest extends BatchTestSupport {

    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointReservationRepository pointReservationRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    Job executePointReservationJob;

    @Test
    void executePointReservationJob() throws Exception {

        //given
        //point reservation 있어야 함
        LocalDate earnDate = LocalDate.of(2021, 1, 5);
        PointWallet pointWallet = pointWalletRepository.save(new PointWallet("user1", BigInteger.valueOf(3000)));

        pointReservationRepository.save(
                new PointReservation(
                        pointWallet,
                        BigInteger.valueOf(1000),
                        earnDate, 10));
        //when
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2021-01-05")
                .toJobParameters();
        JobExecution jobExecution = launchJob(executePointReservationJob, jobParameters);
        //then
        then(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        List<PointReservation> reservations = pointReservationRepository.findAll();
        then(reservations).hasSize(1);
        then(reservations.get(0).isExecuted()).isTrue();

        List<Point> points = pointRepository.findAll();
        then(points).hasSize(1);
        then(points.get(0).getAmount()).isEqualByComparingTo(BigInteger.valueOf(1000));
        then(points.get(0).getEarnedDate()).isEqualTo(LocalDate.of(2021, 1, 5));
        then(points.get(0).getExpireDate()).isEqualTo(LocalDate.of(2021, 1, 15));

        List<PointWallet> wallets = pointWalletRepository.findAll();
        then(wallets).hasSize(1);
        then(wallets.get(0).getAmount()).isEqualByComparingTo(BigInteger.valueOf(4000));

    }
}