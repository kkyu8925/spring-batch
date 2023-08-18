package io.springbatch.springbatchlecture

import io.springbatch.springbatchlecture.decider.CustomDecider
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class FlowJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("batchJob", jobRepository)
            .start(startStep())
            .next(decider())
            .from(decider()).on("ODD").to(oddStep())
            .from(decider()).on("EVEN").to(evenStep())
            .end()
            .build()
    }

    @Bean
    fun decider(): JobExecutionDecider {
        return CustomDecider()
    }

    @Bean
    fun startStep(): Step {
        return StepBuilder("startStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("startStep")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun evenStep(): Step {
        return StepBuilder("evenStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("evenStep")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    fun oddStep(): Step {
        return StepBuilder("oddStep", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("oddStep")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}
