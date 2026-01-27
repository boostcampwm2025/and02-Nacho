package com.andlife.invitation_edit.section

import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.model.create.CardUiModel

@Composable
fun CardSection(
    cardUiModel: CardUiModel?,
    onClickCreatedCard: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isPreviewMode: Boolean = false,
) {
    val defaultColor = NachoTheme.colorScheme.textPrimary
    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.txt_card),
                    style = NachoTheme.typography.bodyMediumSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!isPreviewMode) {
                    TextButton(
                        onClick = onClickCreatedCard,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = if (cardUiModel == null) Icons.Default.Add else Icons.Default.Edit,
                            contentDescription = if (cardUiModel == null) stringResource(R.string.desc_create_card)
                            else stringResource(R.string.desc_update_card),
                            tint = NachoTheme.colorScheme.brandPrimary,
                        )
                        Text(
                            text = if (cardUiModel == null) stringResource(R.string.txt_create_card)
                            else stringResource(R.string.txt_update_card),
                            style = NachoTheme.typography.bodyMediumMedium,
                            color = NachoTheme.colorScheme.brandPrimary,
                        )
                    }
                }
            }
            if (isPreviewMode) {
                Spacer(modifier = Modifier.height(NachoSpacing.small))
            }
            val card = cardUiModel
            if (card != null) {
                Card(
                    modifier = modifier
                        .padding(horizontal = NachoSpacing.large)
                        .padding(bottom = NachoSpacing.large),
                    shape = NachoTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = Color(card.backgroundColor)
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
                            TextView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                setTextColor(defaultColor.toArgb())
                            }
                        },
                        update = { textView ->
                            textView.text = card.editable
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.txt_create_card_explain),
                        style = NachoTheme.typography.bodyMediumSemiBold,
                        color = NachoTheme.colorScheme.textTertiary,
                    )
                }
            }
        }
    }
}
