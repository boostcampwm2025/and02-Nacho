package com.andlife.thanks_card.viewmodel

import android.text.Editable
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.thankscard.ThanksCardRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.editor.state.EditorState
import com.andlife.editor.util.CardConverter
import com.andlife.model.editor.CardImage
import com.andlife.model.editor.NachoUiCard
import com.andlife.model.editor.RichTextUiContent
import com.andlife.model.editor.toDomain
import com.andlife.thanks_card.CreateThanksCard
import com.andlife.thanks_card.model.create.CreateThanksSideEffect
import com.andlife.thanks_card.model.create.CreateThanksUiEvent
import com.andlife.thanks_card.model.create.CreateThanksUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateThanksCardViewModel @Inject constructor(
    private val textEditor: EditorState,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
    private val editorConverter: CardConverter,
    private val savedStateHandle: SavedStateHandle,
    private val thanksCardRepository: ThanksCardRepository
): BaseViewModel<CreateThanksUiState, CreateThanksUiEvent, CreateThanksSideEffect>(CreateThanksUiState()) {

    private val invitationId = savedStateHandle.toRoute<CreateThanksCard>().invitationId

    override val uiState: StateFlow<CreateThanksUiState> = mutableUiState.asStateFlow()

    override fun onEvent(event: CreateThanksUiEvent) {
        when (event) {
            CreateThanksUiEvent.OnClickBack -> sendEffect(CreateThanksSideEffect.OnBack)
            CreateThanksUiEvent.OnClickCreate -> createThanksCard()
        }
    }

    fun getEditorState(): EditorState {
        return textEditor
    }

    private fun createThanksCard() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val editable = textEditor.editText?.text ?: return@launch
            val textContent = processRichTextContent(editable)

            if (textContent !is Result.Success) {
                sendEffect(CreateThanksSideEffect.FailCreateThanksCard)
                return@launch
            }

            val nachoCard = NachoUiCard(
                id = invitationId,
                content = textContent.data,
                backgroundColor = textEditor.currentTextStyle.backgroundColor.toArgb().toLong(),
                backgroundImageUrl = textEditor.currentBackgroundEffect

            ).toDomain()
            thanksCardRepository.createThanksCard(invitationId, nachoCard)
                .onSuccess {
                    sendEffect(CreateThanksSideEffect.SuccessCreateThanksCard)
                }
                .onFailure { error, msg ->
                    Log.d("CreateThanksCardFail", "$msg")
                    sendEffect(CreateThanksSideEffect.FailCreateThanksCard)
                }
            updateState { copy(isLoading = false) }
        }
    }

    private suspend fun processRichTextContent(editable: Editable): Result<RichTextUiContent, DataError> {
        val richTextContent = editorConverter.toRichTextContent(editable)
        val localImagesToUpload = richTextContent.images.filterIsInstance<CardImage.Local>()
        val uploadImageResult = uploadImages(localImagesToUpload.map { it.uri })

        if (uploadImageResult !is Result.Success || uploadImageResult.data.size != localImagesToUpload.size) {
            sendEffect(CreateThanksSideEffect.FailCreateThanksCard)
            return Result.Error((uploadImageResult as Result.Error).error)
        }

        val newImages = richTextContent.images.mapIndexed { index, image ->
            CardImage.Remote(uploadImageResult.data[index])
        }

        return Result.Success(richTextContent.copy(images = newImages))
    }

    private suspend fun uploadImages(images: List<String>): Result<List<String>, DataError> {
        if (images.isEmpty()) return Result.Success(emptyList())

        val mediaFiles = mediaFileProvider.createFromUris(images)
        return mediaUploader.uploadMedias(mediaFiles)
            .map { urls -> urls.filterNotNull() }
    }
}
