package io.springbatch.springbatchlecture.ch11

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.retry.RetryPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class RetryConfiguration(
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
            .reader(reader())
            .processor(processor())
            .writer {
                it.items.forEach { item ->
                    println(item)
                }
            }
            .faultTolerant()
            .skip(RetryableException::class.java)
            .skipLimit(2)
//            .retry(RetryableException::class.java)
//            .retryLimit(2)
            .retryPolicy(retryPolicy())
            .build()
    }

    @Bean
    fun reader(): ListItemReader<String> {
        val items: MutableList<String> = ArrayList()
        for (i in 0..29) {
            items.add(i.toString())
        }
        return ListItemReader(items)
    }

    @Bean
    fun processor(): ItemProcessor<String, String> {
        var count = 0

        return ItemProcessor<String, String> {
            if (it == "2" || it == "3") {
                count++
                throw RetryableException("failed")
            }
            it
        }
    }

    @Bean
    fun retryPolicy(): RetryPolicy {
        val exceptionClass: MutableMap<Class<out Throwable>, Boolean> = HashMap()
        exceptionClass[RetryableException::class.java] = true

        return SimpleRetryPolicy(2, exceptionClass)
    }
}

class RetryableException(msg: String) : java.lang.RuntimeException(msg)
