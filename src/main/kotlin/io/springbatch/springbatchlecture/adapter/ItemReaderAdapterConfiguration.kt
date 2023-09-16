package io.springbatch.springbatchlecture.adapter

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.adapter.ItemReaderAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class ItemReaderAdapterConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
) {
    @Bean
    fun job(): Job {
        return JobBuilder("batchJob", jobRepository)
            .start(step1())
            .build()
    }

    @Bean
    @JobScope
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
            .chunk<String, String>(10, transactionManager)
            .reader(customItemReader())
            .writer(customItemWriter())
            .build()
    }

    @Bean
    fun customItemReader(): ItemReaderAdapter<String> {
        val reader = ItemReaderAdapter<String>()
        reader.setTargetObject(customService())
        reader.setTargetMethod("joinCustomer")
        return reader
    }

    private fun customService(): CustomService {
        return CustomService()
    }

    @Bean
    fun customItemWriter(): ItemWriter<String> {
        return ItemWriter {
            it.items.forEach { item ->
                println(item)
            }
        }
    }
}
