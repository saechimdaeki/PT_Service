package com.example.ptbatch.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserGroupMappingRepository  : JpaRepository<UserGroupMappingEntity, String>{

    fun findByUserGroupId(userGroupId: String) : List<UserGroupMappingEntity>
}