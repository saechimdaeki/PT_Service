package com.example.ptbatch.domain.pass

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface PassRepository : JpaRepository<PassEntity, Long> {


    // batch기능에만 치중할것이기 때문에 따로 service layer로 구분짓지 않음
    @Transactional
    @Modifying
    @Query(value = "update PassEntity p set p.remainingCount = :remainingCount, p.modifiedAt = CURRENT_TIMESTAMP where p.passSeq = :passSeq")
    fun updateRemaingCount(passSeq: Long, remainingCount: Int): Int

}