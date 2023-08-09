package io.springbatch.springbatchlecture.tasklet

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class ExecutionContextTasklet1 : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        Thread.sleep(10000)

        val jobExecutionContext = chunkContext.stepContext.stepExecution.jobExecution.executionContext
        val stepExecutionContext = chunkContext.stepContext.stepExecution.executionContext

        val jobName = chunkContext.stepContext.stepExecution.jobExecution.jobInstance.jobName
        val stepName = chunkContext.stepContext.stepExecution.stepName

        if (jobExecutionContext.get("jobName") == null) {
            jobExecutionContext.putString("jobName", jobName)
        }
        if (stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.putString("stepName", stepName)
        }

        println("jobName: " + jobExecutionContext.get("jobName"))
        println("stepName: " + stepExecutionContext.get("stepName"))

        return RepeatStatus.FINISHED
    }
}
