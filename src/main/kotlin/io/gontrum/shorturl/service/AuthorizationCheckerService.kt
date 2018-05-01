package io.gontrum.shorturl.service

import io.gontrum.shorturl.exception.NotAuthorizedException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AuthorizationCheckerService {
    fun checkAuthenticationFor(authorities: List<String>, vararg authoritiesToCheckFor: String) {
        if (!authorities.any { authoritiesToCheckFor.contains(it) }) throw NotAuthorizedException("user is not authorized to perform action")
    }

    fun getAuthoritiesAsStrings(authentication: Authentication): List<String> {
        return authentication.authorities.map { it.authority }
    }
}