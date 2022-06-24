package com.example.batch.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Enumeration;

@Component
public class JobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private final Job printSomethingJob;
    private final JobLauncher jobLauncher;
    private final JobExecutorManager jobExecutorManager;

    public JobScheduler(Job printSomethingJob, JobLauncher jobLauncher, JobExecutorManager jobExecutorManager) {
        this.printSomethingJob = printSomethingJob;
        this.jobLauncher = jobLauncher;
        this.jobExecutorManager = jobExecutorManager;
    }

    @Scheduled(fixedDelayString = "${scheduler.delay}")
    public void perform() throws Exception {
        Enumeration<String> clusterNames = jobExecutorManager.getClustersNames();

        while (clusterNames.hasMoreElements()) {

            String clusterName = clusterNames.nextElement();
            if (!StringUtils.hasText(clusterName)) {
                LOGGER.warn("There is a cluster with an invalid name. Make sure the name is not null or empty");
                continue;
            }
            if (!jobExecutorManager.canRun(clusterName)) {
                LOGGER.info("The job will not be executed for the cluster '" + clusterName + "' since the previous one isn't finished yet");
                continue;
            }
            jobExecutorManager.lockCluster(clusterName);
            JobParameters param = defineJobParameters(clusterName);
            jobLauncher.run(printSomethingJob, param);

        }
    }

    protected final JobParameters defineJobParameters(String clusterName) {
        return new JobParametersBuilder()
                .addString("JobId", generateJobId(clusterName))
                .addString("cluster", clusterName)
                .toJobParameters();
    }

    protected final String generateJobId(String clusterName) {
        if (!StringUtils.hasText(clusterName)) {
            throw new IllegalArgumentException("The cluster name must be defined");
        }
        return clusterName + "_" + System.currentTimeMillis();
    }
}
