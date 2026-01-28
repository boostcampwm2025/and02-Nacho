package com.andlife.myinvitation.screen

import android.text.Editable
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.editor.screen.NachoTextView
import com.andlife.model.invitation.InvitationCardUiModel
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiState
import com.andlife.ui.R
import com.andlife.ui.section.detail.AddressSection
import com.andlife.ui.section.detail.AnnouncementSection
import com.andlife.ui.section.detail.AuthorSection
import com.andlife.ui.section.detail.DateSection
import com.andlife.ui.section.detail.EmptyCardGuide
import com.andlife.ui.section.detail.ImageSection
import com.andlife.ui.section.detail.PlaceGuideSection
import com.andlife.ui.section.detail.TitleSection
import com.andlife.designsystem.R as designR

@Composable
fun MyInvitationContentsScreen(
    uiState: MyInvitationDetailUiState,
    onClickImage: (Int) -> Unit,
    onClickCreateCard: () -> Unit,
    onClickEditCard: () -> Unit,
    onMapError: () -> Unit,
    isMapVisible: Boolean,
    onSaveEditableCache: (Editable) -> Unit,
    modifier: Modifier = Modifier,
    editCardEnabled: Boolean = false,
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

        MyInvitationCardSection(
            invitationCardModel = model.invitationCard,
            cachedCardEditable = uiState.cachedCardEditable,
            onCreateCard = onClickCreateCard,
            onEditCard = onClickEditCard,
            onSaveEditableCache = onSaveEditableCache,
            editCardEnabled = editCardEnabled,
            modifier = Modifier
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

@Composable
private fun MyInvitationCardSection(
    invitationCardModel: InvitationCardUiModel?,
    cachedCardEditable: Editable?,
    onCreateCard: () -> Unit,
    onEditCard: () -> Unit,
    onSaveEditableCache: (Editable) -> Unit,
    modifier: Modifier = Modifier,
    editCardEnabled: Boolean = false,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.xLarge, horizontal = NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        if (invitationCardModel == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.txt_invitation_card_title),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                )
                NachoButton(
                    onClick = onCreateCard,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                    ),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    contentPadding = PaddingValues(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall),
                ) {
                    Icon(
                        painter = painterResource(designR.drawable.ic_add_24),
                        contentDescription = null,
                        tint = NachoTheme.colorScheme.brandPrimary,
                    )
                    Spacer(Modifier.width(NachoSpacing.small))
                    Text(
                        text = stringResource(R.string.txt_card_create),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.brandPrimary,
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = NachoTheme.shapes.small,
                colors =
                    CardDefaults.cardColors(
                        containerColor = NachoTheme.colorScheme.backgroundSecondary,
                    ),
            ) {
                EmptyCardGuide()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.txt_invitation_card_title),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                )
                NachoButton(
                    onClick = onEditCard,
                    enabled = editCardEnabled,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = NachoElevation.none,
                        pressedElevation = NachoElevation.none,
                    ),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    contentPadding = PaddingValues(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall),
                ) {
                    Icon(
                        painter = painterResource(designR.drawable.ic_edit_24),
                        contentDescription = null,
                        tint = NachoTheme.colorScheme.brandPrimary,
                    )
                    Spacer(Modifier.width(NachoSpacing.small))
                    Text(
                        text = stringResource(R.string.txt_card_edit),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.brandPrimary,
                    )
                }
            }
            val textPrimary = NachoTheme.colorScheme.textPrimary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = NachoTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = Color(invitationCardModel.card.backgroundColor),
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = NachoElevation.medium
                ),
                border = BorderStroke(NachoStroke.small, NachoTheme.colorScheme.backgroundBorder),
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NachoSpacing.large),
                    factory = { context ->
                        NachoTextView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            setTextColor(textPrimary.toArgb())
                            onEditableReady = { editable ->
                                onSaveEditableCache(editable)
                            }
                        }
                    },
                    update = { view ->
                        if (cachedCardEditable != null) {
                            view.bindWithCachedEditable(cachedCardEditable)
                        } else {
                            view.bind(invitationCardModel.card)
                        }
                    }
                )
            }
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
            onClickCreateCard = {},
            onMapError = {},
            isMapVisible = true,
            onSaveEditableCache = {},
        )
    }
}
