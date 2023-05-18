package com.example.ptbatch.domain.user

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user")
class UserEntity(
        @Id
        val userId: String,

        var userName: String,

        @Enumerated(EnumType.STRING)
        var status: UserStatus,

        var phone: String,

        @Convert(converter = MetaJsonConverter::class)
        val meta : Map<String, Any>


) : BaseEntity() {
    fun getUuid(): String? {
        var uuid: String? = null

        if (meta.containsKey("uuid")) {
            uuid = meta["uuid"]!!.toString()
        }
        return uuid
    }
}