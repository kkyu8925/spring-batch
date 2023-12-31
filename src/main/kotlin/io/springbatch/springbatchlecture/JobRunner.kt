package io.springbatch.springbatchlecture

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner

//@Component
class JobRunner(
    private val jobLauncher: JobLauncher,
    private val job: Job
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val jobParameters = JobParametersBuilder()
            .addString("name", "user1")
            .toJobParameters()

        jobLauncher.run(job, jobParameters)
    }
}


