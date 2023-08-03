package io.springbatch.springbatchlecture

import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet1
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet2
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet3
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet4
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class HelloJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,

    private val executionContextTasklet1: ExecutionContextTasklet1,
    private val executionContextTasklet2: ExecutionContextTasklet2,
    private val executionContextTasklet3: ExecutionContextTasklet3,
    private val executionContextTasklet4: ExecutionContextTasklet4,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("job", jobRepository)
            .start(step1())
            .next(step2())
            .next(step3())
            .next(step4())
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

    @Bean
    fun step4(): Step {
        return StepBuilder("step4", jobRepository)
            .tasklet(executionContextTasklet4, transactionManager)
            .build()
    }
}
