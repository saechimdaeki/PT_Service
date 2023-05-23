package com.example.ptbatch.domain.statistics

import com.example.ptbatch.domain.booking.BookingEntity
import com.example.ptbatch.domain.booking.BookingStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "statistics")
class StatisticsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val statisticsSeq: Long? = null,

    var statisticsAt : LocalDateTime,

    var allCount : Int,

    var attendedCount : Int,

    var cancelledCount : Int
) {

    fun add(bookingEntity: BookingEntity) {
        allCount += 1
        if (bookingEntity.attended) {
            attendedCount += 1
        }
        if (bookingEntity.status == BookingStatus.CANCELLED) {
            cancelledCount += 1
        }
    }

    companion object {
        fun create(bookingEntity: BookingEntity) : StatisticsEntity {
            return StatisticsEntity(
                statisticsAt = bookingEntity.getStatisticsAt(),
                allCount = 1,
                attendedCount = if (bookingEntity.attended) 1 else 0,
                cancelledCount = if (bookingEntity.status == BookingStatus.CANCELLED) 1 else 0
            )
        }
    }
}