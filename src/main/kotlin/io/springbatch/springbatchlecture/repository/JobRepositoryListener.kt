package io.springbatch.springbatchlecture.repository

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.stereotype.Component

@Component
class JobRepositoryListener(
    private val jobRepository: JobRepository
) : JobExecutionListener {

    override fun beforeJob(jobExecution: JobExecution) {
    }

    override fun afterJob(jobExecution: JobExecution) {
        val jobName = jobExecution.jobInstance.jobName
        val jobParameters = JobParametersBuilder()
            .addString("name", "user1")
            .toJobParameters()

        jobRepository.getLastJobExecution(jobName, jobParameters)?.let {
            for (execution in it.stepExecutions) {
                val status = execution.status
                println("BatchStatus = " + status.isRunning)
                println("BatchStatus = " + status.name)
            }
        }
    }
}
