package com.example.ptbatch.domain.statistics

import java.time.LocalDateTime

class AggregatedStatistics(
    var statisticsAt : LocalDateTime,
    var allCount : Long,
    var attendedCount : Long,
    var cancelledCount : Long
) {

    fun merge(statistics: AggregatedStatistics) {
        allCount += statistics.allCount
        attendedCount += statistics.attendedCount
        cancelledCount += statistics.cancelledCount
    }
}