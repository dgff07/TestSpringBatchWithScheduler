package com.example.batch.tasks;

import com.example.batch.executors.JobExecutorManager;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.concurrent.TimeUnit;


public class PrintSomethingTask implements Tasklet {

	private final String clusterName;
	private final JobExecutorManager jobExecutorManager;


	public PrintSomethingTask(String clusterName, JobExecutorManager jobExecutorManager) {
		this.clusterName = clusterName;
		this.jobExecutorManager = jobExecutorManager;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		System.out.println("Executing task in cluster: "+ clusterName);
		TimeUnit.SECONDS.sleep(20);
		jobExecutorManager.unlockCluster(clusterName);
		return RepeatStatus.FINISHED;
	}

}
