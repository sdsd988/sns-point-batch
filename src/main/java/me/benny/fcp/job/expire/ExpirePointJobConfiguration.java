package me.benny.fcp.job.expire;


import me.benny.fcp.job.validator.TodayJobParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpirePointJobConfiguration {

    @Bean
    public Job expirePointJob(
            JobBuilderFactory jobBuilderFactory,
            TodayJobParameterValidator validator,
            Step expirePointStep
    ) {
        return jobBuilderFactory.get("expirePointJob")
                .start(expirePointStep)
                .validator(validator)
                .incrementer(new RunIdIncrementer()) //run.id가 계속해서 증가해서 Job parameter 중복되지 않게 해줌
                .build();
    }

}
