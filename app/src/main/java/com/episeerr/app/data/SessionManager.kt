package com.episeerr.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks whether the server has told us (via a 401) that we need to (re-)authenticate.
 * Most Episeerr instances run with REQUIRE_AUTH off, so this starts "not needed" and only
 * flips on when the server actually rejects a request.
 */
@Singleton
class SessionManager @Inject constructor() {
    private val _needsLogin = MutableStateFlow(false)
    val needsLogin: StateFlow<Boolean> = _needsLogin

    fun markUnauthorized() {
        _needsLogin.value = true
    }

    fun markAuthenticated() {
        _needsLogin.value = false
    }
}
