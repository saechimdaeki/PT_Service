package com.example.ptbatch.domain.notification

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notification")
class NotificationEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val notificationSeq: Long? = null,

        var uuid : String,

        @Enumerated(EnumType.STRING)
        var evnet: NotificationEvent,

        var text: String,

        var sent : Boolean,

        var sentAt : LocalDateTime


) : BaseEntity() {
}