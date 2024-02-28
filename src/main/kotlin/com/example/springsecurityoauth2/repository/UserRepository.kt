package com.example.springsecurityoauth2.repository

import com.example.springsecurityoauth2.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
}