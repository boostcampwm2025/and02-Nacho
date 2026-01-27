package com.andlife.invitation_edit.screen.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import kotlinx.collections.immutable.toImmutableList
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.model.create.CreateInvitationUiState
import com.andlife.invitation_edit.section.CardSection
import com.andlife.invitation_edit.section.TopBarSection
import com.andlife.invitation_edit.viewmodel.CreateInvitationViewModel
import com.andlife.model.invitation.DateTimeInfo
import com.andlife.model.invitation.LocationInfo
import com.andlife.model.invitation.TimeUiModel
import com.andlife.model.invitation.AnnouncementUiModel
import com.andlife.ui.section.detail.AddressSection
import com.andlife.ui.section.detail.AnnouncementSection
import com.andlife.ui.section.detail.AuthorSection
import com.andlife.ui.section.detail.DateSection
import com.andlife.ui.section.detail.EmptyAddressSection
import com.andlife.ui.section.detail.EmptyAuthorSection
import com.andlife.ui.section.detail.EmptyDateSection
import com.andlife.ui.section.detail.EmptyImageSection
import com.andlife.ui.section.detail.EmptyItemSection
import com.andlife.ui.section.detail.EmptyTitleSection
import com.andlife.ui.section.detail.ImageSection
import com.andlife.ui.section.detail.PlaceGuideSection
import com.andlife.ui.section.detail.TitleSection

@Composable
fun InvitationPreviewRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateInvitationViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationPreviewScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
private fun InvitationPreviewScreen(
    uiState: CreateInvitationUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarSection(
                title = stringResource(R.string.txt_preview),
                onBackClick = onNavigateBack,
                onPreviewClick = {},
                isPreviewMode = true,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InvitationPreviewContent(
                uiState = uiState,
                modifier = Modifier.fillMaxSize()
            )

            // 하단 고정 미리보기 안내 박스
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(NachoSpacing.large)
                    .background(
                        color = NachoTheme.colorScheme.backgroundOverlay,
                        shape = NachoTheme.shapes.small,
                    )
                    .padding(NachoSpacing.large),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "미리보기 화면입니다",
                    color = NachoTheme.colorScheme.textOnPrimary,
                    style = NachoTheme.typography.bodyMediumMedium
                )
            }
        }
    }
}

@Composable
private fun InvitationPreviewContent(
    uiState: CreateInvitationUiState,
    modifier: Modifier = Modifier,
) {
    val model = uiState.createInvitationUiModel
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NachoTheme.colorScheme.backgroundTertiary)
            .verticalScroll(scrollState),
    ) {
        // ImageSection
        if (model.imageList.isEmpty()) {
            EmptyImageSection()
        } else {
            ImageSection(
                imageUrls = model.imageList.map { it.url }.toImmutableList(),
                onImageClick = { }
            )
        }

        // TitleSection
        if (model.title.isEmpty()) {
            EmptyTitleSection()
        } else {
            TitleSection(title = model.title)
        }

        // Author Section
        if (model.author.isEmpty()) {
            EmptyAuthorSection()
        } else {
            AuthorSection(
                profileUrl = null,
                author = model.author
            )
        }

        // DateSection
        val hasDate = model.date != null
        val hasTime = model.startTime != null

        if (!hasDate || !hasTime) {
            EmptyDateSection()
        } else {
            DateSection(
                dateTime = DateTimeInfo(
                    date = model.date,
                    startTime = TimeUiModel(model.startTime.hour, model.startTime.min),
                )
            )
        }

        // AddressSection
        if (model.placeName.isEmpty() && model.placeAddress.isEmpty()) {
            EmptyAddressSection()
        } else {
            AddressSection(
                placeName = model.placeName,
                placeAddress = model.placeAddress
            )
        }

        // CardSection
        if (model.card == null) {
            EmptyItemSection(
                title = stringResource(R.string.txt_card),
                placeholder = stringResource(R.string.txt_invitation_card_placeholder),
            )
        } else {
            CardSection(
                cardUiModel = model.card,
                onClickCreatedCard = {},
            )
        }

        // AnnouncementSection
        if (model.announcement.isEmpty()) {
            EmptyItemSection(
                title = stringResource(R.string.txt_announcement),
                placeholder = stringResource(R.string.txt_announcement_placeholder),
            )
        } else {
            AnnouncementSection(
                announcements = model.announcement.map {
                    AnnouncementUiModel(
                        title = it.title,
                        content = it.content
                    )
                }.toImmutableList()
            )
        }

        // PlaceGuideSection
        if (model.placeGuide.isEmpty()) {
            EmptyItemSection(
                title = stringResource(R.string.txt_place_guide),
                placeholder = stringResource(R.string.txt_place_guide_placeholder),
            )
        } else {
            PlaceGuideSection(
                location = LocationInfo(
                    name = model.placeName,
                    address = model.placeAddress,
                    guide = model.placeGuide,
                ),
                onMapError = { },
            )
        }
    }
}

@Composable
@PreviewTheme
fun InvitationPreviewScreenPreview() {
    InvitationPreviewScreen(
        uiState = CreateInvitationUiState(),
        onNavigateBack = {},
    )
}
