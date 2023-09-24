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
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
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
            .chunk<String, Customer>(5, transactionManager)
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
            .retry(RetryableException::class.java)
            .retryLimit(2)
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
    fun processor(): ItemProcessor<String, Customer> {
        var count = 0

        return ItemProcessor<String, Customer> {
            if (it == "2" || it == "3") {
                count++
                throw RetryableException("failed")
            }
            Customer(it)
        }
    }

//    @Bean
//    fun writer(): ItemWriter<*> {
//        return RetryItemWriter2()
//    }
//
//    @Bean
//    fun retryTemplate(): RetryTemplate {
//        val exceptionClass: MutableMap<Class<out Throwable?>, Boolean> = HashMap()
//        exceptionClass[RetryableException::class.java] = true
//
////        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
////        backOffPolicy.setBackOffPeriod(2000); //지정한 시간만큼 대기후 재시도 한다.
//        val retryPolicy = SimpleRetryPolicy(2, exceptionClass)
//        val retryTemplate = RetryTemplate()
//        //        retryTemplate.setBackOffPolicy(backOffPolicy);
//        retryTemplate.setRetryPolicy(retryPolicy)
//        return retryTemplate
//    }
}

data class Customer(
    val item: String
)


class RetryableException : java.lang.RuntimeException {
    constructor() : super()
    constructor(msg: String?) : super(msg)
}
