package com.andlife.invitation_card.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.RichTextContent
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.editor.state.EditorState
import com.andlife.editor.util.CardConverter
import com.andlife.invitation_card.CreateCardByInvitation
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationSideEffect
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationUiEvent
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationUiState
import com.andlife.model.editor.CardImage
import com.andlife.model.editor.NachoUiCard
import com.andlife.model.editor.RichTextUiContent
import com.andlife.model.editor.toDomain
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class CreateByInvitationViewModel @Inject constructor(
    private val editorState: EditorState,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
    private val editorConverter: CardConverter,
    private val savedStateHandle: SavedStateHandle,
    private val invitationRepository: InvitationRepository
) : BaseViewModel<CreateByInvitationUiState, CreateByInvitationUiEvent, CreateByInvitationSideEffect>(
    CreateByInvitationUiState(editorState)
) {

    private val id = savedStateHandle.toRoute<CreateCardByInvitation>().invitationId

    override val uiState: StateFlow<CreateByInvitationUiState> = mutableUiState.asStateFlow()

    override fun onEvent(event: CreateByInvitationUiEvent) {
        when (event) {
            CreateByInvitationUiEvent.ClickBack -> {
                sendEffect(CreateByInvitationSideEffect.NavigateBack)
            }

            CreateByInvitationUiEvent.ClickSave -> {
                createCard()
            }
        }
    }

    private fun createCard() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val cardData = uiState.value.editorState.editText ?: run {
                sendEffect(CreateByInvitationSideEffect.FailCreateCard)
                updateState { copy(isLoading = false) }
                return@launch
            }
            val richTextContent = editorConverter.toRichTextContent(cardData.text)
            val nachoUiCard = uploadImages(richTextContent)
            if (nachoUiCard !is Result.Success) {
                sendEffect(CreateByInvitationSideEffect.FailCreateCard)
                updateState { copy(isLoading = false) }
                return@launch
            }
            val nachoCard = nachoUiCard.data.toDomain()
            invitationRepository.createInvitationCard(id, nachoCard)
                .onSuccess {
                    Log.d("CreateByInvitationViewModel", "createCard: success")
                    sendEffect(CreateByInvitationSideEffect.SuccessCreateCard)
                }
                .onFailure { error, msg ->
                    Log.d("CreateByInvitationViewModel", "Fail: success")
                    sendEffect(CreateByInvitationSideEffect.FailCreateCard)
                }
            updateState { copy(isLoading = false) }
        }
    }

    private suspend fun uploadImages(card: RichTextUiContent): Result<NachoUiCard, DataError> {
        val backgroundColor = uiState.value.editorState.currentTextStyle.backgroundColor
        val backgroundImageUrl = uiState.value.editorState.currentBackgroundImageUrl
        val localImagesToUpload = card.images.filterIsInstance<CardImage.Local>()

        if (localImagesToUpload.isEmpty()) {
            return Result.Success(
                NachoUiCard(
                    content = card,
                    backgroundColor = backgroundColor.toArgb().toLong(),
                    backgroundImageUrl = backgroundImageUrl
                )
            )
        }
        val uploadResult = uploadImages(localImagesToUpload.map { it.uri })
        if (uploadResult !is Result.Success) {
            return Result.Error((uploadResult as Result.Error).error)
        }
        val uploadedUrls = uploadResult.data
        val urlIterator = uploadedUrls.iterator()

        val newImages = card.images.map { image ->
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

        val newRichTextContent = card.copy(images = newImages)
        return Result.Success(
            NachoUiCard(
                content = newRichTextContent,
                backgroundColor = backgroundColor.toArgb().toLong(),
                backgroundImageUrl = backgroundImageUrl
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
}
