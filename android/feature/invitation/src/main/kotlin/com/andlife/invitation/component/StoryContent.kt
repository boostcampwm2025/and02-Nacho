package com.andlife.invitation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiModel
import com.andlife.ui.model.UiMediaType

@Composable
fun StoryPageContent(
    item: InvitationCollectionUiModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(InvitationTheme.colorScheme.backgroundOverlay)
    ) {
        when(item.type) {
            UiMediaType.IMAGE -> {
                AsyncImage(
                    model = item.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            UiMediaType.VIDEO -> {
                // TODO: VideoPlayer 컴포넌트 배치 (ExoPlayer)
                Box(modifier = Modifier.fillMaxSize().background(InvitationTheme.colorScheme.backgroundOverlay))
            }
            UiMediaType.AUDIO -> {

            }
        }
        StoryTextSection(
            content = item.content,
            isExpanded = isExpanded,
            onToggleExpand = onToggleExpand,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
