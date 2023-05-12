package com.example.ptbatch.domain.packaze

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "package")
class PackageEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val packageSeq: Long? = null,

        var packageName: String,

        var count: Int? = null,

        var period: Int

) : BaseEntity()