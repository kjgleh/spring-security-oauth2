package com.example.springsecurityoauth2.service

import com.example.springsecurityoauth2.domain.User
import com.example.springsecurityoauth2.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = DefaultOAuth2UserService().loadUser(userRequest)
        val userName = oAuth2User.attributes["name"].toString()
        val userEmail = oAuth2User.attributes["email"].toString()

        val user = userRepository.findByName(userName)

        if (user == null) {
            userRepository.save(
                User(
                    name = userName,
                    email = userEmail,
                    oauth2RegistrationId = userRequest.clientRegistration.registrationId
                )
            )
        }

        return oAuth2User
    }
}