package io.springbatch.springbatchlecture.decider

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider

class CustomDecider : JobExecutionDecider {

    private var count = 0;

    override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
        count++

        return if (count % 2 == 0) {
            FlowExecutionStatus("EVEN")
        } else {
            FlowExecutionStatus("ODD")
        }
    }
}
