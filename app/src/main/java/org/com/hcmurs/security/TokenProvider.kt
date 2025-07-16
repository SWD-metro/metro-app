package org.com.hcmurs.security

interface TokenProvider {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}
