package com.andlife.setting.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.NachoDivider
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.auth.AuthState
import com.andlife.login.LocalLoginManager
import com.andlife.login.social.SocialType
import com.andlife.setting.R
import com.andlife.setting.model.NicknameError
import com.andlife.setting.model.SettingSideEffect
import com.andlife.setting.model.SettingUiEvent
import com.andlife.setting.model.SettingUiState
import com.andlife.setting.viewmodel.SettingViewModel
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.getAppVersion
import kotlinx.coroutines.launch
import com.andlife.designsystem.R as designR

@Composable
fun SettingRoute(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isLogoutDialogVisible by remember { mutableStateOf(false) }
    var isSignedOutDialogVisible by remember { mutableStateOf(false) }
    var isNicknameDialogVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val loginManager = LocalLoginManager.current
    val res = LocalResources.current

    SettingScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateToLogin = onNavigateToLogin,
        onEvent = viewModel::onEvent,
        modifier = modifier,
        onClickEditNickname = { isNicknameDialogVisible = true },
        onClickEditImage = {},
        onClickQuit = { isSignedOutDialogVisible = true },
        onClickLogout = { isLogoutDialogVisible = true }
    )

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            SettingSideEffect.NavigateToLogin -> {
                isLogoutDialogVisible = false
                onLogout()
            }

            SettingSideEffect.PopBackStack -> {
                onNavigateBack()
            }

            SettingSideEffect.FailSignOut -> {
                scope.launch {
                    snackbarHostState.showSnackbar(res.getString(R.string.fail_sign_out))
                }
            }

            SettingSideEffect.SuccessSignOutWithServer -> {
                scope.launch {
                    val result = loginManager.logout(SocialType.KAKAO)
                    if (result) {
                        onSignedOut()
                    } else {
                        snackbarHostState.showSnackbar(res.getString(R.string.fail_sign_out))
                    }
                }
            }
        }
    }

    if (isNicknameDialogVisible) {
        NachoDialog(onDismiss = { isNicknameDialogVisible = false }) {
            EditNicknameDialogContent(
                uiState = uiState,
                onNicknameChanged = { viewModel.onEvent(SettingUiEvent.OnNicknameChanged(it)) },
                onConfirm = {
                    viewModel.onEvent(SettingUiEvent.ClickConfirmNickname(uiState.nicknameInput))
                    isNicknameDialogVisible = false
                },
                onDismiss = { isNicknameDialogVisible = false }
            )
        }
    }

    if (isLogoutDialogVisible) {
        NachoDialog(
            onDismiss = { isLogoutDialogVisible = false }
        ) {
            LogoutDialogContent(
                onDismiss = { isLogoutDialogVisible = false },
                onConfirm = { viewModel.onEvent(SettingUiEvent.ClickLogout) },
                modifier = Modifier
            )
        }
    }

    if (isSignedOutDialogVisible) {
        NachoDialog(onDismiss = { isSignedOutDialogVisible = false }) {
            SignedOutDialogContent(
                onDismiss = { isSignedOutDialogVisible = false },
                onConfirm = {
                    viewModel.onEvent(SettingUiEvent.ClickSignOut)
                    isSignedOutDialogVisible = false
                }
            )
        }
    }
}

@Composable
fun SettingScreen(
    uiState: SettingUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateToLogin: () -> Unit,
    onClickEditNickname: () -> Unit,
    onClickEditImage: () -> Unit,
    onClickLogout: () -> Unit,
    onClickQuit: () -> Unit,
    onEvent: (SettingUiEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingTopBar(
                onBack = { onEvent(SettingUiEvent.ClickBack) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->

        val scrollState = rememberScrollState()
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(vertical = NachoSpacing.twoXLarge),
                    verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                ) {

                    var nameState by remember { mutableStateOf("안드라이프") } // TODO: 임시

                    SettingSection {
                        ProfileContent(
                            authState = uiState.authState,
                            onNavigateToLogin = onNavigateToLogin,
                            onClickImage = onClickEditImage,     // 카메라/프로필 클릭 시
                            onClickEdit = onClickEditNickname,   // 연필 아이콘 클릭 시
                            modifier = Modifier.padding(
                                vertical = NachoSpacing.medium,
                                horizontal = NachoSpacing.large
                            ),
                        )
                    }

                    var checked by remember { mutableStateOf(true) } // TODO: 임시

                    SettingSection(headerTitle = stringResource(R.string.txt_header_notification)) { modifier ->
                        NotificationContent(
                            isNotificationEnabled = checked,
                            onCheckedChange = { checked = it },
                            modifier = modifier,
                        )
                    }

                    SettingSection(headerTitle = stringResource(R.string.txt_header_policy)) { modifier ->
                        PolicyContent(
                            onClickService = {},
                            onClickPrivacy = {},
                            modifier = modifier,
                        )
                    }

                    SettingSection(headerTitle = stringResource(R.string.txt_header_app_info)) { modifier ->
                        AppInfoContent(
                            appVersion = context.getAppVersion(),
                            onClickInfo = {},
                            modifier = modifier,
                        )
                    }

                    SettingSection(
                        headerTitle = stringResource(R.string.txt_header_account),
                        showDivider = false
                    ) { modifier ->
                        AccountContent(
                            authState = uiState.authState,
                            onClickLogout = onClickLogout,
                            onClickQuit = onClickQuit,
                            modifier = modifier,
                        )
                    }
                }
                if (uiState.isOverlayLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
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
    modifier: Modifier = Modifier,
    headerTitle: String? = null,
    showDivider: Boolean = true,
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
                    .padding(vertical = NachoSpacing.small, horizontal = NachoSpacing.large),
            )
        }

        val contentModifier = Modifier.padding(
            vertical = NachoSpacing.medium,
        )
        content(contentModifier)

        if (showDivider) {
            NachoDivider(
                color = NachoTheme.colorScheme.backgroundSecondary,
                horizontalPadding = NachoSpacing.none,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = NachoSpacing.medium)
            )
        }
    }
}

