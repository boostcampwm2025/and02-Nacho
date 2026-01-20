package com.andlife.invitation.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.ui.section.detail.AddressSection
import com.andlife.ui.section.detail.AnnouncementSection
import com.andlife.ui.section.detail.AuthorSection
import com.andlife.ui.section.detail.DateSection
import com.andlife.ui.section.detail.ImageSection
import com.andlife.ui.section.detail.InvitationCardSection
import com.andlife.ui.section.detail.PlaceGuideSection
import com.andlife.ui.section.detail.TitleSection


@Composable
fun InvitationContentsScreen(
    uiState: InvitationDetailUiState,
    onClickImage: (Int) -> Unit,
    onMapError: () -> Unit,
    isMapVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val model = uiState.invitationContentsUiModel
    val scrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(NachoTheme.colorScheme.backgroundTertiary)
                .verticalScroll(scrollState),
    ) {
        ImageSection(
            imageUrls = model.imageList,
            onImageClick = onClickImage,
        )

        TitleSection(
            title = model.title,
        )

        AuthorSection(
            profileUrl = model.hostInfo.profileUrl,
            author = model.hostInfo.name,
        )

        DateSection(
            dateTime = model.dateTime,
        )

        AddressSection(
            placeName = model.location.name,
            placeAddress = model.location.address,
        )

        InvitationCardSection(
            invitationCardUiModel = model.invitationCard,
            isEditable = false,
        )

        AnnouncementSection(
            announcements = model.announcement,
        )

        PlaceGuideSection(
            location = model.location,
            onMapError = onMapError,
            isMapVisible = isMapVisible,
        )
    }
}

@PreviewTheme
@Composable
private fun InvitationContentsScreenPreview() {
    NachoTheme {
        InvitationContentsScreen(
            uiState =
                InvitationDetailUiState(
                    isLoading = false,
                ),
            onClickImage = {},
            onMapError = {},
            isMapVisible = true,
        )
    }
}
