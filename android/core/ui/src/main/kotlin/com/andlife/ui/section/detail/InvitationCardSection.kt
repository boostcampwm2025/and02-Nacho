package com.andlife.ui.section.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.invitation.InvitationCardUiModel
import com.andlife.ui.R

@Composable
fun InvitationCardSection(
    invitationCardUiModel: InvitationCardUiModel,
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    onEditClick: () -> Unit = {},
) {
    val isCardEmpty = invitationCardUiModel.contentJson.isEmpty()
    val iconRes = if (isCardEmpty) R.drawable.ic_add_24 else R.drawable.ic_edit_24
    val buttonText =
        if (isCardEmpty) {
            stringResource(R.string.txt_card_create)
        } else {
            stringResource(R.string.txt_card_edit)
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.xLarge, horizontal = NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
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

            if (isEditable) {
                NachoButton(
                    onClick = onEditClick,
                    elevation =
                        ButtonDefaults.buttonElevation(
                            defaultElevation = NachoElevation.none,
                            pressedElevation = NachoElevation.none,
                        ),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    contentPadding = PaddingValues(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall),
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(NachoIconSize.xSmall),
                    )
                    Spacer(Modifier.width(NachoSpacing.small))
                    Text(buttonText)
                }
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
            if (isCardEmpty) {
                EmptyCardGuide()
            } else {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(color = NachoTheme.colorScheme.backgroundPrimary)
                            .padding(vertical = NachoSpacing.xSmall)
                            .border(
                                width = NachoStroke.small,
                                color = NachoTheme.colorScheme.backgroundSecondary,
                                shape = NachoTheme.shapes.small,
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    // TODO: 초대카드 내용 어떻게 받을지
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(NachoSpacing.threeXLarge),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
                    ) {
                        Text(
                            text = invitationCardUiModel.contentJson,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCardGuide(modifier: Modifier = Modifier) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(NachoSpacing.threeXLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info_24),
            contentDescription = null,
            tint = NachoTheme.colorScheme.textTertiary,
        )
        Column(verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall)) {
            Text(
                text = stringResource(R.string.txt_card_empty_guide),
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textTertiary,
            )
            Text(
                text = stringResource(R.string.txt_card_empty_description),
                style = NachoTheme.typography.bodySmallRegular,
                color = NachoTheme.colorScheme.textTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationCardSectionPreview() {
    NachoTheme {
        Box(modifier = Modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.xLarge),
                modifier = Modifier.padding(NachoSpacing.medium),
            ) {
                InvitationCardSection(
                    invitationCardUiModel = InvitationCardUiModel(contentJson = "초대카드가 있는 경우에 해당 영역을 꾸미게 됩니다."),
                    onEditClick = {},
                )

                InvitationCardSection(
                    invitationCardUiModel = InvitationCardUiModel(contentJson = ""),
                    isEditable = true,
                    onEditClick = {},
                )

                InvitationCardSection(
                    invitationCardUiModel = InvitationCardUiModel(contentJson = "초대카드가 있는 경우에 해당 영역을 꾸미게 됩니다."),
                    isEditable = true,
                    onEditClick = {},
                )
            }
        }
    }
}
