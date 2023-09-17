package io.springbatch.springbatchlecture.page.jpa

import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class JpaPagingConfiguration(
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
            .chunk<Customer, Customer2>(10, transactionManager)
            .reader(customItemReader())
            .processor(customItemProcess())
            .writer(customItemWriter())
            .build()
    }

    @Bean
    fun customItemReader(): JpaPagingItemReader<Customer> {
        return JpaPagingItemReaderBuilder<Customer>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(10)
            .queryString("select c from Customer c")
            .build()
    }

//    @Bean
//    fun customItemWriter(): ItemWriter<Customer> {
//        return ItemWriter {
//            for (item in it) {
//                println(item.address.location)
//            }
//        }
//    }

    @Bean
    fun customItemWriter(): JpaItemWriter<Customer2> {
        return JpaItemWriterBuilder<Customer2>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }

    @Bean
    fun customItemProcess(): ItemProcessor<Customer, Customer2> {
        return ItemProcessor<Customer, Customer2> {
            Customer2(
                id = it.id,
                username = it.username,
                age = it.age,
            )
        }
    }
}
