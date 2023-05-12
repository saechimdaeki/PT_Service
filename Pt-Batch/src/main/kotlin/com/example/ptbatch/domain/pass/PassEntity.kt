package com.example.ptbatch.domain.pass

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "pass")
class PassEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        var packageSeq: Long,

        var userId: String,

        @Enumerated(EnumType.STRING)
        var status: PassStatus,

        var remainingCount: Int,

        var startedAt: LocalDateTime,

        var endedAt: LocalDateTime,

        var expiredAt: LocalDateTime

) : BaseEntity() {
}