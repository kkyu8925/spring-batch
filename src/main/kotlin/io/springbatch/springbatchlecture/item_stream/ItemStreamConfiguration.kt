package io.springbatch.springbatchlecture.item_stream

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class ItemStreamConfiguration(
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
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
            .chunk<String, String>(5, transactionManager)
            .reader(itemReader())
            .writer(itemWriter())
            .build()
    }


    @Bean
    fun itemReader(): CustomItemStreamReader {
        val items: MutableList<String> = ArrayList(10)
        for (i in 1..10) {
            items.add(i.toString())
        }
        return CustomItemStreamReader(items)
    }

    @Bean
    fun itemWriter(): ItemWriter<String> {
        return CustomItemWriter()
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
