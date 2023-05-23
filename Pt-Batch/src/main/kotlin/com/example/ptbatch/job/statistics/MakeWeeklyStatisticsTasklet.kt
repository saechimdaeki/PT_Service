package com.example.ptbatch.job.statistics

import com.example.ptbatch.domain.statistics.AggregatedStatistics
import com.example.ptbatch.domain.statistics.StatisticsRepository
import com.example.ptbatch.util.CustomCSVWriter
import com.example.ptbatch.util.LocalDateTimeUtils
import com.example.ptbatch.util.LocalDateTimeUtils.format
import com.example.ptbatch.util.LocalDateTimeUtils.getWeekOfYear
import com.example.ptbatch.util.LocalDateTimeUtils.parse
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@StepScope
@Component
class MakeWeeklyStatisticsTasklet(
    private val statisticsRepository: StatisticsRepository
) : Tasklet{

    @Value("\${jobParameters[from]}")
    lateinit var fromString : String
    @Value("\${jobParameters[to]}")
    lateinit var toString : String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val from = parse(fromString)
        val to = parse(toString)

        val statisticsList = statisticsRepository.findByStatisticsAtBetweenAndGroupBy(from, to)
        val weeklyStatisticsEntityMap: MutableMap<Int, AggregatedStatistics> = LinkedHashMap()

        for (statistics in statisticsList) {
            val week = getWeekOfYear(statistics.statisticsAt)
            val savedStatisticsEntity = weeklyStatisticsEntityMap[week]
            if (savedStatisticsEntity == null) {
                weeklyStatisticsEntityMap[week] = statistics
            } else {
                savedStatisticsEntity.merge(statistics)
            }
        }

        val data: MutableList<Array<String>> = ArrayList()
        data.add(arrayOf("week", "allCount", "attendedCount", "cancelledCount"))
        weeklyStatisticsEntityMap.forEach { (week: Int, statistics: AggregatedStatistics) ->
            data.add(
                arrayOf(
                    "Week $week",
                    statistics.allCount.toString(),
                    statistics.attendedCount.toString(),
                    statistics.cancelledCount.toString()
                )
            )
        }
        CustomCSVWriter.write("weekly_statistics_" + format(from!!, LocalDateTimeUtils.YYYY_MM_DD) + ".csv", data)
        return RepeatStatus.FINISHED
    }
}