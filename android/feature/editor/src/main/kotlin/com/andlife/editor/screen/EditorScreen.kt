package com.andlife.editor.screen

import android.text.Layout
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.PopupProperties
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.editor.R
import com.andlife.editor.model.ColorPaletteMode
import com.andlife.editor.model.EditorDefaults
import com.andlife.editor.util.contrastColor
import com.andlife.editor.state.EditorState
import com.andlife.invitation_card.editor.utils.ImageLoaderImpl
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(
    state: EditorState,
    titleText: String,
    onBackClick: () -> Unit,
    onSaveChangesClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isLoading: Boolean = false,
) {
    var colorPaletteMode by remember { mutableStateOf<ColorPaletteMode?>(null) }
    val scope = rememberCoroutineScope()
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                scope.launch {
                    state.insertImage(uri)
                }
            }
        }

    val colorForPalette = when (colorPaletteMode) {
        ColorPaletteMode.Text -> state.currentTextStyle.textColor
        ColorPaletteMode.Background -> state.currentTextStyle.backgroundColor
        null -> null
    }

    Scaffold(
        topBar = {
            EditTopBar(
                titleText = titleText,
                onBackClick = {
                    state.clearFocusAndHideKeyboard()
                    onBackClick()
                },
                onSaveChangesClick = {
                    state.restartInput()
                    state.clearFocusAndHideKeyboard()
                    onSaveChangesClick()
                },
                enabled = !state.isTextEmpty && !isLoading
            )
        },
        bottomBar = {
            EditorBottomBar(
                colorForPalette = colorForPalette,
                onColorChange = { newColor ->
                    when (colorPaletteMode) {
                        ColorPaletteMode.Text -> {
                            state.updateTextColor(newColor)
                        }

                        ColorPaletteMode.Background -> {
                            state.updateBackgroundColor(newColor)
                        }

                        null -> {}
                    }
                },
                onRedoClick = { },
                onUndoClick = { },
                colorPaletteMode = colorPaletteMode,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .background(NachoTheme.colorScheme.backgroundTertiary)
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            HorizontalDivider()
            EditorToolbar(
                state = state,
                currentPaletteMode = colorPaletteMode,
                onClickTextColor = {
                    colorPaletteMode = if (colorPaletteMode == ColorPaletteMode.Text) {
                        null
                    } else {
                        ColorPaletteMode.Text
                    }
                },
                onClickBackgroundColor = {
                    colorPaletteMode = if (colorPaletteMode == ColorPaletteMode.Background) {
                        null
                    } else {
                        ColorPaletteMode.Background
                    }
                },
                onClickAddImage = {
                    pickMedia.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            HorizontalDivider()
            EditCard(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(NachoSpacing.large),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTopBar(
    titleText: String,
    onBackClick: () -> Unit,
    onSaveChangesClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TopAppBar(
        title = {
            Text(
                text = titleText,
                style = NachoTheme.typography.headingSmallBold,
                color = NachoTheme.colorScheme.textPrimary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                )
            }
        },
        actions = {
            NachoButton(
                onClick = onSaveChangesClick,
                enabled = enabled,
            ) {
                Text(
                    text = stringResource(R.string.btn_save),
                    style = NachoTheme.typography.bodyLargeSemiBold,
                    color = NachoTheme.colorScheme.textOnPrimary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NachoTheme.colorScheme.backgroundPrimary,
        ),
        modifier = modifier.padding(end = NachoSpacing.medium),
    )
}

@Composable
private fun EditorBottomBar(
    colorForPalette: Color?,
    onColorChange: (Color) -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorPaletteMode: ColorPaletteMode? = null,
) {
    Column(
        modifier =
            modifier
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .background(NachoTheme.colorScheme.backgroundPrimary),
    ) {
        AnimatedVisibility(colorPaletteMode != null) {
            val colorList = when (colorPaletteMode) {
                ColorPaletteMode.Text -> EditorDefaults.textColorPalette
                ColorPaletteMode.Background -> EditorDefaults.backgroundColorPalette
                else -> persistentListOf()
            }
            ColorPalette(
                modifier = Modifier.fillMaxWidth(),
                colorList = colorList,
                onColorChange = onColorChange,
                currentColor = colorForPalette,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NachoSpacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.clickable { onUndoClick() },
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                    contentDescription = stringResource(R.string.btn_undo),
                    tint = NachoTheme.colorScheme.textSecondary
                )
                Text(text = stringResource(R.string.btn_undo))
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.clickable { onRedoClick() },
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.btn_redo))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_redo),
                    contentDescription = stringResource(R.string.btn_redo),
                    tint = NachoTheme.colorScheme.textSecondary
                )
            }
        }
    }
}

@Composable
private fun EditorToolbar(
    state: EditorState,
    currentPaletteMode: ColorPaletteMode?,
    onClickTextColor: () -> Unit,
    onClickBackgroundColor: () -> Unit,
    onClickAddImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Box {
        Row(
            modifier =
                modifier
                    .height(IntrinsicSize.Min)
                    .horizontalScroll(scrollState)
                    .background(NachoTheme.colorScheme.backgroundPrimary)
                    .padding(vertical = NachoSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
        ) {
            TextSizeBox(
                currentSize = state.currentTextStyle.fontSize,
                onSizeChange = state::updateFontSize,
                modifier = Modifier.padding(start = NachoSpacing.small),
            )

            VerticalDivider()

            ColorToggleButton(
                icon = Icons.Default.TextFields,
                selectedColor = state.currentTextStyle.textColor,
                onClick = onClickTextColor,
                color = if (currentPaletteMode == ColorPaletteMode.Text) {
                    NachoTheme.colorScheme.brandPrimary
                } else {
                    NachoTheme.colorScheme.iconOnSecondary
                }
            )

            ColorToggleButton(
                icon = Icons.Default.Brush,
                selectedColor = state.currentTextStyle.backgroundColor,
                onClick = onClickBackgroundColor,
                color = if (currentPaletteMode == ColorPaletteMode.Background) {
                    NachoTheme.colorScheme.brandPrimary
                } else {
                    NachoTheme.colorScheme.iconOnSecondary
                }
            )

            VerticalDivider()

            StyleToggleButton(
                icon = Icons.Default.FormatBold,
                isActive = state.currentTextStyle.isBold,
                onClick = state::toggleBold,
            )

            StyleToggleButton(
                icon = Icons.Default.FormatItalic,
                isActive = state.currentTextStyle.isItalic,
                onClick = state::toggleItalic
            )

            StyleToggleButton(
                icon = Icons.Default.FormatUnderlined,
                isActive = state.currentTextStyle.isUnderline,
                onClick = state::toggleUnderline
            )

            StyleToggleButton(
                icon = Icons.Default.FormatStrikethrough,
                isActive = state.currentTextStyle.isStrikethrough,
                onClick = state::toggleStrikethrough
            )

            VerticalDivider()

            StyleToggleButton(
                icon = Icons.AutoMirrored.Default.FormatAlignLeft,
                isActive = state.currentTextStyle.alignment == Layout.Alignment.ALIGN_NORMAL,
                onClick = state::alignLeft,
            )

            StyleToggleButton(
                icon = Icons.Default.FormatAlignCenter,
                isActive = state.currentTextStyle.alignment == Layout.Alignment.ALIGN_CENTER,
                onClick = state::alignCenter,
            )

            StyleToggleButton(
                icon = Icons.AutoMirrored.Default.FormatAlignRight,
                isActive = state.currentTextStyle.alignment == Layout.Alignment.ALIGN_OPPOSITE,
                onClick = state::alignRight
            )

            VerticalDivider()

            StyleToggleButton(
                icon = Icons.Default.Photo,
                isActive = false,
                onClick = onClickAddImage,
            )

            StyleToggleButton(
                modifier = Modifier.padding(end = NachoSpacing.small),
                icon = Icons.Default.Celebration,
                isActive = false,
                onClick = { }, // todo: State 연결
            )
        }
    }
}

@Composable
private fun EditCard(
    state: EditorState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            state.detach()
        }
    }

    Surface(
        modifier = modifier,
        shape = NachoTheme.shapes.medium,
        color = state.currentTextStyle.backgroundColor,
        border = BorderStroke(NachoStroke.small, NachoTheme.colorScheme.backgroundBorder),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(NachoSpacing.large),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    AppCompatEditText(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        minHeight = resources.displayMetrics.heightPixels
                        hint = context.getString(R.string.desc_edit_text_hint)
                        isFocusable = true
                        isFocusableInTouchMode = true
                        state.attach(this)
                    }
                },
            )
        }
    }
}

