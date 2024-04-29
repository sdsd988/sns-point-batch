package me.benny.fcp.job.message;

import me.benny.fcp.job.listener.InputExpiredSoonPointAlarmCriteriaDateStepListener;
import me.benny.fcp.message.Message;
import me.benny.fcp.point.ExpiredPointSummary;
import me.benny.fcp.point.PointRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.Map;

@Configuration
public class MessageExpireSoonPointStepConfiguration {

    @Bean
    @JobScope
    public Step messageExpireSoonPointStep(
            StepBuilderFactory stepBuilderFactory,
            PlatformTransactionManager platformTransactionManager,
            InputExpiredSoonPointAlarmCriteriaDateStepListener listener,
            RepositoryItemReader<ExpiredPointSummary> messageExpireSoonPointItemReader,
            ItemProcessor<ExpiredPointSummary, Message> messageExpireSoonPointItemProcessor,
            JpaItemWriter<Message> messageExpireSoonPointItemWriter
    ) {
        return stepBuilderFactory
                .get("messageExpireSoonPointStep")
                .allowStartIfComplete(true)
                .transactionManager(platformTransactionManager)
                .listener(listener)
                .<ExpiredPointSummary, Message>chunk(1000)
                .reader(messageExpireSoonPointItemReader)
                .processor(messageExpireSoonPointItemProcessor)
                .writer(messageExpireSoonPointItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<ExpiredPointSummary> messageExpireSoonPointItemReader(
            PointRepository pointRepository,
            @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
            LocalDate alarmCriteriaDate
    ) {
        return new RepositoryItemReaderBuilder<ExpiredPointSummary>()
                .name("messageExpireSoonPointItemReader")
                .repository(pointRepository)
                .methodName("sumBeforeExpireDate")
                .pageSize(1000)
                .arguments(alarmCriteriaDate)
                .sorts(Map.of("pointWallet", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<ExpiredPointSummary, Message> messageExpireSoonPointItemProcessor(
            @Value("#{T(java.time.LocalDate).parse(stepExecutionContext[alarmCriteriaDate])}")
            LocalDate alarmCriteriaDate
    ) {
        return summary -> Message.expiredSoonPointMessageInstance(
                summary.getUserId(),
                alarmCriteriaDate,
                summary.getAmount()
        );
    }

    @Bean
    @StepScope
    public JpaItemWriter<Message> messageExpireSoonPointItemWriter(
            EntityManagerFactory entityManagerFactory
    ) {
        JpaItemWriter<Message> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
