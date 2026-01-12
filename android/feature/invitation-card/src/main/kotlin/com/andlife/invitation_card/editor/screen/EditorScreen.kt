package com.andlife.invitation_card.editor.screen

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import com.andlife.designsystem.component.InvitationButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation_card.R
import com.andlife.invitation_card.editor.model.ColorPaletteMode
import com.andlife.invitation_card.editor.model.EditorDefaults
import com.andlife.invitation_card.editor.utils.contrastColor
import kotlinx.collections.immutable.ImmutableList

@Composable
fun EditorScreen(
    titleText: String,
    onBackClick: () -> Unit,
    onSaveChangesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var colorPaletteMode by remember { mutableStateOf<ColorPaletteMode?>(null) }
    val scope = rememberCoroutineScope()
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // todo: 이미지 삽입
            }
        }

    val colorForPalette = when (colorPaletteMode) {
        ColorPaletteMode.Text -> Color.Black // todo: State 연결
        ColorPaletteMode.Background -> Color.White // todo: State 연결
        null -> null // 팔레트가 닫혔을 때는 null
    }

    Scaffold(
        topBar = {
            EditTopBar(
                titleText = titleText,
                onBackClick = onBackClick,
                onSaveChangesClick = onSaveChangesClick,
            )
        },
        bottomBar = {
            EditorBottomBar(
                colorForPalette = colorForPalette,
                onColorChange = { newColor ->
                    when (colorPaletteMode) {
                        ColorPaletteMode.Text -> {} // todo: State 연결
                        ColorPaletteMode.Background -> {} // todo: State 연결
                        null -> {}
                    }
                },
                onRedoClick = { },
                onUndoClick = { },
                colorPaletteMode = colorPaletteMode,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .background(InvitationTheme.colorScheme.backgroundTertiary)
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            HorizontalDivider()
            EditorToolbar(
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(InvitationSpacing.large),
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
) {
    TopAppBar(
        title = {
            Text(
                text = titleText,
                style = InvitationTheme.typography.headingSmallBold,
                color = InvitationTheme.colorScheme.textPrimary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        actions = {
            InvitationButton(
                onClick = onSaveChangesClick,
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = InvitationTheme.typography.bodyLargeSemiBold,
                    color = InvitationTheme.colorScheme.textOnPrimary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = InvitationTheme.colorScheme.backgroundPrimary,
        ),
        modifier = modifier,
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
                .background(InvitationTheme.colorScheme.backgroundPrimary),
    ) {
        AnimatedVisibility(colorPaletteMode != null) {
            ColorPalette(
                modifier = Modifier.fillMaxWidth(),
                colorList = EditorDefaults.palette,
                onColorChange = onColorChange,
                currentColor = colorForPalette,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(InvitationSpacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.clickable { onUndoClick() },
                horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                    contentDescription = stringResource(R.string.undo),
                    tint = InvitationTheme.colorScheme.textSecondary
                )
                Text(text = stringResource(R.string.undo))
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.clickable { onRedoClick() },
                horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.redo))
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_redo),
                    contentDescription = stringResource(R.string.redo),
                    tint = InvitationTheme.colorScheme.textSecondary
                )
            }
        }
    }
}

@Composable
private fun EditorToolbar(
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
                    .background(InvitationTheme.colorScheme.backgroundPrimary)
                    .padding(vertical = InvitationSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
        ) {
            TextSizeBox(
                currentSize = 16f, // todo: State 연결
                onSizeChange = { },
                modifier = Modifier.padding(start = InvitationSpacing.small),
            )

            VerticalDivider()

            ColorToggleButton(
                icon = Icons.Default.TextFields,
                selectedColor = Color.Black, // todo: State 연결
                onClick = onClickTextColor,
                color = if (currentPaletteMode == ColorPaletteMode.Text) {
                    InvitationTheme.colorScheme.brandPrimary
                } else {
                    InvitationTheme.colorScheme.iconOnSecondary
                }
            )

            ColorToggleButton(
                icon = Icons.Default.Brush,
                selectedColor = Color.White, // todo: State 연결
                onClick = onClickBackgroundColor,
                color = if (currentPaletteMode == ColorPaletteMode.Background) {
                    InvitationTheme.colorScheme.brandPrimary
                } else {
                    InvitationTheme.colorScheme.iconOnSecondary
                }
            )

            VerticalDivider()

            StyleToggleButton(
                icon = Icons.Default.FormatBold,
                isActive = false, // todo: State 연결
                onClick = {}, // todo: State 연결
            )

            StyleToggleButton(
                icon = Icons.Default.FormatItalic,
                isActive = false, // todo: State 연결
                onClick = { }, // todo: State 연결
            )

            StyleToggleButton(
                icon = Icons.Default.FormatUnderlined,
                isActive = false, // todo: State 연결
                onClick = { }, // todo: State 연결
            )

            StyleToggleButton(
                icon = Icons.Default.FormatStrikethrough,
                isActive = false, // todo: State 연결
                onClick = { }, // todo: State 연결
            )

            VerticalDivider()

            StyleToggleButton(
                icon = Icons.AutoMirrored.Default.FormatAlignLeft,
                isActive = false, // todo: State 연결
                onClick = { }, // todo: State 연결
            )

            StyleToggleButton(
                icon = Icons.Default.FormatAlignCenter,
                isActive = false, // todo: State 연결
                onClick = { }, // todo: State 연결
            )

            StyleToggleButton(
                icon = Icons.AutoMirrored.Default.FormatAlignRight,
                isActive = false, // todo: State 연결
                onClick = { }, // todo: State 연결
            )

            VerticalDivider()

            StyleToggleButton(
                icon = Icons.Default.Photo,
                isActive = false,
                onClick = onClickAddImage,
            )

            StyleToggleButton(
                modifier = Modifier.padding(end = InvitationSpacing.small),
                icon = Icons.Default.Celebration,
                isActive = false,
                onClick = { }, // todo: State 연결
            )
        }
    }
}

@Composable
private fun EditCard(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = modifier,
        shape = InvitationTheme.shapes.medium,
        color = InvitationTheme.colorScheme.backgroundPrimary,
        border = BorderStroke(1.dp, InvitationTheme.colorScheme.backgroundBorder),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(InvitationSpacing.large),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    AppCompatEditText(context).apply {
                        isFocusable = true
                        isFocusableInTouchMode = true
                        // todo: State 연결
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
    color: Color = InvitationTheme.colorScheme.iconOnSecondary,
    contentDescription: String? = null,
) {
    val borderDp = if (selectedColor == Color.White) 1.dp else 0.dp
    Column(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            shape = InvitationTheme.shapes.large,
            color = selectedColor,
            border = BorderStroke(borderDp, InvitationTheme.colorScheme.backgroundBorder),
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
            InvitationTheme.colorScheme.brandPrimary
        } else {
            InvitationTheme.colorScheme.iconOnSecondary
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
                    vertical = InvitationSpacing.xSmall,
                    horizontal = InvitationSpacing.small
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
                .border(2.dp, Color.Gray.copy(alpha = 0.3f), shape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.selected_color),
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
    shape: Shape = InvitationTheme.shapes.small,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        shape = shape,
        onClick = { expanded = !expanded },
        color = InvitationTheme.colorScheme.backgroundPrimary,
        border = BorderStroke(1.dp, InvitationTheme.colorScheme.backgroundBorder),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = InvitationSpacing.medium,
                vertical = InvitationSpacing.small
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall),
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
                containerColor = InvitationTheme.colorScheme.backgroundPrimary
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
    InvitationTheme {
        EditorScreen(
            titleText = "초대카드 생성",
            onBackClick = {},
            onSaveChangesClick = {},
        )
    }
}
