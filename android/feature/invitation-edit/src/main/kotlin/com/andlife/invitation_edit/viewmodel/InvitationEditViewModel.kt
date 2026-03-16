package com.andlife.invitation_edit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.RefreshEventHub
import com.andlife.domain.util.Result
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation_edit.InvitationEdit
import com.andlife.invitation_edit.model.form.AnnouncementUiModel
import com.andlife.invitation_edit.model.form.InvitationFormSideEffect
import com.andlife.invitation_edit.model.form.InvitationFormUiEvent
import com.andlife.invitation_edit.model.form.InvitationFormUiState
import com.andlife.invitation_edit.model.form.InvitationTimeUiModel
import com.andlife.invitation_edit.model.form.ThumbnailImageUiModel
import com.andlife.invitation_edit.model.form.toLocalTime
import com.andlife.invitation_edit.model.form.toSaveParam
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InvitationEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
    private val invitationRepository: InvitationRepository,
) : BaseViewModel<InvitationFormUiState, InvitationFormUiEvent, InvitationFormSideEffect>(
    InvitationFormUiState(),
) {
    private val invitationId: Long = savedStateHandle.toRoute<InvitationEdit>().id

    override val uiState: StateFlow<InvitationFormUiState> =
        mutableUiState
            .onStart {
                loadInvitation()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = InvitationFormUiState()
            )

    override fun onEvent(event: InvitationFormUiEvent) {
        when (event) {
            is InvitationFormUiEvent.UpdateAuthor -> updateAuthor(event)
            is InvitationFormUiEvent.UpdateDate -> updateDate(event)
            is InvitationFormUiEvent.UpdateAddressGuide -> updateAddressGuide(event)
            is InvitationFormUiEvent.UpdateImageList -> updateAddImageList(event)
            is InvitationFormUiEvent.UpdateStartTime -> updateStartTime(event)
            is InvitationFormUiEvent.UpdateEndTime -> updateEndTime(event)
            is InvitationFormUiEvent.UpdateTitle -> updateTitle(event)
            is InvitationFormUiEvent.UpdateAddress -> updateAddress(event)
            is InvitationFormUiEvent.UpdatePlaceAddress -> updatePlaceAddress(event)
            is InvitationFormUiEvent.UpdateAnnouncement -> updateAnnouncement(event)
            is InvitationFormUiEvent.RemoveImage -> updateRemoveImage(event)
            is InvitationFormUiEvent.RemoveAnnouncement -> updateRemoveAnnouncement(event)
            is InvitationFormUiEvent.ReorderAnnouncement -> {
                reorderAnnouncement(event.fromIndex, event.toIndex)
            }
            InvitationFormUiEvent.OnClickBack -> onBackClick()
            InvitationFormUiEvent.OnClickSave -> updateInvitation()
            InvitationFormUiEvent.OnClickPreview -> {}
        }
    }

    private suspend fun loadInvitation() {
        updateState { copy(isLoading = true) }
        invitationRepository.getInvitation(invitationId)
            .onSuccess { invitation ->
                updateState {
                    copy(
                        invitationId = invitation.id,
                        cardId = invitation.invitationCard?.id,
                        invitationFormUiModel = invitationFormUiModel.copy(
                            title = invitation.title,
                            author = invitation.displayHostName,
                            imageList = invitation.thumbnailUrls.mapIndexed { index, url ->
                                ThumbnailImageUiModel(id = "$PREFIX_EXISTING_IMAGE$index", url = url)
                            }.toPersistentList(),
                            date = invitation.invitationDate,
                            startTime = InvitationTimeUiModel(
                                hour = invitation.startTime.hour,
                                min = invitation.startTime.minute
                            ),
                            endTime = invitation.endTime?.let {
                                InvitationTimeUiModel(hour = it.hour, min = it.minute)
                            },
                            placeName = invitation.placename,
                            placeAddress = invitation.address,
                            placeGuide = invitation.locationGuide ?: "",
                            lat = invitation.latitude,
                            lng = invitation.longitude,
                            announcement = invitation.announcements.map {
                                AnnouncementUiModel(
                                    title = it.title,
                                    content = it.content,
                                    displayOrder = it.displayOrder
                                )
                            }.toPersistentList(),
                        ),
                        isLoading = false,
                    )
                }
            }
            .onFailure { _, _ ->
                updateState { copy(isLoading = false) }
                sendEffect(InvitationFormSideEffect.FailLoad)
            }
    }

    private fun updateTitle(event: InvitationFormUiEvent.UpdateTitle) {
        updateState { copy(invitationFormUiModel = invitationFormUiModel.copy(title = event.title)) }
    }

    private fun updateAuthor(event: InvitationFormUiEvent.UpdateAuthor) {
        updateState { copy(invitationFormUiModel = invitationFormUiModel.copy(author = event.author)) }
    }

    private fun updateAddImageList(event: InvitationFormUiEvent.UpdateImageList) {
        val currentImages = uiState.value.invitationFormUiModel.imageList.toPersistentList()
        if (currentImages.size >= MAX_IMAGE_COUNT) {
            sendEffect(InvitationFormSideEffect.FullImage)
            return
        }
        val availableCount = MAX_IMAGE_COUNT - currentImages.size
        val imagesToAdd = event.imageList
            .take(availableCount)
            .map { ThumbnailImageUiModel(id = UUID.randomUUID().toString(), url = it) }

        if (imagesToAdd.isNotEmpty()) {
            val updatedImages = currentImages.addAll(imagesToAdd)
            updateState {
                copy(invitationFormUiModel = invitationFormUiModel.copy(imageList = updatedImages))
            }
        }
    }

    private fun updateRemoveImage(event: InvitationFormUiEvent.RemoveImage) {
        val currentImages = uiState.value.invitationFormUiModel.imageList.toPersistentList()
        val updatedImages = currentImages.remove(event.image)
        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(imageList = updatedImages))
        }
    }

    private fun updateDate(event: InvitationFormUiEvent.UpdateDate) {
        updateState { copy(invitationFormUiModel = invitationFormUiModel.copy(date = event.date)) }
    }

    private fun updateStartTime(event: InvitationFormUiEvent.UpdateStartTime) {
        val time = InvitationTimeUiModel(hour = event.hour, min = event.min)
        val updatedModel = uiState.value.invitationFormUiModel.copy(startTime = time)

        if (!updatedModel.isEndTimeValid) {
            sendEffect(InvitationFormSideEffect.InvalidTime)
        }

        updateState { copy(invitationFormUiModel = updatedModel) }
    }

    private fun updateEndTime(event: InvitationFormUiEvent.UpdateEndTime) {
        val time = InvitationTimeUiModel(hour = event.hour, min = event.min)
        val updatedModel = uiState.value.invitationFormUiModel.copy(endTime = time)

        if (!updatedModel.isEndTimeValid) {
            sendEffect(InvitationFormSideEffect.InvalidTime)
        }

        updateState { copy(invitationFormUiModel = updatedModel) }
    }

    private fun updateAddress(event: InvitationFormUiEvent.UpdateAddress) {
        updateState {
            copy(
                invitationFormUiModel = invitationFormUiModel.copy(
                    placeName = event.address.placeName,
                    placeAddress = event.address.roadAddressName,
                    lat = event.address.latitude,
                    lng = event.address.longitude,
                ),
            )
        }
    }

    private fun updatePlaceAddress(event: InvitationFormUiEvent.UpdatePlaceAddress) {
        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(placeAddress = event.placeAddress))
        }
    }

    private fun updateAddressGuide(event: InvitationFormUiEvent.UpdateAddressGuide) {
        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(placeGuide = event.addressGuide))
        }
    }

    private fun updateAnnouncement(event: InvitationFormUiEvent.UpdateAnnouncement) {
        val currentList = uiState.value.invitationFormUiModel.announcement.toMutableList()

        val existingIndex = currentList.indexOfFirst { it.id == event.id }

        if (existingIndex != -1) {
            currentList[existingIndex] = currentList[existingIndex].copy(
                title = event.title,
                content = event.content
            )
        } else {
            val newAnnouncement = AnnouncementUiModel(
                title = event.title,
                content = event.content,
                displayOrder = currentList.size
            )
            currentList.add(newAnnouncement)
        }

        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(
                announcement = currentList.toPersistentList()
            ))
        }
    }

    private fun updateRemoveAnnouncement(event: InvitationFormUiEvent.RemoveAnnouncement) {
        val currentList = uiState.value.invitationFormUiModel.announcement.toMutableList()
        currentList.remove(event.announcement)

        val updatedList = currentList.mapIndexed { index, announcement ->
            announcement.copy(displayOrder = index)
        }.toPersistentList()

        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(announcement = updatedList))
        }
    }

    private fun onBackClick() {
        sendEffect(InvitationFormSideEffect.OnBack)
    }

    private fun updateInvitation() {
        val uiModel = uiState.value.invitationFormUiModel
        val date = uiModel.date ?: return
        val startTime = uiModel.startTime?.toLocalTime() ?: return

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val thumbnailResult = processThumbnails(uiModel.imageList)

            if (thumbnailResult !is Result.Success) {
                updateState { copy(isLoading = false) }
                sendEffect(InvitationFormSideEffect.FailSave)
                return@launch
            }

            val updateParam = uiModel.toSaveParam(
                thumbnails = thumbnailResult.data,
                date = date,
                startTime = startTime,
                endTime = uiModel.endTime?.toLocalTime(),
                invitationCard = null,
            )

            invitationRepository.updateInvitation(invitationId, updateParam)
                .onSuccess { id ->
                    updateState { copy(isLoading = false) }
                    RefreshEventHub.emit(RefreshEventHub.RefreshTarget.ALL)
                    sendEffect(InvitationFormSideEffect.SuccessSave(id))
                }
                .onFailure { _, _ ->
                    updateState { copy(isLoading = false) }
                    sendEffect(InvitationFormSideEffect.FailSave)
                }
        }
    }

    private suspend fun processThumbnails(currentImages: List<ThumbnailImageUiModel>): Result<List<String>, DataError> {
        val newImages = currentImages.filter { !it.id.startsWith(PREFIX_EXISTING_IMAGE) }

        if (newImages.isEmpty()) {
            return Result.Success(currentImages.map { it.url })
        }

        val newImageUrls = newImages.map { it.url }
        val mediaFiles = mediaFileProvider.createFromUris(newImageUrls)

        if (mediaFiles.isEmpty()) return Result.Error(DataError.LocalImage.NotFound)

        val uploadResult = mediaUploader.uploadMedias(mediaFiles)

        return when (uploadResult) {
            is Result.Success -> {
                val uploadedUrls = uploadResult.data.filterNotNull()

                if (uploadedUrls.size != newImages.size) {
                    return Result.Error(DataError.Network.UNKNOWN)
                }

                var newUploadIndex = 0
                val finalUrls = currentImages.mapNotNull { image ->
                    if (image.id.startsWith(PREFIX_EXISTING_IMAGE)) {
                        image.url
                    } else {
                        uploadedUrls.getOrNull(newUploadIndex++)
                    }
                }

                if (finalUrls.size != currentImages.size) {
                    Result.Error(DataError.Network.UNKNOWN)
                } else {
                    Result.Success(finalUrls)
                }
            }
            is Result.Error -> Result.Error(uploadResult.error)
        }
    }

    private fun reorderAnnouncement(fromIndex: Int, toIndex: Int) {
        val from = fromIndex - 8
        val to = toIndex - 8

        val currentList = uiState.value.invitationFormUiModel.announcement

        if (from !in currentList.indices || to !in currentList.indices) return

        val mutableList = currentList.toMutableList()

        val item = mutableList.removeAt(from)
        mutableList.add(to, item)

        updateState {
            copy(
                invitationFormUiModel = invitationFormUiModel.copy(
                    announcement = mutableList.toPersistentList()
                )
            )
        }
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 10
        private const val PREFIX_EXISTING_IMAGE = "EXISTING_"
    }
}
