package com.example.springsecurityoauth2.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "users")
class User(
    var name: String,
    var email: String,
    @Column(name = "oauth2_registration_id")
    val oauth2RegistrationId: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id = 0L
}