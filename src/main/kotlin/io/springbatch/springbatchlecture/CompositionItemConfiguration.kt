package io.springbatch.springbatchlecture

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class CompositionItemConfiguration(
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
            .reader(object : ItemReader<String?> {
                var i = 0

                override fun read(): String? {
                    i++
                    return if (i > 10) null else "item"
                }
            })
            .processor(customItemProcessor())
            .writer { println(it) }
            .build()
    }

    @Bean
    fun customItemProcessor(): CompositeItemProcessor<String, String> {
        return CompositeItemProcessorBuilder<String, String>()
            .delegates(CustomItemProcessor1(), CustomItemProcessor2())
            .build()
    }
}

class CustomItemProcessor1 : ItemProcessor<String, String> {
    private var cnt = 0

    override fun process(item: String): String {
        cnt++
        return item + cnt
    }
}

class CustomItemProcessor2 : ItemProcessor<String, String> {
    private var cnt = 0

    override fun process(item: String): String {
        cnt++
        return item + cnt
    }
}
