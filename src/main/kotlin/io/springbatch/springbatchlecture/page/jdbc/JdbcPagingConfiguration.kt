package io.springbatch.springbatchlecture.page.jdbc

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class JdbcPagingConfiguration(
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
    fun customItemReader(): JdbcPagingItemReader<Customer> {
        val parameters = HashMap<String, Any>()
        parameters["firstname"] = "A%"

        return JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbcPagingItemReader")
            .pageSize(10)
            .fetchSize(10)
            .dataSource(dataSource)
            .rowMapper { rs, _ ->
                Customer(
                    id = rs.getLong("id"),
                    firstName = rs.getString("firstName"),
                    lastName = rs.getString("lastName"),
                    birthdate = rs.getString("birthdate")
                )
            }
            .queryProvider(createQueryProvider())
            .parameterValues(parameters)
            .build()
    }

    @Bean
    fun createQueryProvider(): PagingQueryProvider {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("id,firstName,lastName,birthdate")
        queryProvider.setFromClause("from customer")
        queryProvider.setWhereClause("where firstName like :firstname")

        val sortKeys: MutableMap<String, Order> = HashMap(1)
        sortKeys["id"] = Order.ASCENDING
        queryProvider.setSortKeys(sortKeys)

        return queryProvider.getObject()
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
