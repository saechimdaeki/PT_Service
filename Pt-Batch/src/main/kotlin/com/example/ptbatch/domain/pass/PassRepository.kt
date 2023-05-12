package com.example.ptbatch.domain.pass

import org.springframework.data.jpa.repository.JpaRepository

interface PassRepository : JpaRepository<PassEntity, Long> {
}