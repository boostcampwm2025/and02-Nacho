package com.andlife.myinvitation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.andlife.myinvitation.MyInvitation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val MY_INVITATION_ID = "my_invitation_id"

@HiltViewModel
class MyInvitation2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val route = savedStateHandle.toRoute<MyInvitation>()

    val initialInvitationId = savedStateHandle.getStateFlow(
        key = MY_INVITATION_ID,
        initialValue = route.initialInvitationId
    )

    fun setInitialInvitationId(id: Long) {
        savedStateHandle[MY_INVITATION_ID] = id
    }
}
