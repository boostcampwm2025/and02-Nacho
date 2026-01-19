package com.andlife.myinvitation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiState
import com.andlife.ui.section.detail.AddressSection
import com.andlife.ui.section.detail.AnnouncementSection
import com.andlife.ui.section.detail.AuthorSection
import com.andlife.ui.section.detail.DateSection
import com.andlife.ui.section.detail.ImageSection
import com.andlife.ui.section.detail.InvitationCardSection
import com.andlife.ui.section.detail.PlaceGuideSection
import com.andlife.ui.section.detail.TitleSection

@Composable
fun MyInvitationContentsScreen(
    uiState: MyInvitationDetailUiState,
    onClickImage: (Int) -> Unit,
    onClickEditCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundTertiary),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
            ) {
                CircularProgressIndicator(
                    color = NachoTheme.colorScheme.brandPrimary,
                )
                Text(
                    text = stringResource(R.string.txt_loading_invitation),
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                )
            }
        }
        return
    }
    val model = uiState.invitationContentsUiModel
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier =
            modifier
                .fillMaxSize()
                .background(NachoTheme.colorScheme.backgroundTertiary),
    ) {
        item {
            ImageSection(
                imageUrls = model.imageList,
                onImageClick = onClickImage,
            )
        }
        item {
            TitleSection(
                title = model.title,
            )
        }
        item {
            AuthorSection(
                profileUrl = model.hostInfo.profileUrl,
                author = model.hostInfo.name,
            )
        }
        item {
            DateSection(
                date = model.dateTime.date,
                startTime = model.dateTime.startTime,
            )
        }
        item {
            AddressSection(
                placeName = model.location.name,
                placeAddress = model.location.address,
            )
        }
        item {
            model.invitationCard?.let { card ->
                InvitationCardSection(
                    invitationCardUiModel = card,
                    isEditable = true,
                    onEditClick = onClickEditCard,
                )
            }
        }
        item {
            AnnouncementSection(
                announcements = model.announcement,
            )
        }
        item {
            PlaceGuideSection(
                location = model.location,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun MyInvitationContentsScreenPreview() {
    NachoTheme {
        MyInvitationContentsScreen(
            uiState =
                MyInvitationDetailUiState(
                    isLoading = false,
                ),
            onClickImage = {},
            onClickEditCard = {},
        )
    }
}
