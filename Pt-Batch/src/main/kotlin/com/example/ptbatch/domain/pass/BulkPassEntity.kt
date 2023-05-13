package com.example.ptbatch.domain.pass

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name= "bulk_pass")
class BulkPassEntity (

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val bulkPassSeq : Long? = null,

        var packageSeq: Long,

        var userGroupId: String,

        @Enumerated(EnumType.STRING)
        var status : BulkPassStatus,

        var count: Int,

        var startedAt: LocalDateTime,

        var endedAt : LocalDateTime
) {
}