package com.example.ptbatch.domain.packaze

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "package")
class PackageEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        var packageName: String,

        var count: Int,

        var period: Int

) : BaseEntity()