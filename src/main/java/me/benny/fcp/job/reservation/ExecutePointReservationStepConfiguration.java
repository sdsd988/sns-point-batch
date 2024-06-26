package me.benny.fcp.job.reservation;

import me.benny.fcp.point.Point;
import me.benny.fcp.point.PointRepository;
import me.benny.fcp.point.reservation.PointReservation;
import me.benny.fcp.point.reservation.PointReservationRepository;
import me.benny.fcp.point.wallet.PointWallet;
import me.benny.fcp.point.wallet.PointWalletRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class ExecutePointReservationStepConfiguration {

    @Bean
    @JobScope
    public Step executePointReservationStep(
            StepBuilderFactory stepBuilderFactory,
            PlatformTransactionManager platformTransactionManager,
            JpaPagingItemReader<PointReservation> executePointReservationItemReader,
            ItemProcessor<PointReservation, Pair<PointReservation, Point>> executePointReservationItemProcessor,
            ItemWriter<Pair<PointReservation, Point>> executePointReservationItemWriter
    ) {
        return stepBuilderFactory
                .get("executePointReservationStep")
                .allowStartIfComplete(true)
                .transactionManager(platformTransactionManager)
                .<PointReservation, Pair<PointReservation, Point>>chunk(1000)
                .reader(executePointReservationItemReader)
                .processor(executePointReservationItemProcessor)
                .writer(executePointReservationItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<PointReservation> executePointReservationItemReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}")
            LocalDate today
    ) {
        return new JpaPagingItemReaderBuilder<PointReservation>()
                .name("executePointReservationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select pr from PointReservation pr where pr.earnedDate <= :today and pr.executed = false")
                .parameterValues(Map.of("today", today))
                .pageSize(1000)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<PointReservation, Pair<PointReservation, Point>> executePointReservationItemProcessor() {
        return reservation -> {
            reservation.setExecuted(true);
            Point earnedPoint = new Point(
                    reservation.getPointWallet(),
                    reservation.getAmount(),
                    reservation.getEarnedDate(),
                    reservation.getExpireDate()
            );
            PointWallet wallet = earnedPoint.getPointWallet();
            wallet.setAmount(wallet.getAmount().add(earnedPoint.getAmount()));
            return Pair.of(reservation, earnedPoint);
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Pair<PointReservation, Point>> executePointReservationItemWriter(
            PointReservationRepository pointReservationRepository,
            PointRepository pointRepository,
            PointWalletRepository pointWalletRepository
    ) {
        return reservationAndPoints -> {
            for (Pair<PointReservation, Point> pair : reservationAndPoints) {
                PointReservation reservation = pair.getFirst();
                Point point = pair.getSecond();
                pointReservationRepository.save(reservation);
                pointRepository.save(point);
                pointWalletRepository.save(reservation.getPointWallet());
            }
        };
    }
}