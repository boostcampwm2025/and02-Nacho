package com.andlife.thanks_card.viewmodel

import android.graphics.Bitmap
import android.util.Log
import android.widget.EditText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.thankscard.ThanksCardRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.editor.state.EditorState
import com.andlife.editor.util.CardConverter
import com.andlife.invitation_card.editor.utils.ImageLoader
import com.andlife.model.editor.CardImage
import com.andlife.model.editor.NachoUiCard
import com.andlife.model.editor.RichTextUiContent
import com.andlife.model.editor.toDomain
import com.andlife.model.editor.toUiModel
import com.andlife.thanks_card.UpdateThanksCard
import com.andlife.thanks_card.model.update.UpdateThanksCardSideEffect
import com.andlife.thanks_card.model.update.UpdateThanksCardUiEvent
import com.andlife.thanks_card.model.update.UpdateThanksCardUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateThanksCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val thanksCardRepository: ThanksCardRepository,
    private val cardConverter: CardConverter,
    private val imageLoader: ImageLoader,
    private val editorState: EditorState,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
) : BaseViewModel<UpdateThanksCardUiState, UpdateThanksCardUiEvent, UpdateThanksCardSideEffect>(UpdateThanksCardUiState()) {

    val cardId = savedStateHandle.toRoute<UpdateThanksCard>().cardId

    override val uiState: StateFlow<UpdateThanksCardUiState> = mutableUiState
        .onStart {
            loadThanksCard()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = UpdateThanksCardUiState()
        )

    override fun onEvent(event: UpdateThanksCardUiEvent) {
        when (event) {
            UpdateThanksCardUiEvent.OnClickBack -> sendEffect(UpdateThanksCardSideEffect.OnBack)
            UpdateThanksCardUiEvent.OnClickRetry -> {
                loadThanksCard()
            }

            UpdateThanksCardUiEvent.OnClickSaveChanges -> updateThanksCard()
        }
    }

    fun getEditorState(): EditorState {
        return editorState
    }

    private fun loadThanksCard() {
        viewModelScope.launch {
            thanksCardRepository.getThanksCard(cardId)
                .onSuccess { nachoCard ->
                    runCatching {
                        convertSpannable(nachoCard.toUiModel())
                    }.onFailure {
                        updateState { copy(isError = true) }
                    }
                }
                .onFailure { error, msg ->
                    updateState { copy(isError = true) }
                }
        }
    }

    private suspend fun convertSpannable(nachoUiCard: NachoUiCard) {
        val editText = editorState.editText ?: return
        val spannable = cardConverter.toSpannable(nachoUiCard.content)
        editorState.setBackground(Color(nachoUiCard.backgroundColor))
        editorState.setEditable(spannable)
        loadImagesAndInsert(nachoUiCard, editText)
        updateState { copy(isLoading = false, isError = false) }
    }

    private suspend fun loadImagesAndInsert(nachoUiCard: NachoUiCard, editTextView: EditText) = coroutineScope {
        val content = nachoUiCard.content
        val imagePositions = cardConverter.findImagePlaceholderPositions(content.text)

        val targetWidth = editTextView.width.takeIf { it > 0 }
            ?: (editTextView.resources.displayMetrics.widthPixels * 0.7f).toInt()
        val targetHeight = (targetWidth * 9f / 16f).toInt()

        val deferredImages = imagePositions.mapIndexed { index, position ->
            val imageUrl = content.images.getOrNull(index)?.source ?: return@mapIndexed null

            async {
                val result = imageLoader.loadFromUrl(editTextView.context, imageUrl, targetWidth, targetWidth)
                if (result !is Result.Success) {
                    throw IllegalStateException()
                }
                LoadedUrlImage(position, result.data, imageUrl)
            }
        }.filterNotNull()

        val results = deferredImages.awaitAll()

        results.forEach { loadedUrlImage ->
            editorState.applyImageSpan(
                loadedUrlImage.bitmap,
                loadedUrlImage.url,
                loadedUrlImage.position,
                loadedUrlImage.position + 1,
                targetWidth,
                targetHeight
            )
        }
    }

    private fun updateThanksCard() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val cardData = editorState.editText ?: run {
                failUpdateThanksCard()
                return@launch
            }
            val richTextContent = cardConverter.toRichTextContent(cardData.text)
            val nachoUiCard = uploadImages(richTextContent)

            if (nachoUiCard !is Result.Success) {
                failUpdateThanksCard()
                return@launch
            }
            val nachoCard = nachoUiCard.data.toDomain()
            thanksCardRepository.updateThanksCard(cardId = cardId, card = nachoCard)
                .onSuccess {
                    sendEffect(UpdateThanksCardSideEffect.OnSuccessUpdateThanksCard)
                    updateState { copy(isLoading = false) }
                }
                .onFailure { error, msg ->
                    failUpdateThanksCard()
                }
        }
    }

    private fun failUpdateThanksCard() {
        sendEffect(UpdateThanksCardSideEffect.OnFailUpdateThanksCard)
        updateState { copy(isLoading = false) }
    }

    private suspend fun uploadImages(card: RichTextUiContent): Result<NachoUiCard, DataError> {
        val backgroundColor = editorState.currentTextStyle.backgroundColor
        val backgroundImageUrl = editorState.currentBackgroundImageUrl
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
                id = cardId,
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

private data class LoadedUrlImage(
    val position: Int,
    val bitmap: Bitmap,
    val url: String
)
