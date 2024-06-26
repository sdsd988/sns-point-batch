package me.benny.fcp.job.expire;

import me.benny.fcp.point.Point;
import me.benny.fcp.point.PointRepository;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class ExpirePointStepConfiguration {

    @Bean
    @JobScope
    public Step expirePointStep(
            StepBuilderFactory stepBuilderFactory,
            PlatformTransactionManager platformTransactionManager,
            JpaPagingItemReader<Point> expirePointItemReader,
            ItemProcessor<Point, Point> expirePointItemProcessor,
            ItemWriter<Point> expirePointItemWriter
    ) {
        return stepBuilderFactory
                .get("expirePointStep")
                .transactionManager(platformTransactionManager)
                .<Point, Point>chunk(1000)
                .reader(expirePointItemReader)
                .processor(expirePointItemProcessor)
                .writer(expirePointItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Point> expirePointItemReader(
            EntityManagerFactory entityManagerFactory,
            @Value("#{T(java.time.LocalDate).parse(jobParameters[today])}")
            LocalDate today
    ) {
        return new JpaPagingItemReaderBuilder<Point>()
                .name("expirePointItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Point p where p.expireDate < :today and used = false and expired = false")
                .parameterValues(Map.of("today", today))
                .pageSize(1000)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Point, Point> expirePointItemProcessor() {
        return point -> {
            point.setExpired(true);
            PointWallet wallet = point.getPointWallet();
            wallet.setAmount(wallet.getAmount().subtract(point.getAmount()));
            return point;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Point> expirePointItemWriter(
            PointRepository pointRepository,
            PointWalletRepository pointWalletRepository
    ) {
        return points -> {
            for (Point point : points) {
                if (point.isExpired()) {
                    pointRepository.save(point);
                    pointWalletRepository.save(point.getPointWallet());
                }
            }
        };
    }
}
