package me.benny.fcp.job.expire;

import me.benny.fcp.BatchTestSupport;
import me.benny.fcp.point.Point;
import me.benny.fcp.point.PointRepository;
import me.benny.fcp.point.wallet.PointWallet;
import me.benny.fcp.point.wallet.PointWalletRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class ExpirePointJobConfigurationTest extends BatchTestSupport {

    @Autowired
    Job expirePointJob;
    @Autowired
    PointWalletRepository pointWalletRepository;
    @Autowired
    PointRepository pointRepository;

    @Test
    void expirePointJob() throws Exception {
        // given
        LocalDate earnDate = LocalDate.of(2021, 1, 1);
        LocalDate expireDate = LocalDate.of(2021, 1, 3);
        PointWallet pointWallet = pointWalletRepository.save(
                new PointWallet("user123", BigInteger.valueOf(6000))
        );
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        pointRepository.save(new Point(pointWallet, BigInteger.valueOf(1000), earnDate, expireDate));
        // when
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "2021-01-04")
                .toJobParameters();
        JobExecution execution = launchJob(expirePointJob, jobParameters);
        // then
        List<Point> points = pointRepository.findAll();
        then(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        then(points.stream().filter(Point::isExpired)).hasSize(3);
        PointWallet changedPointWallet = pointWalletRepository.findById(pointWallet.getId()).orElseGet(null);
        then(changedPointWallet).isNotNull();
        then(changedPointWallet.getAmount()).isEqualByComparingTo(BigInteger.valueOf(3000));
    }
}