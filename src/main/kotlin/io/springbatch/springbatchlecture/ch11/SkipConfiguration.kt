package io.springbatch.springbatchlecture.ch11

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class SkipConfiguration(
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
                    if (i == 3) {
                        throw SkippableException("skip")
                    }
                    println("ItemReader : $i")
                    return if (i > 20) null else i.toString()
                }
            })
            .processor(processor())
            .writer(writer())
            .faultTolerant()
//            .noSkip(SkippableException::class.java)
//            .skipPolicy(limitCheckingItemSkipPolicy())
//            .retry(SkippableException.class)
//            .retryLimit(2)
//            .skip(SkippableException::class.java)
//            .skipLimit(2)
            .build()
    }

    @Bean
    fun limitCheckingItemSkipPolicy(): LimitCheckingItemSkipPolicy {
        val skippableExceptionClasses: MutableMap<Class<out Throwable>, Boolean> = HashMap()
        skippableExceptionClasses[SkippableException::class.java] = true
        return LimitCheckingItemSkipPolicy(3, skippableExceptionClasses)
    }

    @Bean
    fun processor(): ItemProcessor<String, String> {
        var cnt = 0

        return ItemProcessor<String, String> {
            if (it == "6" || it == "7") {
                println("ItemProcessor : $it")
                cnt++
                throw SkippableException("Process failed. cnt:$cnt")
            } else {
                println("ItemProcessor : $it")
                (Integer.valueOf(it) * -1).toString()
            }
        }
    }

    @Bean
    fun writer(): ItemWriter<String> {
        var cnt = 0

        return ItemWriter<String> {
            for (item in it.items) {
                if (item == "-12") {
                    println("ItemWriter : $item")
                    cnt++
                    throw SkippableException("Write failed. cnt:$cnt")
                } else {
                    println("ItemWriter : $item")
                }
            }
        }
    }
}


class NoSkipException : java.lang.Exception {
    constructor() : super()
    constructor(msg: String?) : super(msg)
}


class SkippableException : java.lang.Exception {
    constructor(msg: String?) : super(msg)
}
