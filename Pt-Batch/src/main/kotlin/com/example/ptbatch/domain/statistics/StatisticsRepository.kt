package com.example.ptbatch.domain.statistics

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime


interface StatisticsRepository : JpaRepository<StatisticsEntity, Long> {
    @Query(
        value = "SELECT new com.example.ptbatch.domain.statistics.AggregatedStatistics(s.statisticsAt, SUM(s.allCount), SUM(s.attendedCount), SUM(s.cancelledCount)) " +
                "         FROM StatisticsEntity s " +
                "        WHERE s.statisticsAt BETWEEN :from AND :to " +
                "     GROUP BY s.statisticsAt"
    )
    fun findByStatisticsAtBetweenAndGroupBy(
        @Param("from") from: LocalDateTime?,
        @Param("to") to: LocalDateTime?
    ): List<AggregatedStatistics>

}