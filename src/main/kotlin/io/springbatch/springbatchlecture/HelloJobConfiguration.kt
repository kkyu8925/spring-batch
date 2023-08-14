package io.springbatch.springbatchlecture

import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet1
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet2
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet3
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet4
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher
import org.springframework.batch.core.repository.ExecutionContextSerializer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class ExecutionContextSerializerConfig : DefaultBatchConfiguration() {

    @Bean
    override fun getExecutionContextSerializer(): ExecutionContextSerializer {
        return Jackson2ExecutionContextStringSerializer()
    }

    @Bean
    override fun jobLauncher(): JobLauncher {
        val jobLauncher = TaskExecutorJobLauncher()
        jobLauncher.setJobRepository(jobRepository())
        jobLauncher.setTaskExecutor(SimpleAsyncTaskExecutor())
        jobLauncher.afterPropertiesSet()
        return jobLauncher
    }
}

//@Configuration
class HelloJobConfiguration(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,

    private val executionContextTasklet1: ExecutionContextTasklet1,
    private val executionContextTasklet2: ExecutionContextTasklet2,
    private val executionContextTasklet3: ExecutionContextTasklet3,
    private val executionContextTasklet4: ExecutionContextTasklet4,
) {

    @Bean
    fun job(): Job {
        return JobBuilder("batchJob1", jobRepository)
            .start(chunkStep())
//            .next(step2())
//            .next(step3())
//            .next(step4())
//            .validator(CustomJobParametersValidator())
//            .listener(JobRepositoryListener(jobRepository))
            .build()
    }

    @Bean
    fun step1(): Step {
        return StepBuilder("step1", jobRepository)
            .tasklet(executionContextTasklet1, transactionManager)
            .build()
    }

    @Bean
    fun step2(): Step {
        return StepBuilder("step2", jobRepository)
            .tasklet(executionContextTasklet2, transactionManager)
            .build()
    }

    @Bean
    fun step3(): Step {
        return StepBuilder("step3", jobRepository)
            .tasklet(executionContextTasklet3, transactionManager)
            .build()
    }

    @Bean
    fun step4(): Step {
        return StepBuilder("step4", jobRepository)
            .tasklet(executionContextTasklet4, transactionManager)
            .build()
    }

    @Bean
    fun chunkStep(): Step {
        return StepBuilder("chunkStep", jobRepository)
            .chunk<String, String>(10, transactionManager)
            .reader(ListItemReader(listOf("item1, item2, item3")))
            .processor { it.uppercase() }
            .writer { it.forEach { item -> println(item) } }
            .build()
    }
}
