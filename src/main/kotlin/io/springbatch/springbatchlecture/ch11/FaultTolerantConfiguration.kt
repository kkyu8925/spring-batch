package io.springbatch.springbatchlecture.ch11

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class FaultTolerantConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("batchJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(step1())
            .build()
    }

    @Bean
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
            .chunk<String, String>(5, transactionManager)
            .reader(object : ItemReader<String?> {
                var i = 0
                override fun read(): String? {
                    i++
                    require(i != 1) { "skip" }
                    return if (i > 3) null else "item$i"
                }
            })
            .processor {
                throw IllegalStateException("retry")
            }
            .writer { println(it.items) }
            .faultTolerant()
            .skip(IllegalArgumentException::class.java)
            .skipLimit(1)
            .retry(IllegalStateException::class.java)
            .retryLimit(2)
            .build()
    }

    @Bean
    fun simpleLimitExceptionHandler(): SimpleLimitExceptionHandler? {
        return SimpleLimitExceptionHandler(3)
    }
}
