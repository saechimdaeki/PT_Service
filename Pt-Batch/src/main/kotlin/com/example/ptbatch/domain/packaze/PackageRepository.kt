package com.example.ptbatch.domain.packaze

import org.springframework.data.jpa.repository.JpaRepository

interface PackageRepository : JpaRepository<PackageEntity, Long> {
}