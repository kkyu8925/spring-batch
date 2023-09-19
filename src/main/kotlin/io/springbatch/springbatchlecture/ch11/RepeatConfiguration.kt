package io.springbatch.springbatchlecture.ch11

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler
import org.springframework.batch.repeat.support.RepeatTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class RepeatConfiguration(
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
                    return if (i > 3) null else "item$i"
                }
            })
            .processor(object : ItemProcessor<String, String> {
                val template = RepeatTemplate()

                override fun process(item: String): String {

                    // 반복할 때마다 count 변수의 값을 1씩 증가
                    // count 값이 chunkSize 값보다 크거나 같을 때 반복문 종료
//                    template.setCompletionPolicy(SimpleCompletionPolicy(3));
                    // 소요된 시간이 설정된 시간보다 클 경우 반복문 종료
                    // setCompletionPolicy 하나의 정책만 실행
//                    template.setCompletionPolicy(TimeoutTerminationPolicy(3000));

                    // 여러 유형의 CompletionPolicy 를 복합적으로 처리함
                    // 여러 개 중에 먼저 조건이 부합하는 CompletionPolicy 에 따라 반복문이 종료됨
//                    val completionPolicies = arrayOf<CompletionPolicy>(
//                        TimeoutTerminationPolicy(3000), SimpleCompletionPolicy(2)
//                    )
//                    val completionPolicy = CompositeCompletionPolicy()
//                    completionPolicy.setPolicies(completionPolicies);
//                    template.setCompletionPolicy(completionPolicy);

                    // 예외 제한 횟수만큼 반복문 실행
                    template.setExceptionHandler(simpleLimitExceptionHandler())

                    template.iterate {
                        println("repeatTest")
//                        throw RuntimeException("Exception is occurred")
                        RepeatStatus.CONTINUABLE;
                    }

                    return item
                }
            })
            .writer { println(it.items) }
            .build()
    }

    @Bean
    fun simpleLimitExceptionHandler(): SimpleLimitExceptionHandler {
        return SimpleLimitExceptionHandler(2)
    }
}
