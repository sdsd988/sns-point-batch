package me.benny.fcp.job.message;

import me.benny.fcp.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageExpiredPointJobConfiguration {
    @Bean
    public Job messageExpiredPointJob(
            JobBuilderFactory jobBuilderFactory,
            TodayJobParameterValidator validator,
            Step messageExpiredPointStep
    ) {
        return jobBuilderFactory
                .get("messageExpiredPointJob")
                .validator(validator)
                .incrementer(new RunIdIncrementer())
                .start(messageExpiredPointStep)
                .build();
    }
}
