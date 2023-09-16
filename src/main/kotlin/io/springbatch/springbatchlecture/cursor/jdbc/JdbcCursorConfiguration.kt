package io.springbatch.springbatchlecture.cursor.jdbc

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class JdbcCursorConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val dataSource: DataSource
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
    fun customItemReader(): JdbcCursorItemReader<Customer> {
        return JdbcCursorItemReaderBuilder<Customer>()
            .dataSource(dataSource)
            .name("jdbcCursorItemReader")
            .fetchSize(10)
            .sql("select id, firstName, lastName, birthdate from customer where firstName like ? order by lastName, firstName")
            .rowMapper { rs, _ ->
                Customer(
                    id = rs.getLong("id"),
                    firstName = rs.getString("firstName"),
                    lastName = rs.getString("lastName"),
                    birthdate = rs.getString("birthdate")
                )
            }
            .queryArguments("A%")
            .build()
    }

    @Bean
    fun customItemWriter(): ItemWriter<Customer> {
        return ItemWriter {
            for (item in it) {
                println(item)
            }
        }
    }
}
