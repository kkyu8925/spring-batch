package io.springbatch.springbatchlecture

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class ChunkOrientedTaskletConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("batchJob", jobRepository)
            .start(step1())
            .next(step2())
            .build()
    }

    @Bean
    @JobScope
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
            .chunk<String, String>(3, transactionManager)
            .reader(ListItemReader(mutableListOf("item1", "item2", "item3", "item4", "item5", "item6")))
            .processor { "my_$it" }
            .writer {
                it.items.forEach { item ->
                    println(item)
                }
            }
            .build()
    }

    @Bean
    fun step2(): Step {
        return StepBuilder("step2", jobRepository)
            .tasklet({ contribution, chunkContext ->
                println("step2 has executed")
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }
}
