package com.example.ptbatch.domain.user

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user")
class UserEntity(
        @Id
        val id: String,

        var userName: String,

        @Enumerated(EnumType.STRING)
        var status: UserStatus,

        var phone: String,

        var meta: String


) : BaseEntity() {
}