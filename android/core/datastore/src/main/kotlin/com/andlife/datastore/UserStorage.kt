package com.andlife.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class UserStorage @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private val USER_ID = longPreferencesKey("USER_ID")
        private val GUEST_INVITATION_IDS = stringSetPreferencesKey("GUEST_INVITATION_IDS")
    }

    fun getUserId(): Long? = runBlocking {
        context.dataStore.data.first()[USER_ID]
    }

    fun getInvitationIds(): List<Long> = runBlocking {
        val ids = context.dataStore.data.first()[GUEST_INVITATION_IDS] ?: emptySet()
        ids.mapNotNull { it.toLongOrNull() }
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { it[USER_ID] = userId }
    }

    suspend fun addInvitationId(invitationId: Long) {
        context.dataStore.edit { prefs ->
            val currentIds = prefs[GUEST_INVITATION_IDS] ?: emptySet()
            prefs[GUEST_INVITATION_IDS] = currentIds + invitationId.toString()
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun deleteInvitationId(invitationId: Long) {
        context.dataStore.edit { prefs ->
            val currentIds = prefs[GUEST_INVITATION_IDS] ?: emptySet()
            prefs[GUEST_INVITATION_IDS] = currentIds - invitationId.toString()
        }
    }
}