@Composable
private fun ColorToggleButton(
    icon: ImageVector,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NachoTheme.colorScheme.iconOnSecondary,
    contentDescription: String? = null,
) {
    val borderDp = if (selectedColor == Color.White) NachoStroke.small else NachoStroke.none
    Column(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(NachoSpacing.xSmall),
            shape = NachoTheme.shapes.large,
            color = selectedColor,
            border = BorderStroke(
                borderDp,
                NachoTheme.colorScheme.backgroundBorder
            ),
        ) { }
    }

}

@Composable
private fun StyleToggleButton(
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                onClick = onClick,
                role = Role.Button,
            ),
        imageVector = icon,
        contentDescription = contentDescription,
        tint = if (isActive) {
            NachoTheme.colorScheme.brandPrimary
        } else {
            NachoTheme.colorScheme.iconOnSecondary
        },
    )
}

@Composable
private fun ColorPalette(
    colorList: ImmutableList<Color>,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    currentColor: Color? = null,
) {
    val scrollState = rememberScrollState()
    Row(modifier = modifier.horizontalScroll(scrollState)) {
        colorList.forEach { color ->
            ColorBox(
                modifier = Modifier.padding(
                    vertical = NachoSpacing.xSmall,
                    horizontal = NachoSpacing.small
                ),
                color = color,
                selected = currentColor == color,
                onClick = { onColorChange(color) },
            )
        }
    }
}

@Composable
private fun ColorBox(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    size: Dp = 24.dp,
) {
    Box(
        modifier =
            modifier
                .size(size)
                .clip(shape)
                .background(color)
                .border(
                    NachoStroke.medium,
                    Color.Gray.copy(alpha = 0.3f),
                    shape
                )
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.desc_selected_color),
                tint = color.contrastColor(),
            )
        }
    }
}

@Composable
private fun TextSizeBox(
    currentSize: Float,
    onSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = NachoTheme.shapes.small,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        shape = shape,
        onClick = { expanded = !expanded },
        color = NachoTheme.colorScheme.backgroundPrimary,
        border = BorderStroke(
            NachoStroke.small,
            NachoTheme.colorScheme.backgroundBorder
        ),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = NachoSpacing.medium,
                vertical = NachoSpacing.small
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
        ) {
            Text(
                text = currentSize.toInt().toString(),
                modifier = Modifier.padding()
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false),
                containerColor = NachoTheme.colorScheme.backgroundPrimary
            ) {
                EditorDefaults.fontFamilies.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.toInt().toString()) },
                        onClick = {
                            onSizeChange(it)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
@PreviewTheme
private fun EditorScreenPreview() {
    NachoTheme {
        EditorScreen(
            state = EditorState(ImageLoaderImpl()),
            snackbarHostState = SnackbarHostState(),
            titleText = "초대카드 생성",
            onBackClick = {},
            onSaveChangesClick = {},
        )
    }
}