@Composable
private fun ProfileContent(
    authState: AuthState,
    onNavigateToLogin: () -> Unit,
    onClickImage: () -> Unit,
    onClickEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (authState) {
        is AuthState.Authenticated -> {
            val user = authState.user
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(onClick = onClickImage),
                ) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentDescription = stringResource(R.string.desc_profile_image),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
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
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera_24),
                            contentDescription = stringResource(R.string.desc_profile_camera),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(NachoSpacing.twoXSmall),
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
                        Text(
                            text = user.name,
                            style = NachoTheme.typography.headingSmallSemiBold,
                            color = NachoTheme.colorScheme.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onClickEdit,
                            modifier = Modifier.size(NachoIconSize.xLarge),
                        ) {
                            Icon(
                                painter = painterResource(designR.drawable.ic_edit_24),
                                contentDescription = stringResource(R.string.desc_profile_edit),
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

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.small)
            ) {
                Text(
                    text = stringResource(R.string.txt_not_login_user),
                    style = NachoTheme.typography.bodyLargeMedium,
                    color = NachoTheme.colorScheme.textSecondary,
                )

                NachoButton(
                    onClick = onNavigateToLogin,
                    elevation =
                        ButtonDefaults.buttonElevation(
                            defaultElevation = NachoElevation.none,
                            pressedElevation = NachoElevation.none,
                        ),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    contentPadding = PaddingValues(NachoSpacing.small),
                ) {
                    Text(
                        text = stringResource(R.string.txt_go_login),
                        color = NachoTheme.colorScheme.brandPrimary
                    )
                }
            }
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.txt_push_notification),
            style = NachoTheme.typography.bodyLargeRegular,
            color = NachoTheme.colorScheme.textPrimary,
        )
        Switch(
            checked = isNotificationEnabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NachoTheme.colorScheme.brandOnPrimary,
                checkedTrackColor = NachoTheme.colorScheme.brandPrimary,
                uncheckedThumbColor = NachoTheme.colorScheme.brandOnPrimary,
                uncheckedTrackColor = NachoTheme.colorScheme.backgroundBorder,
                uncheckedBorderColor = Color.Transparent,
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.txt_policy_service),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.txt_policy_privacy),
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


@Composable
private fun AppInfoContent(
    appVersion: String,
    onClickInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.txt_app_notice),
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textPrimary,
            )
            IconButton(
                onClick = onClickInfo,
                modifier = Modifier.size(NachoIconSize.medium),
            ) {
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_app_notice),
                    tint = NachoTheme.colorScheme.iconOnSecondary,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.txt_app_version),
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textPrimary,
            )
            Text(
                text = appVersion,
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textPrimary,
            )
        }
    }
}

