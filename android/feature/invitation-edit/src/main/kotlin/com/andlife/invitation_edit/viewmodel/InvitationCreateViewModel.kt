package com.andlife.invitation_edit.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.andlife.domain.util.Result
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.map
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.editor.util.CardConverter
import com.andlife.editor.util.CreateCardSession
import com.andlife.invitation_edit.model.form.AnnouncementUiModel
import com.andlife.invitation_edit.model.form.CardUiModel
import com.andlife.invitation_edit.model.form.InvitationFormSideEffect
import com.andlife.invitation_edit.model.form.InvitationFormUiEvent
import com.andlife.invitation_edit.model.form.InvitationFormUiState
import com.andlife.invitation_edit.model.form.InvitationTimeUiModel
import com.andlife.invitation_edit.model.form.ThumbnailImageUiModel
import com.andlife.invitation_edit.model.form.toSaveParam
import com.andlife.invitation_edit.model.form.toLocalTime
import com.andlife.model.editor.CardImage
import com.andlife.model.editor.NachoUiCard
import com.andlife.model.editor.toDomain
import com.andlife.domain.util.RefreshEventHub
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationCreateViewModel @Inject constructor(
    private val createCardSession: CreateCardSession,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
    private val editorConverter: CardConverter,
    private val invitationRepository: InvitationRepository,
) : BaseViewModel<InvitationFormUiState, InvitationFormUiEvent, InvitationFormSideEffect>(
    InvitationFormUiState(),
) {
    override val uiState: StateFlow<InvitationFormUiState> = mutableUiState.asStateFlow()

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
            InvitationFormUiEvent.OnClickBack -> onBackClick()
            InvitationFormUiEvent.OnClickSave -> createInvitation()
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
        val imagesToAdd =
            event.imageList
                .take(availableCount)
                .map { ThumbnailImageUiModel(url = it) }

        if (imagesToAdd.isNotEmpty()) {
            val updatedImages = currentImages.addAll(imagesToAdd)
            updateState {
                copy(invitationFormUiModel = invitationFormUiModel.copy(imageList = updatedImages))
            }
        }
    }

    fun updateRemoveImage(event: InvitationFormUiEvent.RemoveImage) {
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
        val announcement = AnnouncementUiModel(title = event.title, content = event.content)
        val newAnnouncementList = uiState.value.invitationFormUiModel.announcement
            .toPersistentList()
            .add(announcement)
        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(announcement = newAnnouncementList))
        }
    }

    private fun updateRemoveAnnouncement(event: InvitationFormUiEvent.RemoveAnnouncement) {
        val newAnnouncementList = uiState.value.invitationFormUiModel.announcement
            .toPersistentList()
            .remove(event.announcement)
        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(announcement = newAnnouncementList),)
        }
    }

    fun getCardEditorResult() {
        val editable = createCardSession.editable ?: return
        val backgroundColor = createCardSession.backgroundColor
        val backgroundImageUrl = createCardSession.backgroundImageUrl
        val card = CardUiModel(editable, backgroundColor.toArgb(), backgroundImageUrl)
        updateState {
            copy(invitationFormUiModel = invitationFormUiModel.copy(card = card))
        }
    }

    private fun onBackClick() {
        createCardSession.clear()
        sendEffect(InvitationFormSideEffect.OnBack)
    }

    private fun createInvitation() {
        val uiModel = uiState.value.invitationFormUiModel

        val date = uiModel.date ?: run {
            return
        }
        val startTime = uiModel.startTime?.toLocalTime() ?: run {
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val thumbnails = uiState.value.invitationFormUiModel.imageList.map { it.url }
            val uploadedThumbnailsResult = uploadImages(thumbnails)
            if (uploadedThumbnailsResult !is Result.Success) {
                sendEffect(InvitationFormSideEffect.FailSave)
                return@launch
            }

            val cardResult = processCardAndUploadImages(uiModel.card)
            if (cardResult !is Result.Success) {
                sendEffect(InvitationFormSideEffect.FailSave)
                return@launch
            }

            val nachoCard = cardResult.data?.toDomain()

            val createParam = uiModel.toSaveParam(
                thumbnails = uploadedThumbnailsResult.data,
                date = date,
                startTime = startTime,
                endTime = uiModel.endTime?.toLocalTime(),
                invitationCard = nachoCard,
            )

            invitationRepository.createInvitation(params = createParam)
                .onSuccess { id ->
                    updateState { copy(isLoading = false) }
                    createCardSession.clear()
                    RefreshEventHub.emit(RefreshEventHub.RefreshTarget.ALL)
                    sendEffect(InvitationFormSideEffect.SuccessSave(id))
                }
                .onFailure { error, msg ->
                    Log.e("InvitationCreateViewModel", "에러 발생: $msg")
                    updateState { copy(isLoading = false) }
                    sendEffect(InvitationFormSideEffect.FailSave)
                }
        }
    }

    private suspend fun processCardAndUploadImages(
        cardUiModel: CardUiModel?
    ): Result<NachoUiCard?, DataError> {
        if (cardUiModel == null) return Result.Success(null)

        val richTextContent = editorConverter.toRichTextContent(cardUiModel.editable)

        val localImagesToUpload = richTextContent.images.filterIsInstance<CardImage.Local>()

        if (localImagesToUpload.isEmpty()) {
            return Result.Success(
                NachoUiCard(
                    content = richTextContent,
                    backgroundColor = cardUiModel.backgroundColor.toLong()
                )
            )
        }
        val uploadResult = uploadImages(localImagesToUpload.map { it.uri })
        if (uploadResult !is Result.Success) {
            return Result.Error((uploadResult as Result.Error).error)
        }

        val uploadedUrls = uploadResult.data
        val urlIterator = uploadedUrls.iterator()

        val newImages = richTextContent.images.map { image ->
            when (image) {
                is CardImage.Remote -> image
                is CardImage.Local -> {
                    if (urlIterator.hasNext()) {
                        CardImage.Remote(urlIterator.next())
                    } else {
                        return Result.Error(DataError.LocalImage.NotFound)
                    }
                }
            }
        }

        val newRichTextContent = richTextContent.copy(images = newImages)
        return Result.Success(
            NachoUiCard(
                content = newRichTextContent,
                backgroundColor = cardUiModel.backgroundColor.toLong(),
                backgroundImageUrl = cardUiModel.backgroundImageUrl
            )
        )
    }

    private suspend fun uploadImages(images: List<String>): Result<List<String>, DataError> {
        if (images.isEmpty()) return Result.Success(emptyList())

        val mediaFiles = mediaFileProvider.createFromUris(images)
        return mediaUploader.uploadMedias(mediaFiles)
            .map { urls -> urls.filterNotNull() }
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 10
    }
}
