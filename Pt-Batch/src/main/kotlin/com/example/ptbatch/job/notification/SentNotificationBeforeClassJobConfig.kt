package com.example.ptbatch.job.notification

import com.example.ptbatch.domain.booking.BookingEntity
import com.example.ptbatch.domain.notification.NotificationEntity
import com.example.ptbatch.domain.notification.NotificationEvent
import com.example.ptbatch.domain.notification.NotificationModelMapper
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.support.SynchronizedItemStreamReader
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager


@Configuration
class SentNotificationBeforeClassJobConfig(
        private val jobRepository: JobRepository,
        private val transactionManager: PlatformTransactionManager,
        private val entityManagerFactory: EntityManagerFactory,
        private val sendNotificationItemWriter: SendNotificationItemWriter,
) {


    @Bean
    fun sendNotificationBeforeClassJob(): Job {
        return JobBuilder("sendNotificationBeforeClassJob", jobRepository)
                .start(addNotificationStep())
                .next(sendNotificationStep())
                .build()
    }

    @Bean
    fun addNotificationStep(): Step {
        return StepBuilder("addNotificationStep", jobRepository)
                .chunk<BookingEntity, NotificationEntity>(CHUNK_SIZE, transactionManager)
                .reader(addNotificationItemReader())
                .processor(addNotificationItemProcessor())
                .writer(addNotificationItemWriter())
                .build()
    }

    @Bean
    fun sendNotificationStep(): Step {

        return StepBuilder("sendNotificationStep", jobRepository)
                .chunk<NotificationEntity, NotificationEntity>(CHUNK_SIZE, transactionManager)
                .reader(sendNotificationItemReader())
                .writer(sendNotificationItemWriter)
                .taskExecutor(SimpleAsyncTaskExecutor())
                .build()
    }

    @Bean
    fun sendNotificationItemReader(): SynchronizedItemStreamReader<NotificationEntity> {
        val itemReader = JpaCursorItemReaderBuilder<NotificationEntity>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select n from NotificationEntity n where n.event = :event and n.sent = :sent")
                .parameterValues(mapOf(Pair("event", NotificationEvent.BEFORE_CLASS), Pair("sent", false)))
                .build()
        return SynchronizedItemStreamReaderBuilder<NotificationEntity>()
                .delegate(itemReader)
                .build()
    }

    @Bean
    fun addNotificationItemReader(): JpaPagingItemReader<BookingEntity> {
        return JpaPagingItemReader<BookingEntity>().apply {
            this.setQueryString("SELECT b FROM BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq ")
            this.setEntityManagerFactory(entityManagerFactory)
            this.pageSize = CHUNK_SIZE
        }
    }

    @Bean
    fun addNotificationItemProcessor(): ItemProcessor<BookingEntity?, NotificationEntity?> {
        return ItemProcessor { bookingEntity: BookingEntity? -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS) }
    }

    @Bean
    fun addNotificationItemWriter(): JpaItemWriter<NotificationEntity> {
        return JpaItemWriter<NotificationEntity>().apply {
            this.setEntityManagerFactory(entityManagerFactory)
        }
    }

    companion object {
        const val CHUNK_SIZE = 10
    }
}