@Composable
private fun AccountContent(
    authState: AuthState,
    onClickLogout: () -> Unit,
    onClickQuit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoggedIn = authState is AuthState.Authenticated
    val textColor = if (isLoggedIn) {
        NachoTheme.colorScheme.textPrimary
    } else {
        NachoTheme.colorScheme.textTertiary
    }
    val iconTint = if (isLoggedIn) {
        NachoTheme.colorScheme.iconOnSecondary
    } else {
        NachoTheme.colorScheme.iconDisabled
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClickLogout,
                    enabled = authState is AuthState.Authenticated
                )
                .padding(horizontal = NachoSpacing.large, vertical = NachoSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.txt_logout),
                style = NachoTheme.typography.bodyLargeRegular,
                color = textColor,
            )
            IconButton(
                onClick = onClickLogout,
                modifier = Modifier.size(NachoIconSize.medium),
                enabled = isLoggedIn,
            ) {
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_logout),
                    tint = iconTint,
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.medium)
                .clickable(
                    onClick = onClickQuit,
                    enabled = isLoggedIn
                )
                .padding(horizontal = NachoSpacing.large, vertical = NachoSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.txt_quit),
                style = NachoTheme.typography.bodyLargeRegular,
                color = NachoTheme.colorScheme.textTertiary,
            )
            IconButton(
                onClick = onClickQuit,
                modifier = Modifier.size(NachoIconSize.medium),
            ) {
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_quit),
                    tint = NachoTheme.colorScheme.iconDisabled,
                )
            }
        }
    }
}

@Composable
fun EditNicknameDialogContent(
    uiState: SettingUiState,
    onNicknameChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val errorMessage = when (uiState.nicknameError) {
        NicknameError.EMPTY -> stringResource(R.string.msg_nickname_empty)
        NicknameError.TOO_LONG -> stringResource(R.string.msg_nickname_too_long)
        NicknameError.INVALID_CHAR -> stringResource(R.string.msg_nickname_invalid_char)
        NicknameError.NONE -> ""
    }

    Column(modifier = Modifier.padding(NachoSpacing.large)) {
        Text(
            text = stringResource(R.string.txt_nickname_edit_title),
            style = NachoTheme.typography.headingSmallSemiBold,
        )

        Spacer(modifier = Modifier.height(NachoSpacing.medium))

        OutlinedTextField(
            value = uiState.nicknameInput,
            onValueChange = onNicknameChanged,
            isError = uiState.nicknameError != NicknameError.NONE,
            supportingText = {
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = NachoTheme.colorScheme.brandPrimary)
                }
            },
            modifier = Modifier.fillMaxWidth(),
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onDismiss() },
                shape = NachoTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.txt_cancel),
                    color = NachoTheme.colorScheme.textSecondary
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))
            TextButton(
                onClick = { onConfirm() },
                shape = NachoTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.txt_confirm),
                    color = NachoTheme.colorScheme.brandPrimary
                )
            }
        }
    }
}

@Composable
fun ProfileImageActionDialog(
    onTakePhoto: () -> Unit,
    onPickAlbum: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(NachoSpacing.medium)) {
        TextItem("카메라 촬영", onClick = { onTakePhoto(); onDismiss() })
        TextItem("앨범에서 사진 선택", onClick = { onPickAlbum(); onDismiss() })
        TextItem("취소", onClick = onDismiss, color = NachoTheme.colorScheme.textSecondary)
    }
}

@Composable
private fun TextItem(text: String, onClick: () -> Unit, color: Color = Color.Unspecified) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(NachoSpacing.medium),
        style = NachoTheme.typography.bodyLargeMedium,
        color = color
    )
}

@Composable
private fun LogoutDialogContent(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NachoSpacing.xLarge)
    ) {
        Text(
            text = stringResource(R.string.txt_logout),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary
        )

        Text(
            modifier = Modifier.padding(top = NachoSpacing.medium),
            text = stringResource(R.string.txt_question_logout),
            color = NachoTheme.colorScheme.textSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onDismiss() },
                shape = NachoTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.txt_cancel),
                    color = NachoTheme.colorScheme.textSecondary
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))
            TextButton(
                onClick = { onConfirm() },
                shape = NachoTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.txt_confirm),
                    color = NachoTheme.colorScheme.brandPrimary
                )
            }
        }
    }
}

@Composable
private fun SignedOutDialogContent(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NachoSpacing.xLarge)
    ) {
        Text(
            text = stringResource(R.string.txt_quit),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary
        )

        Text(
            modifier = Modifier.padding(top = NachoSpacing.medium),
            text = stringResource(R.string.txt_sign_out),
            color = NachoTheme.colorScheme.textPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onDismiss() },
                shape = NachoTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.txt_cancel),
                    color = NachoTheme.colorScheme.textSecondary
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = NachoSpacing.small))
            TextButton(
                onClick = { onConfirm() },
                shape = NachoTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.txt_confirm),
                    color = NachoTheme.colorScheme.brandPrimary
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
            uiState = SettingUiState(isLoading = false),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToLogin = {},
            onClickEditNickname = {},
            onClickEditImage = {},
            onClickQuit = {},
            onClickLogout = {}
        )
    }
}
