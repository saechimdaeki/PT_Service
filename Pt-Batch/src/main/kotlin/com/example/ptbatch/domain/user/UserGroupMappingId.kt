package com.example.ptbatch.domain.user

import java.io.Serializable

class UserGroupMappingId(
        val userGroupId: String,
        val userId: String
) : Serializable {
}