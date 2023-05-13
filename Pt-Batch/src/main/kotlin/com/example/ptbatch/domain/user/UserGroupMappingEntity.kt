package com.example.ptbatch.domain.user

import com.example.ptbatch.domain.common.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table

@Entity
@Table(name = "user_gorup_mapping")
@IdClass(UserGroupMappingId::class)
class UserGroupMappingEntity(
        @Id
        val userGroupId: String,

        @Id
        val userId: String,

        var userGroupName: String,

        var description : String

)  : BaseEntity(){
}