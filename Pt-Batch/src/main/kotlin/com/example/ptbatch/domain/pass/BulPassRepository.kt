package com.example.ptbatch.domain.pass

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BulPassRepository : JpaRepository<BulkPassEntity, Long> {

    fun findByStatusAndStartedAtGreaterThan(status : BulkPassStatus, startedAt : LocalDateTime) : List<BulkPassEntity>
}