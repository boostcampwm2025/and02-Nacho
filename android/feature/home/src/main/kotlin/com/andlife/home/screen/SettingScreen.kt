package com.andlife.home.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.andlife.designsystem.component.NachoDivider
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.IconOnSecondary
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.home.R
import com.andlife.designsystem.R as designR

@Composable
fun SettingRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingScreen(
        onBack = onNavigateBack,
        modifier = modifier,
    )
}

@Composable
fun SettingScreen(
    onBack: () -> Unit,
    onClickImage: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {},
    onClickService: () -> Unit = {},
    onClickPrivacy: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingTopBar(
                onBack = onBack,
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(vertical = NachoSpacing.twoXLarge, horizontal = NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
        ) {

            SettingSection { modifier ->
                ProfileContent(
                    name = "안드라이프",
                    profileUrl = "",
                    onClickImage = onClickImage,
                    onClickEdit = onClickEdit,
                    modifier = modifier
                )
            }

            SettingSection(headerTitle = "알림") { modifier ->
                NotificationContent(
                    isNotificationEnabled = true,
                    onCheckedChange = onCheckedChange,
                    modifier = modifier
                )
            }

            SettingSection(headerTitle = "약관 및 정책") { modifier ->
                PolicyContent(
                    onClickService = onClickService,
                    onClickPrivacy = onClickPrivacy,
                    modifier = modifier
                )
            }

            SettingSection(
                headerTitle = "앱 정보",
                showDivider = false
            ) {
                // TODO: AppInfoContent()
            }

            SettingSection(
                headerTitle = "계정",
                showDivider = false
            ) {
                // TODO: AccountContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = stringResource(R.string.txt_title_setting),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(designR.drawable.ic_arrow_back_24),
                        contentDescription = stringResource(R.string.desc_top_bar_back),
                        tint = NachoTheme.colorScheme.iconSecondary,
                    )
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = NachoTheme.colorScheme.backgroundPrimary,
                ),
        )

        NachoDivider(
            color = NachoTheme.colorScheme.backgroundSecondary,
            horizontalPadding = NachoSpacing.none,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SettingSection(
    headerTitle: String? = null,
    showDivider: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        headerTitle?.let {
            Text(
                text = headerTitle,
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.small),
            )
        }

        val contentModifier = Modifier.padding(
            vertical = NachoSpacing.medium,
            horizontal = NachoSpacing.medium,
        )
        content(contentModifier)

        if (showDivider) {
            NachoDivider(
                color = NachoTheme.colorScheme.backgroundSecondary,
                horizontalPadding = NachoSpacing.none,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NachoSpacing.small)
            )
        }
    }
}

@Composable
private fun ProfileContent(
    name: String,
    profileUrl: String,
    onClickImage: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(80.dp),
        ) {
            AsyncImage(
                model = profileUrl,
                contentDescription = stringResource(R.string.desc_profile_image),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .clickable(onClick = onClickImage),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(designR.drawable.ic_person_24),
                error = painterResource(designR.drawable.ic_person_24),
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .fillMaxSize(0.35f),
                shape = CircleShape,
                color = NachoTheme.colorScheme.backgroundPrimary,
                border = BorderStroke(NachoStroke.small, NachoTheme.colorScheme.backgroundPrimary),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera_24),
                    contentDescription = stringResource(R.string.desc_profile_image_icon),
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = name,
                    onValueChange = {},
                    enabled = true,
                    readOnly = false,
                    modifier = Modifier.weight(1f),
                    textStyle = NachoTheme.typography.headingSmallSemiBold,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = NachoTheme.colorScheme.brandPrimary,
                        selectionColors = TextSelectionColors(
                            handleColor = NachoTheme.colorScheme.brandPrimary,
                            backgroundColor = NachoTheme.colorScheme.brandPrimary.copy(alpha = 0.4f)
                        )
                    ),
                )
                IconButton(
                    onClick = onClickEdit,
                    modifier = Modifier.size(NachoIconSize.xLarge),
                ) {
                    Icon(
                        painter = painterResource(designR.drawable.ic_edit_24),
                        contentDescription = stringResource(R.string.desc_profile_name_icon),
                        tint = Color.Unspecified,
                    )
                }
            }

            NachoDivider(
                color = NachoTheme.colorScheme.backgroundSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NotificationContent(
    isNotificationEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "푸시 알림",
            style = NachoTheme.typography.bodyLargeRegular,
            color = NachoTheme.colorScheme.textPrimary,
        )
        Switch(
            checked = isNotificationEnabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NachoTheme.colorScheme.brandOnPrimary,
                checkedTrackColor = NachoTheme.colorScheme.brandPrimary
            )
        )
    }
}

@Composable
private fun PolicyContent(
    onClickService: () -> Unit,
    onClickPrivacy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "서비스 이용약관",
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textPrimary,
            )
            IconButton(
                onClick = onClickService,
                modifier = Modifier.size(NachoIconSize.medium),
            ) {
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_policy_service),
                    tint = NachoTheme.colorScheme.iconOnSecondary,
                )
            }
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "개인정보 처리방침",
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textPrimary,
            )
            IconButton(
                onClick = onClickPrivacy,
                modifier = Modifier.size(NachoIconSize.medium),
            ) {
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_policy_privacy),
                    tint = NachoTheme.colorScheme.iconOnSecondary,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun SettingScreenPreview() {
    NachoTheme {
        SettingScreen(
            onBack = {},
        )
    }
}
