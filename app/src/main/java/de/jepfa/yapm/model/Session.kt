package de.jepfa.yapm.model

import java.util.concurrent.TimeUnit
import javax.crypto.SecretKey

object Session {

    /**
     * After this period of time of inactivity the secret is outdated.
     */
    private val SECRET_KEEP_VALID: Long = TimeUnit.SECONDS.toMillis(60)
    private val LOGOUT_AFTER: Long = TimeUnit.SECONDS.toMillis(600)

    private var masterSecretKey: SecretKey? = null
    private var masterPassword: Encrypted? = null
    private var lastUpdated: Long = 0

    fun getMasterKeySK() : SecretKey? {
        return masterSecretKey
    }

    fun getEncMasterPasswd() :Encrypted? {
        return masterPassword
    }

    fun login(secretKey: SecretKey, encMasterPasswd: Encrypted) {
        masterSecretKey = secretKey
        masterPassword = encMasterPasswd
        touch()
    }

    fun touch() {
        lastUpdated = System.currentTimeMillis()
    }

    fun safeTouch() {
        if (!isLoggedOut() && !isLocked()) {
            touch()
        }
    }

    fun isOutdated(): Boolean {
        return age() > SECRET_KEEP_VALID || shouldBeLoggedOut()
    }

    fun shouldBeLoggedOut(): Boolean {
        return age() > LOGOUT_AFTER
    }

    fun isLocked() : Boolean {
        return masterSecretKey == null
    }

    fun isLoggedOut() : Boolean {
        return masterPassword == null
    }

    fun isDenied() : Boolean {
        return isLoggedOut() || isLocked() || isOutdated()
    }

    fun lock() {
        masterSecretKey = null
        touch()
    }

    fun logout() {
        masterPassword = null
        lock()
    }

    private fun age() = System.currentTimeMillis() - lastUpdated

}
