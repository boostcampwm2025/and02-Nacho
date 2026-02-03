package com.andlife.editor.component

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.editor.util.Effect
import com.andlife.editor.util.EffectDefaults
import com.andlife.ui.R
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectBottomSheet(
    modifier: Modifier = Modifier,
    selectedEffect: String? = null,
    effects: ImmutableList<Effect> = EffectDefaults.effects,
    onDismiss: () -> Unit = {},
    onConfirm: (String?) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val res = LocalResources.current
    var currentSelected by remember { mutableStateOf<String?>(selectedEffect) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = NachoTheme.colorScheme.backgroundPrimary
    ) {
        Column {
            LazyVerticalGrid(
                modifier = modifier
                    .padding(horizontal = NachoSpacing.large)
                    .padding(bottom = NachoSpacing.large),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
            ) {
                item {
                    val selected = currentSelected == null
                    val borderColor =
                        if (selected) NachoTheme.colorScheme.brandPrimary else NachoTheme.colorScheme.backgroundBorder
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .border(NachoStroke.small, borderColor, NachoTheme.shapes.medium)
                                .clip(NachoTheme.shapes.medium)
                                .clickable { currentSelected = null },
                        ) {
                        }
                        Text(stringResource(R.string.effect_none))
                    }
                }

                items(effects) { effect ->
                    Log.d("EffectBottomSheet", "EffectBottomSheet currentSelected: ${currentSelected}")
                    Log.d("EffectBottomSheet", "EffectBottomSheet res.get: ${res.getString(effect.nameResId)}")
                    val selected = currentSelected == res.getString(effect.nameResId)
                    Log.d("EffectBottomSheet", "selected: ${selected}")
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(effect.resId))
                    val borderColor =
                        if (selected) NachoTheme.colorScheme.brandPrimary else NachoTheme.colorScheme.backgroundBorder
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        isPlaying = selected,
                        iterations = LottieConstants.IterateForever,
                        restartOnPlay = true
                    )

                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .border(NachoStroke.small, borderColor, NachoTheme.shapes.medium)
                                .clip(NachoTheme.shapes.medium)
                                .clickable { currentSelected = res.getString(effect.nameResId) },
                        ) {
                            LottieAnimation(
                                composition = composition,
                                progress = { if (selected) progress else 0.5f },
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(stringResource(effect.nameResId))
                    }
                }
            }
            NachoButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = NachoSpacing.large)
                    .padding(bottom = NachoSpacing.large),
                contentPadding = PaddingValues(vertical = NachoSpacing.medium),
                onClick = {
                    onConfirm(currentSelected)
                    onDismiss()
                }) {
                Text(stringResource(R.string.btn_select))
            }
        }
    }
}

@Composable
@PreviewTheme
private fun EffectBottomSheetPreview() {
    NachoTheme {
        Column {
            EffectBottomSheet()
        }
    }
}
