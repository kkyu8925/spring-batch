package io.springbatch.springbatchlecture.ch11

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.classify.BinaryExceptionClassifier
import org.springframework.classify.Classifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.RetryCallback
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.DefaultRetryState
import org.springframework.retry.support.RetryTemplate
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class RetryTemplateConfiguration(
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
            .processor(processor(retryTemplate()))
            .writer(writer())
            .faultTolerant()
            .skip(TemplateRetryableException::class.java)
            .skipLimit(2)
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
    fun processor(retryTemplate: RetryTemplate): ItemProcessor<String, Customer> {
        var count = 0

        val rollbackClassifier: Classifier<Throwable, Boolean> = BinaryExceptionClassifier(true)

        return ItemProcessor { item ->
            retryTemplate.execute(RetryCallback<Customer, RuntimeException> {
                if (item == "1" || item == "2") {
                    count++
                    throw TemplateRetryableException("failed")
                }
                Customer(item)
            }, RecoveryCallback {
                Customer(item)
            }, DefaultRetryState(item, rollbackClassifier))
        }
    }

    @Bean
    fun writer(): ItemWriter<Customer> {
        return ItemWriter<Customer> {
            println(it.items)
        }
    }

    @Bean
    fun retryTemplate(): RetryTemplate {
        val exceptionClass: MutableMap<Class<out Throwable>, Boolean> = HashMap()
        exceptionClass[TemplateRetryableException::class.java] = true
        val retryPolicy = SimpleRetryPolicy(2, exceptionClass)

        val backOffPolicy = FixedBackOffPolicy()
        backOffPolicy.backOffPeriod = 2000

        val retryTemplate = RetryTemplate()
        retryTemplate.setRetryPolicy(retryPolicy)
        retryTemplate.setBackOffPolicy(backOffPolicy)
        return retryTemplate
    }
}

data class Customer(
    val item: String
)

class TemplateRetryableException(msg: String) : java.lang.RuntimeException(msg)
