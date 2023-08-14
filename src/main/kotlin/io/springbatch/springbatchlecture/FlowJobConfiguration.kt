package io.springbatch.springbatchlecture

import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet1
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet2
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet3
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class FlowJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,

    private val executionContextTasklet1: ExecutionContextTasklet1,
    private val executionContextTasklet2: ExecutionContextTasklet2,
    private val executionContextTasklet3: ExecutionContextTasklet3,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("batchJob", jobRepository)
            .start(step1())
            .on(FlowExecutionStatus.COMPLETED.name)
            .to(step3())
            .from(step1())
            .on(FlowExecutionStatus.FAILED.name)
            .to(step2())
            .end()
            .build()
    }


    @Bean
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
            .tasklet(executionContextTasklet1, transactionManager)
            .build()
    }

    @Bean
    fun step2(): Step {
        return StepBuilder("step2", jobRepository)
            .tasklet(executionContextTasklet2, transactionManager)
            .build()
    }

    @Bean
    fun step3(): Step {
        return StepBuilder("step3", jobRepository)
            .tasklet(executionContextTasklet3, transactionManager)
            .build()
    }
}
