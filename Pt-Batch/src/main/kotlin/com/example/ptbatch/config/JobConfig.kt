package com.example.ptbatch.config

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

// @EnableBatchProcessing 을 붙이면 동작하지 않는다..? springboot3에서 (알아보고 수정하기)
@Configuration
class JobConfig(
        private val jobRepository: JobRepository,
        private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun simpleJob(): Job {
        return JobBuilder("simpleJob", jobRepository)
                .start(simpleStep())
                .build()
    }

    @Bean
    fun simpleStep(): Step {
        return StepBuilder("simpleStep", jobRepository)
                .tasklet({ contribution, chunkContext ->
                    println("Simple Step")
                    RepeatStatus.FINISHED
                }, transactionManager)
                .build()
    }
}
