package io.springbatch.springbatchlecture.adapter

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.adapter.ItemWriterAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class ItemWriterAdapterConfiguration(
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
            .chunk<String, String>(10, transactionManager)
            .reader(object : ItemReader<String> {
                var i = 0

                override fun read(): String {
                    i++
                    return if (i > 10) "" else "item$i"
                }
            })
            .writer(customItemWriter())
            .build()
    }


    @Bean
    fun customItemWriter(): ItemWriterAdapter<String> {
        val writer = ItemWriterAdapter<String>()
        writer.setTargetObject(customService())
        writer.setTargetMethod("joinCustomer")
        return writer
    }

    @Bean
    fun customService(): CustomService2<String> {
        return CustomService2()
    }
}
