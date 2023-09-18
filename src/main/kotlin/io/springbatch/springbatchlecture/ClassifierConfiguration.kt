package io.springbatch.springbatchlecture

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor
import org.springframework.classify.Classifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

//@Configuration
class ClassifierConfiguration(
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
            .chunk<ProcessorInfo, ProcessorInfo>(10, transactionManager)
            .reader(object : ItemReader<ProcessorInfo?> {
                var i = 0

                override fun read(): ProcessorInfo? {
                    i++
                    return if (i > 3) null else ProcessorInfo(id = i)
                }
            })
            .processor(customItemProcessor())
            .writer { println(it.items) }
            .build()
    }

    @Bean
    fun customItemProcessor(): ItemProcessor<ProcessorInfo, ProcessorInfo> {
        val processorMap: MutableMap<Int, ItemProcessor<ProcessorInfo, ProcessorInfo>> = HashMap()
        processorMap[1] = ClassifierCustomItemProcessor1()
        processorMap[2] = ClassifierCustomItemProcessor2()
        processorMap[3] = ClassifierCustomItemProcessor3()

        val processor = ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo>()
        val classifier: ProcessorClassifier<ProcessorInfo, ItemProcessor<*, out ProcessorInfo>> = ProcessorClassifier()
        classifier.setProcessorMap(processorMap)
        processor.setClassifier(classifier)

        return processor
    }
}


data class ProcessorInfo(
    val id: Int
)


class ProcessorClassifier<C, T> : Classifier<C, T> {
    private var processorMap: Map<Int, ItemProcessor<ProcessorInfo, ProcessorInfo>> = HashMap()

    override fun classify(classifiable: C): T {
        return processorMap[(classifiable as ProcessorInfo).id] as T
    }

    fun setProcessorMap(processorMap: Map<Int, ItemProcessor<ProcessorInfo, ProcessorInfo>>) {
        this.processorMap = processorMap
    }
}

class ClassifierCustomItemProcessor1 : ItemProcessor<ProcessorInfo, ProcessorInfo> {

    override fun process(item: ProcessorInfo): ProcessorInfo {
        println("CustomItemProcessor1")
        return item
    }
}

class ClassifierCustomItemProcessor2 : ItemProcessor<ProcessorInfo, ProcessorInfo> {

    override fun process(item: ProcessorInfo): ProcessorInfo {
        println("CustomItemProcessor2")
        return item
    }
}


class ClassifierCustomItemProcessor3 : ItemProcessor<ProcessorInfo, ProcessorInfo> {

    override fun process(item: ProcessorInfo): ProcessorInfo {
        println("CustomItemProcessor3")
        return item
    }
}
