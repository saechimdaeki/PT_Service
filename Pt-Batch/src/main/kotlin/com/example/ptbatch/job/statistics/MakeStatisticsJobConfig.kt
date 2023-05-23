package com.example.ptbatch.job.statistics

import com.example.ptbatch.domain.booking.BookingEntity
import com.example.ptbatch.domain.statistics.StatisticsEntity
import com.example.ptbatch.domain.statistics.StatisticsRepository
import com.example.ptbatch.util.LocalDateTimeUtils
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.job.flow.Flow
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaCursorItemReader
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime


@Configuration
class MakeStatisticsJobConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
    private val statisticsRepository: StatisticsRepository,
    private val makeWeeklyStatisticsTasklet: MakeWeeklyStatisticsTasklet,
    private val makeDailyStatisticsTasklet: MakeDailyStatisticsTasklet,
) {
    @Bean
    fun makeStatisticsJob() : Job {
        val addStatisticsFlow: Flow = FlowBuilder<Flow>("addStatisticsFlow")
            .start(addStatisticsStep())
            .build()

        val makeDailyStatisticsFlow: Flow = FlowBuilder<Flow>("makeDailyStatisticsFlow")
            .start(makeDailyStatisticsStep())
            .build()

        val makeWeeklyStatisticsFlow: Flow = FlowBuilder<Flow>("makeWeeklyStatisticsFlow")
            .start(makeWeeklyStatisticsStep())
            .build()

        val parallelMakeStatisticsFlow = FlowBuilder<Flow>("parallelMakeStatisticsFlow")
            .split(SimpleAsyncTaskExecutor())
            .add(makeDailyStatisticsFlow, makeWeeklyStatisticsFlow)
            .build()

        return JobBuilder("makeStatisticsJob",jobRepository)
            .start(addStatisticsFlow)
            .next(parallelMakeStatisticsFlow)
            .build()
            .build()
    }

    @Bean
    fun addStatisticsStep() : Step {
        return StepBuilder("addStatisticsStep", jobRepository)
            .chunk<BookingEntity, BookingEntity>(CHUNK_SIZE,transactionManager)
            .reader(addStatisticsItemReader(null,null))
            .writer(addStatisticsItemWriter())
            .build()
    }

    @Bean
    @StepScope
    fun addStatisticsItemReader(@Value("\${jobParameters[from]}") fromString: String?, @Value("\${jobParameters[to]}")toString : String?) : JpaCursorItemReader<BookingEntity> {
        val from = LocalDateTimeUtils.parse(fromString)
        val to = LocalDateTimeUtils.parse(toString)
        return JpaCursorItemReaderBuilder<BookingEntity>()
            .name("usePassesItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select b from BookingEntitiy b where b.endedAt between :from and :to")
            .parameterValues(mapOf("from" to from, "to" to to))
            .build()
    }

    @Bean
    fun addStatisticsItemWriter() : ItemWriter<BookingEntity> {
        return ItemWriter { bookingEntities ->
            val statisticsEntityMap: MutableMap<LocalDateTime, StatisticsEntity> =
                LinkedHashMap()
            for (bookingEntity in bookingEntities) {
                val statisticsAt = bookingEntity.getStatisticsAt()
                val statisticsEntity = statisticsEntityMap[statisticsAt]
                if (statisticsEntity == null) {
                    statisticsEntityMap[statisticsAt] = StatisticsEntity.create(bookingEntity)
                } else {
                    statisticsEntity.add(bookingEntity)
                }
            }
            val statisticsEntities: List<StatisticsEntity> =
                ArrayList(statisticsEntityMap.values)
            statisticsRepository.saveAll(statisticsEntities)
        }
    }

    @Bean
    fun makeDailyStatisticsStep() : Step {
        return StepBuilder("makeDailyStatisticsStep", jobRepository)
            .tasklet(makeDailyStatisticsTasklet, transactionManager)
            .build()
    }

    @Bean
    fun makeWeeklyStatisticsStep() : Step {
        return StepBuilder("makeWeeklyStatisticsStep", jobRepository)
            .tasklet(makeWeeklyStatisticsTasklet, transactionManager)
            .build()
    }

    companion object {
        const val CHUNK_SIZE = 10
    }
}