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
import com.andlife.invitation_edit.model.create.AnnouncementUiModel
import com.andlife.invitation_edit.model.create.CardUiModel
import com.andlife.invitation_edit.model.create.CreateInvitationSideEffect
import com.andlife.invitation_edit.model.create.CreateInvitationUiEvent
import com.andlife.invitation_edit.model.create.CreateInvitationUiState
import com.andlife.invitation_edit.model.create.InvitationTimeUiModel
import com.andlife.invitation_edit.model.create.ThumbnailImageUiModel
import com.andlife.invitation_edit.model.create.toCreateParam
import com.andlife.invitation_edit.model.create.toLocalTime
import com.andlife.model.editor.CardImage
import com.andlife.model.editor.NachoUiCard
import com.andlife.model.editor.toDomain
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateInvitationViewModel @Inject constructor(
    private val createCardSession: CreateCardSession,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
    private val editorConverter: CardConverter,
    private val invitationRepository: InvitationRepository,
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

            CreateInvitationUiEvent.OnClickCreate -> {
                createInvitation()
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
        val backgroundImageUrl = createCardSession.backgroundImageUrl
        val card = CardUiModel(editable, backgroundColor.toArgb(), backgroundImageUrl)
        updateState {
            copy(createInvitationUiModel = createInvitationUiModel.copy(card = card))
        }
    }

    private fun onBackClick() {
        createCardSession.clear()
        sendEffect(CreateInvitationSideEffect.OnBack)
    }

    private fun createInvitation() {
        val uiModel = uiState.value.createInvitationUiModel

        val date = uiModel.date ?: run {
            return
        }
        val startTime = uiModel.startTime?.toLocalTime() ?: run {
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val thumbnails = uiState.value.createInvitationUiModel.imageList.map { it.url }
            val uploadedThumbnailsResult = uploadImages(thumbnails)
            if (uploadedThumbnailsResult !is Result.Success) {
                sendEffect(CreateInvitationSideEffect.FailCreate)
                return@launch
            }

            val cardResult = processCardAndUploadImages(uiModel.card)
            if (cardResult !is Result.Success) {
                sendEffect(CreateInvitationSideEffect.FailCreate)
                return@launch
            }

            val nachoCard = cardResult.data?.toDomain()

            val createParam = uiModel.toCreateParam(
                thumbnails = uploadedThumbnailsResult.data,
                date = date,
                startTime = startTime,
                endTime = uiModel.endTime?.toLocalTime(),
                invitationCard = nachoCard,
            )

            invitationRepository.createInvitation(params = createParam)
                .onSuccess { id ->
                    updateState { copy(isLoading = false) }
                    sendEffect(CreateInvitationSideEffect.SuccessCreate(id))
                }
                .onFailure {
                    updateState { copy(isLoading = false) }
                    sendEffect(CreateInvitationSideEffect.FailCreate)
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

    private suspend fun uploadImages(
        images: List<String>
    ): Result<List<String>, DataError> {
        if (images.isEmpty()) return Result.Success(emptyList())

        val mediaFiles = mediaFileProvider.createFromUris(images)
        return mediaUploader.uploadMedias(mediaFiles)
            .map { urls -> urls.filterNotNull() }
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 10
    }
}
