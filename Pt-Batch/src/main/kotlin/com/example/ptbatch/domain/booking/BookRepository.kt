package com.example.ptbatch.domain.booking

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface BookRepository : JpaRepository<BookingEntity , Long> {

    @Transactional //임시
    @Modifying
    @Query(value = "update BookingEntity b set b.usedPass = :usedPass, b.modifiedAt = CURRENT_TIMESTAMP where b.passSeq = :passSeq")
    fun updatedUsedPass(passSeq: Long, usedPass: Boolean): Int
}