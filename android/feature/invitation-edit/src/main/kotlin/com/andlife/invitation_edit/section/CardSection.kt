package com.andlife.invitation_edit.section

import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.model.form.CardUiModel
import com.andlife.designsystem.R as designR

@Composable
fun CardSection(
    cardUiModel: CardUiModel?,
    onClickCreatedCard: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val defaultColor = NachoTheme.colorScheme.textPrimary
    val iconRes = if (cardUiModel == null) designR.drawable.ic_add_24 else designR.drawable.ic_edit_24
    val buttonDescRes = if (cardUiModel == null) R.string.desc_create_card else R.string.desc_update_card
    val buttonText = if (cardUiModel == null) R.string.txt_create_card else R.string.txt_update_card

    Box(modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary)) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.txt_card),
                    style = NachoTheme.typography.bodyMediumSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                )
                NachoButton(
                    onClick = onClickCreatedCard,
                    enabled = !isLoading,
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
                        contentDescription = stringResource(buttonDescRes),
                        tint = NachoTheme.colorScheme.brandPrimary,
                    )
                    Spacer(Modifier.width(NachoSpacing.small))
                    Text(
                        text = stringResource(buttonText),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.brandPrimary,
                    )
                }
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
