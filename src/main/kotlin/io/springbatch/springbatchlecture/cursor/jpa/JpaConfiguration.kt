package io.springbatch.springbatchlecture.cursor.jpa

import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class JpaConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
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
            .chunk<Customer, Customer>(10, transactionManager)
            .reader(customItemReader())
            .writer(customItemWriter())
            .build()
    }

    @Bean
    fun customItemReader(): JpaCursorItemReader<Customer> {
        val parameters = HashMap<String, Any>()
        parameters["firstname"] = "A%"

        return JpaCursorItemReaderBuilder<Customer>()
            .name("jpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select c from Customer c where firstname like :firstname")
            .parameterValues(parameters)
            .build()

    }

    @Bean
    fun customItemWriter(): ItemWriter<Customer> {
        return ItemWriter {
            for (item in it) {
                println(item.firstname)
            }
        }
    }
}
