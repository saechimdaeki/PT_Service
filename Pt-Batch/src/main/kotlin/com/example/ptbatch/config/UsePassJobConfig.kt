package com.example.ptbatch.config

import com.example.ptbatch.domain.booking.BookRepository
import com.example.ptbatch.domain.booking.BookingEntity
import com.example.ptbatch.domain.booking.BookingStatus
import com.example.ptbatch.domain.pass.PassRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.springframework.batch.integration.async.AsyncItemWriter
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.FutureTask


@Configuration
class UsePassJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val passRepository: PassRepository,
    private val bookRepository: BookRepository,

) {

    @Bean
    fun usePassJob() : Job {
        return JobBuilder("usePassJob", jobRepository)
            .start(usePasseStep())
            .build()
    }

    @Bean
    fun usePasseStep() : Step {
        return StepBuilder("usePasseStep", jobRepository)
            .chunk<BookingEntity, Future<BookingEntity>>(CHUNK_SIZE,transactionManager)
            .reader(usePassItemReader())
            .processor(usePassAsyncItemProcessor())
            .writer(usePassAsyncItemWriter())
            .build()

    }

    @Bean
    fun usePassAsyncItemWriter(): ItemWriter<in Future<BookingEntity>> {
        val asyncItemWriter = AsyncItemWriter<BookingEntity>()
        asyncItemWriter.setDelegate(usePassesItemWriter()!!) // usePassesItemWriter 최종 결과값을 넘겨주고 작업을 위임합니다.
        return asyncItemWriter
    }
    @Bean
    fun usePassItemReader() : JpaCursorItemReader<BookingEntity> {
        return JpaCursorItemReaderBuilder<BookingEntity>()
            .name("usePassItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select b from BookingEntity b join fetch b.passEntity where b.status = :status and b.usedPass = false and b.endedAt < :endedAt")
            .parameterValues(mapOf("status" to BookingStatus.COMPLETED, "endedAt" to LocalDateTime.now()))
            .build()
    }

    @Bean
    fun usePassItemProcessor() : ItemProcessor<BookingEntity, BookingEntity> {
        return ItemProcessor { bookingEntity ->
            val passEntity = bookingEntity.passEntity
            passEntity.remainingCount = passEntity.remainingCount -1
            bookingEntity.passEntity = passEntity

            bookingEntity.usedPass = true
            bookingEntity
        }
    }

    @Bean
    fun usePassAsyncItemProcessor(): ItemProcessor<in BookingEntity, out Future<BookingEntity>> {
        val asyncItemProcessor = AsyncItemProcessor<BookingEntity, Future<BookingEntity>>()
        asyncItemProcessor.setDelegate(usePassesItemProcessor())
        asyncItemProcessor.setTaskExecutor(SimpleAsyncTaskExecutor())
        return asyncItemProcessor as ItemProcessor<in BookingEntity, out Future<BookingEntity>>
    }

    @Bean
    fun usePassesItemProcessor(): ItemProcessor<BookingEntity, Future<BookingEntity>> {
        return ItemProcessor { bookingEntity: BookingEntity ->
            val passEntity = bookingEntity.passEntity
            passEntity.remainingCount = passEntity.remainingCount - 1
            bookingEntity.passEntity = passEntity
            bookingEntity.usedPass = true
            val futureResult = FutureTask { bookingEntity }
            futureResult.run()
            futureResult
        }
    }

    @Bean
    fun usePassesAsyncItemWriter(): AsyncItemWriter<BookingEntity>? {
        val asyncItemWriter = AsyncItemWriter<BookingEntity>()
        asyncItemWriter.setDelegate(usePassesItemWriter()!!)
        return asyncItemWriter
    }

    @Bean
    fun usePassesItemWriter(): ItemWriter<BookingEntity>? {
        return ItemWriter<BookingEntity> { bookingEntities: Chunk<out BookingEntity> ->
            for (bookingEntity in bookingEntities) {
                val updatedCount = passRepository.updateRemaingCount(bookingEntity.passSeq, bookingEntity.passEntity.remainingCount)
                if (updatedCount > 0) {
                    bookRepository.updatedUsedPass(bookingEntity.passSeq, bookingEntity.usedPass)
                }
            }
        }
    }





    companion object {
        const val CHUNK_SIZE = 10
    }
}