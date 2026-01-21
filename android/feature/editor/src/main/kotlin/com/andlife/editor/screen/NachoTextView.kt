package com.andlife.editor.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.doOnLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.andlife.domain.util.getOrNull
import com.andlife.editor.util.CardConverter
import com.andlife.invitation_card.editor.utils.CenteredImageSpan
import com.andlife.invitation_card.editor.utils.ImageLoader
import com.andlife.model.editor.NachoUiCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.graphics.drawable.toDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NachoTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    @Inject lateinit var converter: CardConverter
    @Inject lateinit var imageLoader: ImageLoader

    private var loadJob: Job? = null
    private var lastBoundCard: NachoUiCard? = null
    private var currentSpannable: SpannableStringBuilder? = null

    fun bind(card: NachoUiCard?) {
        if (lastBoundCard == card) return
        lastBoundCard = card

        loadJob?.cancel()

        if (card == null) {
            text = ""
            return
        }

        setInitialContent(card)

        doOnLayout { view ->
            val lifecycleScope = findViewTreeLifecycleOwner()?.lifecycleScope
            loadJob = lifecycleScope?.launch {
                loadImages(view.width, card)
            }
        }
    }

    private fun setInitialContent(card: NachoUiCard) {
        val content = card.content
        val spannable = converter.toSpannable(content)
        val positions = converter.findImagePlaceholderPositions(content.text)

        val estimatedWidth = resources.displayMetrics.widthPixels
        val estimatedHeight = (estimatedWidth * 9f / 16f).toInt()

        positions.forEachIndexed { index, position ->
            content.images.getOrNull(index)?.let { image ->
                val placeholderDrawable = createPlaceholderDrawable(estimatedWidth, estimatedHeight)
                val placeholderSpan = CenteredImageSpan(placeholderDrawable, estimatedWidth, image.source)
                spannable.setSpan(
                    placeholderSpan,
                    position,
                    position + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        currentSpannable = spannable
        text = spannable
    }

    private suspend fun loadImages(targetWidth: Int, card: NachoUiCard) {
        val targetHeight = (targetWidth * 9f / 16f).toInt()
        val content = card.content
        val spannable = currentSpannable ?: return
        val positions = converter.findImagePlaceholderPositions(content.text)

        positions.forEachIndexed { index, position ->
            content.images.getOrNull(index)?.let { image ->
                val existingSpans = spannable.getSpans(
                    position,
                    position + 1,
                    CenteredImageSpan::class.java
                )
                existingSpans.forEach { spannable.removeSpan(it) }

                val placeholderDrawable = createPlaceholderDrawable(targetWidth, targetHeight)
                val placeholderSpan = CenteredImageSpan(placeholderDrawable, targetWidth, image.source)
                spannable.setSpan(
                    placeholderSpan,
                    position,
                    position + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        text = spannable
        requestLayout()

        val loadedImages = coroutineScope {
            positions.mapIndexedNotNull { index, position ->
                content.images.getOrNull(index)?.let { image ->
                    async {
                        imageLoader.loadFromUrl(
                            context = context,
                            url = image.source,
                            maxWidth = targetWidth,
                            maxHeight = targetHeight
                        ).getOrNull()?.let { bitmap ->
                            LoadedImage(position, bitmap, image.source)
                        }
                    }
                }
            }.awaitAll().filterNotNull()
        }

        loadedImages.forEach { loadedImage ->
            val existingSpans = spannable.getSpans(
                loadedImage.position,
                loadedImage.position + 1,
                CenteredImageSpan::class.java
            )
            existingSpans.forEach { spannable.removeSpan(it) }

            val drawable = loadedImage.bitmap.toDrawable(resources)
            drawable.setBounds(0, 0, targetWidth, targetHeight)

            val imageSpan = CenteredImageSpan(drawable, targetWidth, loadedImage.url)
            spannable.setSpan(
                imageSpan,
                loadedImage.position,
                loadedImage.position + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        text = spannable
        requestLayout()
        invalidate()
    }

    private fun createPlaceholderDrawable(width: Int, height: Int): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.LTGRAY)
            setBounds(0, 0, width, height)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loadJob?.cancel()
    }

    private data class LoadedImage(
        val position: Int,
        val bitmap: Bitmap,
        val url: String
    )
}
