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
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.MyInvitationDetailUiState
import com.andlife.ui.section.invitation.AddressSection
import com.andlife.ui.section.invitation.AnnouncementSection
import com.andlife.ui.section.invitation.AuthorSection
import com.andlife.ui.section.invitation.DateSection
import com.andlife.ui.section.invitation.ImageSection
import com.andlife.ui.section.invitation.InvitationCardSection
import com.andlife.ui.section.invitation.PlaceGuideSection
import com.andlife.ui.section.invitation.TitleSection

@Composable
fun MyInvitationContentsScreen(
    uiState: MyInvitationDetailUiState,
    onClickImage: (Int) -> Unit,
    onClickEditCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NachoTheme.colorScheme.backgroundTertiary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
            ) {
                CircularProgressIndicator(
                    color = NachoTheme.colorScheme.brandPrimary
                )
                Text(
                    text = stringResource(R.string.txt_loading_invitation),
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary
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
            Modifier
                .fillMaxSize()
                .background(NachoTheme.colorScheme.backgroundTertiary),
    ) {
        item {
            ImageSection(
                imageUrls = model.imageList,
                onImageClick = onClickImage,
                modifier = modifier,
            )
        }
        item {
            TitleSection(
                title = model.title,
                modifier = modifier,
            )
        }
        item {
            AuthorSection(
                profileUrl = model.hostInfo.profileUrl,
                author = model.hostInfo.name,
                modifier = modifier,
            )
        }
        item {
            DateSection(
                date = model.dateTime.date,
                startTime = model.dateTime.startTime,
                modifier = modifier,
            )
        }
        item {
            AddressSection(
                placeName = model.location.name,
                placeAddress = model.location.address,
                modifier = modifier,
            )
        }
        item {
            model.invitationCard?.let { card ->
                InvitationCardSection(
                    invitationCardUiModel = card,
                    isEditable = true,
                    onEditClick = onClickEditCard,
                    modifier = modifier,
                )
            }
        }
        item {
            AnnouncementSection(
                announcements = model.announcement,
                modifier = modifier,
            )
        }
        item {
            PlaceGuideSection(
                location = model.location,
                modifier = modifier,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun MyInvitationContentsScreenPreview() {
    NachoTheme {
        MyInvitationContentsScreen(
            uiState = MyInvitationDetailUiState(
                isLoading = false
            ),
            onClickImage = {},
            onClickEditCard = {},
        )
    }
}
