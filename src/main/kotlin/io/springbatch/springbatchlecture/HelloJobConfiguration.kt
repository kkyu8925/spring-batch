package io.springbatch.springbatchlecture

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class HelloJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun job(): Job {
        return JobBuilder("job", jobRepository)
            .start(helloStep1())
            .next(helloStep2())
            .build()
    }

    @Bean
    fun helloStep1(): Step {
        return StepBuilder("helloStep1", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("Step1 has executed")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun helloStep2(): Step {
        return StepBuilder("helloStep1", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("Step2 has executed")

                throw RuntimeException("Step2 has failed")

                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}
