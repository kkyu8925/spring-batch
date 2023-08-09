package io.springbatch.springbatchlecture.controller

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class JobLauncherController(
    private val jobLauncher: JobLauncher,
    private val job: Job
) {

    @PostMapping("/batch")
    fun launch(@RequestBody id: String): String {
        val jobParameters = JobParametersBuilder()
            .addString("id", id)
            .addDate("date", Date())
            .toJobParameters()

        jobLauncher.run(job, jobParameters)
        return "batch completed"
    }
}
