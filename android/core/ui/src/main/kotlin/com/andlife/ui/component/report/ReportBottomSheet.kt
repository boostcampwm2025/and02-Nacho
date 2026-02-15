package com.andlife.ui.component.report

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.common.ReportReason
import com.andlife.ui.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBottomSheet(
    onSubmit: (ReportReason, String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }
    var description by remember { mutableStateOf("") }

    val isSubmitEnabled = when (selectedReason) {
        null -> false
        ReportReason.ETC -> description.isNotBlank()
        else -> true
    }

    val dismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    BackHandler { dismiss() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        dragHandle = {},
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large)
                    .padding(top = NachoSpacing.twoXLarge)
            ) {
                Text(
                    text = stringResource(R.string.txt_report),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_close_24),
                    contentDescription = stringResource(R.string.desc_close),
                    tint = NachoTheme.colorScheme.textPrimary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { dismiss() }
                )
            }
            Text(
                text = stringResource(R.string.txt_report_reason_title),
                style = NachoTheme.typography.bodyLargeSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
                modifier = Modifier.padding(horizontal = NachoSpacing.large)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                ReportReason.entries.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NachoSpacing.twoXSmall)
                            .clickable { selectedReason = reason },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
                    ) {
                        RadioButton(
                            selected = reason == selectedReason,
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NachoTheme.colorScheme.iconPrimary,
                                unselectedColor = NachoTheme.colorScheme.iconSecondary
                            )
                        )
                        Text(
                            text = stringResource(reason.stringResId),
                            style = NachoTheme.typography.bodyMediumRegular,
                            color = NachoTheme.colorScheme.textPrimary
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.txt_report_description_title),
                style = NachoTheme.typography.bodyLargeSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
                modifier = Modifier.padding(horizontal = NachoSpacing.large)
            )
            NachoTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 300) {
                        description = it
                    }
                },
                placeholder = stringResource(R.string.txt_report_description_hint),
                minLines = 5,
                maxLines = 5,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large)
            )
            NachoButton(
                onClick = {
                    onSubmit(selectedReason!!, description.takeIf { it.isNotBlank() })
                    dismiss()
                },
                enabled = isSubmitEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large),
                contentPadding = PaddingValues(vertical = NachoSpacing.medium),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = NachoElevation.none,
                    pressedElevation = NachoElevation.none
                )
            ) {
                Text(
                    text = stringResource(R.string.txt_report_submit),
                    style = NachoTheme.typography.bodyLargeSemiBold
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun ReportBottomSheetPreview() {
    NachoTheme {
        ReportBottomSheet(
            onSubmit = { _, _ -> },
            onDismiss = { }
        )
    }
}
