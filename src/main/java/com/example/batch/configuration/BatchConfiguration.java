package com.example.batch.configuration;


import com.example.batch.executors.JobExecutorManager;
import com.example.batch.tasks.PrintSomethingTask;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private ApplicationContext context;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ApplicationContext context) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.context = context;
    }

    @Bean
    public Job printSomethingJob(Step step1) {
        return jobBuilderFactory.get("printSomething").incrementer(new RunIdIncrementer()).flow(step1).end().build();
    }

    @Bean
    public Step step1(PrintSomethingTask printSomethingTask) {
        return stepBuilderFactory.get("step1").tasklet(printSomethingTask).build();
    }

    @Bean
    @StepScope
    public PrintSomethingTask printSomethingTask(@Value("#{jobParameters[cluster]}") String clusterName, JobExecutorManager jobExecutorManager) {
        PrintSomethingTask task = new PrintSomethingTask(clusterName, jobExecutorManager);
        return task;
    }

}