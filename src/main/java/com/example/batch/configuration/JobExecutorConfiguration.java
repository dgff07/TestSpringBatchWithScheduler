package com.example.batch.configuration;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class JobExecutorConfiguration{

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(15);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(30);
        return taskExecutor;
    }

    @Bean
    public BatchConfigurer batchConfigurer(JobRepository jobRepository, ThreadPoolTaskExecutor taskExecutor) {
        return new DefaultBatchConfigurer() {
            @Override
            public JobLauncher getJobLauncher() {
                SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
                jobLauncher.setTaskExecutor(taskExecutor);
                jobLauncher.setJobRepository(jobRepository);
                return jobLauncher;
            }
        };
    }

}
