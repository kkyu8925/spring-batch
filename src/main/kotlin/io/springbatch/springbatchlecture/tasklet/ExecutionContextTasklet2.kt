package io.springbatch.springbatchlecture.tasklet

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class ExecutionContextTasklet2 : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus {
        val jobExecutionContext = chunkContext.stepContext.stepExecution.jobExecution.executionContext
        val stepExecutionContext = chunkContext.stepContext.stepExecution.executionContext

        println("jobName: " + jobExecutionContext.get("jobName"))
        println("stepName: " + stepExecutionContext.get("stepName"))

        val stepName = chunkContext.stepContext.stepExecution.stepName
        if (stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.putString("stepName", stepName)
        }

        return RepeatStatus.FINISHED
    }
}
