package io.springbatch.springbatchlecture

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class ScopeConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("batchJob", jobRepository)
            .start(step1(""))
            .next(step2())
            .build()
    }

    @Bean
    @JobScope
    fun step1(@Value("#{jobParameters['message']}") message: String): Step {
        println("jobParameters['message'] : $message")
        return StepBuilder("step1", jobRepository)
            .tasklet(tasklet1(""), transactionManager)
            .build()
    }

    @Bean
    fun step2(): Step {
        return StepBuilder("step2", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("step2")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    @Bean
    @StepScope
    fun tasklet1(@Value("#{jobParameters['message2']}") name: String): Tasklet {
        return Tasklet { stepContribution: StepContribution?, chunkContext: ChunkContext? ->
            println("jobParameters['message2'] : $name")
            RepeatStatus.FINISHED
        }
    }
}
