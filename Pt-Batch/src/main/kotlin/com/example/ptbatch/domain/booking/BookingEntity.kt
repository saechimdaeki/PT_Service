package com.example.ptbatch.domain.booking

import com.example.ptbatch.domain.common.BaseEntity
import com.example.ptbatch.domain.pass.PassEntity
import com.example.ptbatch.domain.user.UserEntity
import io.netty.util.concurrent.Future
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "booking")
class BookingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val bookingSeq: Long? = null,

    var passSeq: Long,

    var userId: String,

    var usedPass: Boolean,

    var attended: Boolean,

    @Enumerated(EnumType.STRING)
    var status: BookingStatus,

    var startedAt: LocalDateTime,

    var endedAt: LocalDateTime,

    var cancelledAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    var userEntity: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passSeq", insertable = false, updatable = false)
    var passEntity: PassEntity


) : BaseEntity() {

    fun getStatisticsAt(): LocalDateTime {
        return endedAt.withHour(0).withMinute(0).withSecond(0).withNano(0)
    }
}