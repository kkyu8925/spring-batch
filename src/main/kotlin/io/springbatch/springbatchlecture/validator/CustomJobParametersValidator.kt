package io.springbatch.springbatchlecture.validator

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.JobParametersValidator

class CustomJobParametersValidator : JobParametersValidator {

    override fun validate(parameters: JobParameters?) {
        checkNotNull(parameters) {
            throw JobParametersInvalidException("JobParameters is not null")
        }

        checkNotNull(parameters.getString("name")) {
            throw JobParametersInvalidException("name parameters is not null")
        }
    }
}
