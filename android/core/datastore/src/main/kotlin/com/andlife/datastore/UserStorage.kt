package com.andlife.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val WIFI_DIALOG_DISMISSED = booleanPreferencesKey("wifi_dialog_dismissed")
private val FIRST_DOWNLOAD_DONE = booleanPreferencesKey("first_download_done")

@Singleton
class UserStorage @Inject constructor(
    @param:UserDataStore private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val GUEST_INVITATION_IDS = stringSetPreferencesKey("GUEST_INVITATION_IDS")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val WAS_LOGGED_IN = booleanPreferencesKey("was_logged_in")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")

        const val SAMPLE_INVITATION_ID = 1L
    }

    fun getInvitationIds(): List<Long> = runBlocking {
        val ids = dataStore.data.first()[GUEST_INVITATION_IDS] ?: emptySet()
        ids.mapNotNull { it.toLongOrNull() }.distinct()
    }

    suspend fun addInvitationId(invitationId: Long) {
        dataStore.edit { prefs ->
            val currentIds = prefs[GUEST_INVITATION_IDS] ?: emptySet()
            prefs[GUEST_INVITATION_IDS] = currentIds + invitationId.toString()
        }
    }

    suspend fun deleteInvitationId(invitationId: Long) {
        dataStore.edit { prefs ->
            val currentIds = prefs[GUEST_INVITATION_IDS] ?: emptySet()
            prefs[GUEST_INVITATION_IDS] = currentIds - invitationId.toString()
        }
    }

    suspend fun clearGuestData() {
        dataStore.edit { prefs ->
            prefs[GUEST_INVITATION_IDS] = setOf(SAMPLE_INVITATION_ID.toString())
        }
    }

    suspend fun setWasLoggedIn(wasLoggedIn: Boolean) {
        dataStore.edit { prefs ->
            prefs[WAS_LOGGED_IN] = wasLoggedIn
        }
    }

    suspend fun getWasLoggedIn(): Boolean {
        return dataStore.data.first()[WAS_LOGGED_IN] ?: false
    }

    // NOTE: 호출 하는 곳에서 예외 처리 해줘야 함
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[ACCESS_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[REFRESH_TOKEN]
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(WAS_LOGGED_IN)
        }
    }

    suspend fun isWifiDialogDismissed(): Boolean {
        return dataStore.data.first()[WIFI_DIALOG_DISMISSED] ?: false
    }

    suspend fun setWifiDialogDismissed() {
        dataStore.edit { it[WIFI_DIALOG_DISMISSED] = true }
    }

    suspend fun isFirstDownloadDone(): Boolean {
        return dataStore.data.first()[FIRST_DOWNLOAD_DONE] ?: false
    }

    suspend fun setFirstDownloadDone() {
        dataStore.edit { it[FIRST_DOWNLOAD_DONE] = true }
    }

    suspend fun isFirstLaunch(): Boolean = dataStore.data.first()[IS_FIRST_LAUNCH] ?: true

    suspend fun setFirstLaunchDone() {
        dataStore.edit { it[IS_FIRST_LAUNCH] = false }
    }
}
