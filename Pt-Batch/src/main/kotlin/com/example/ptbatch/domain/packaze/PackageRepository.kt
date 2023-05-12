package com.example.ptbatch.domain.packaze

import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface PackageRepository : JpaRepository<PackageEntity, Long> {
    fun findByCreatedAtAfter(dateTime: LocalDateTime?, of: PageRequest) : List<PackageEntity>


    // TODO 추후 패키지 이동 후 수정(chore 테스트용)
    @Transactional
    @Modifying
    @Query("update PackageEntity p set p.count = :count, p.period = :period where p.packageSeq = :id")
    fun updateCountAndPeriod(id: Long, count: Int, period: Int): Int

}