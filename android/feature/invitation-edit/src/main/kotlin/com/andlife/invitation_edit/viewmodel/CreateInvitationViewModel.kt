package com.andlife.invitation_edit.viewmodel

import com.andlife.editor.util.CreateCardSession
import com.andlife.invitation_edit.model.create.AnnouncementUiModel
import com.andlife.invitation_edit.model.create.CardUiModel
import com.andlife.invitation_edit.model.create.CreateInvitationSideEffect
import com.andlife.invitation_edit.model.create.CreateInvitationUiEvent
import com.andlife.invitation_edit.model.create.CreateInvitationUiState
import com.andlife.invitation_edit.model.create.InvitationTimeUiModel
import com.andlife.invitation_edit.model.create.ThumbnailImageUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateInvitationViewModel @Inject constructor(
    private val createCardSession: CreateCardSession
) :
    BaseViewModel<CreateInvitationUiState, CreateInvitationUiEvent, CreateInvitationSideEffect>(
        CreateInvitationUiState(),
    ) {
    override val uiState: StateFlow<CreateInvitationUiState> = mutableUiState.asStateFlow()

    override fun onEvent(event: CreateInvitationUiEvent) {
        when (event) {
            is CreateInvitationUiEvent.UpdateAuthor -> {
                updateAuthor(event)
            }

            is CreateInvitationUiEvent.UpdateDate -> {
                updateDate(event)
            }

            is CreateInvitationUiEvent.UpdateAddressGuide -> {
                updateAddressGuide(event)
            }

            is CreateInvitationUiEvent.UpdateImageList -> {
                updateAddImageList(event)
            }

            is CreateInvitationUiEvent.UpdateStartTime -> {
                updateStartTime(event)
            }

            is CreateInvitationUiEvent.UpdateEndTime -> {
                updateEndTime(event)
            }

            is CreateInvitationUiEvent.UpdateTitle -> {
                updateTitle(event)
            }

            is CreateInvitationUiEvent.UpdateAddress -> {
                updateAddress(event)
            }

            is CreateInvitationUiEvent.UpdatePlaceAddress -> {
                updatePlaceAddress(event)
            }

            is CreateInvitationUiEvent.UpdateAnnouncement -> {
                updateAnnouncement(event)
            }

            is CreateInvitationUiEvent.RemoveImage -> {
                updateRemoveImage(event)
            }

            is CreateInvitationUiEvent.RemoveAnnouncement -> {
                updateRemoveAnnouncement(event)
            }

            CreateInvitationUiEvent.OnClickBack -> {
                onBackClick()
            }
        }
    }

    private fun updateTitle(event: CreateInvitationUiEvent.UpdateTitle) {
        updateState { copy(createInvitationUiModel = createInvitationUiModel.copy(title = event.title)) }
    }

    private fun updateAuthor(event: CreateInvitationUiEvent.UpdateAuthor) {
        updateState { copy(createInvitationUiModel = createInvitationUiModel.copy(author = event.author)) }
    }

    private fun updateAddImageList(event: CreateInvitationUiEvent.UpdateImageList) {
        val currentImages =
            uiState.value.createInvitationUiModel.imageList
                .toPersistentList()
        if (currentImages.size >= MAX_IMAGE_COUNT) {
            sendEffect(CreateInvitationSideEffect.FullImage)
            return
        }
        val availableCount = MAX_IMAGE_COUNT - currentImages.size
        val imagesToAdd =
            event.imageList
                .take(availableCount)
                .map { ThumbnailImageUiModel(url = it) }

        if (imagesToAdd.isNotEmpty()) {
            val updatedImages = currentImages.addAll(imagesToAdd)
            updateState {
                copy(createInvitationUiModel = createInvitationUiModel.copy(imageList = updatedImages))
            }
        }
    }

    fun updateRemoveImage(event: CreateInvitationUiEvent.RemoveImage) {
        val currentImages =
            uiState.value.createInvitationUiModel.imageList
                .toPersistentList()
        val updatedImages = currentImages.remove(event.image)
        updateState {
            copy(createInvitationUiModel = createInvitationUiModel.copy(imageList = updatedImages))
        }
    }

    private fun updateDate(event: CreateInvitationUiEvent.UpdateDate) {
        updateState { copy(createInvitationUiModel = createInvitationUiModel.copy(date = event.date)) }
    }

    private fun updateStartTime(event: CreateInvitationUiEvent.UpdateStartTime) {
        val time = InvitationTimeUiModel(hour = event.hour, min = event.min)
        updateState { copy(createInvitationUiModel = createInvitationUiModel.copy(startTime = time)) }
    }

    private fun updateEndTime(event: CreateInvitationUiEvent.UpdateEndTime) {
        val time = InvitationTimeUiModel(hour = event.hour, min = event.min)
        updateState { copy(createInvitationUiModel = createInvitationUiModel.copy(endTime = time)) }
    }

    private fun updateAddress(event: CreateInvitationUiEvent.UpdateAddress) {
        updateState {
            copy(
                createInvitationUiModel =
                    createInvitationUiModel.copy(
                        placeName = event.address.placeName,
                        placeAddress = event.address.roadAddressName,
                        lat = event.address.latitude,
                        lng = event.address.longitude,
                    ),
            )
        }
    }

    private fun updatePlaceAddress(event: CreateInvitationUiEvent.UpdatePlaceAddress) {
        updateState {
            copy(
                createInvitationUiModel = createInvitationUiModel.copy(placeAddress = event.placeAddress),
            )
        }
    }

    private fun updateAddressGuide(event: CreateInvitationUiEvent.UpdateAddressGuide) {
        updateState {
            copy(
                createInvitationUiModel = createInvitationUiModel.copy(placeGuide = event.addressGuide),
            )
        }
    }

    private fun updateAnnouncement(event: CreateInvitationUiEvent.UpdateAnnouncement) {
        val announcement = AnnouncementUiModel(title = event.title, content = event.content)
        val newAnnouncementList =
            uiState.value.createInvitationUiModel.announcement
                .toPersistentList()
                .add(announcement)
        updateState {
            copy(
                createInvitationUiModel = createInvitationUiModel.copy(announcement = newAnnouncementList),
            )
        }
    }

    private fun updateRemoveAnnouncement(event: CreateInvitationUiEvent.RemoveAnnouncement) {
        val newAnnouncementList =
            uiState.value.createInvitationUiModel.announcement.toPersistentList().remove(event.announcement)
        updateState {
            copy(
                createInvitationUiModel = createInvitationUiModel.copy(announcement = newAnnouncementList),
            )
        }
    }

    fun getCardEditorResult() {
        val editable = createCardSession.editable ?: return
        val backgroundColor = createCardSession.backgroundColor
        val card = CardUiModel(editable, backgroundColor)
        updateState {
            copy(createInvitationUiModel = createInvitationUiModel.copy(card = card))
        }
    }

    private fun onBackClick() {
        createCardSession.clear()
        sendEffect(CreateInvitationSideEffect.OnBack)
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 10
    }
}
