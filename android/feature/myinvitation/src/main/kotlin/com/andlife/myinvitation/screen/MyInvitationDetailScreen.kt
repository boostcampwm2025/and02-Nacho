package com.andlife.myinvitation.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.NachoDivider
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.invitation.DateTimeInfo
import com.andlife.model.invitation.HostInfo
import com.andlife.model.invitation.InvitationCardUiModel
import com.andlife.model.invitation.InvitationContentsUiModel
import com.andlife.model.invitation.LatLngUiModel
import com.andlife.model.invitation.LocationInfo
import com.andlife.model.invitation.TimeUiModel
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.detail.MyInvitationDetailSideEffect
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiEvent
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiState
import com.andlife.myinvitation.viewmodel.MyInvitationDetailViewModel
import com.andlife.ui.component.GenericTabRow
import com.andlife.ui.component.loading.InvitationLoadingError
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import com.andlife.designsystem.R as designR

@Composable
fun MyInvitationDetailRoute(
    onNavigateBack: () -> Unit,
    onNavigateToEditCard: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyInvitationDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val mapErrorMessage = stringResource(R.string.snack_load_error_map)

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            MyInvitationDetailSideEffect.NavigateBack -> {
                onNavigateBack()
            }

            is MyInvitationDetailSideEffect.NavigateToEditCard -> {
                onNavigateToEditCard(effect.myInvitationId)
            }

            MyInvitationDetailSideEffect.ShowMapErrorSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = mapErrorMessage
                    )
                }
            }
        }
    }

    MyInvitationDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun MyInvitationDetailScreen(
    uiState: MyInvitationDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (MyInvitationDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabTitles = stringArrayResource(R.array.txt_tap_title).toImmutableList()
    val coroutineScope = rememberCoroutineScope()
    var isMapVisible by remember { mutableStateOf(true) }

    val navigateBackWithMapCleanup: () -> Unit = {
        isMapVisible = false
        coroutineScope.launch {
            delay(50L)
            onEvent(MyInvitationDetailUiEvent.ClickBack)
        }
    }

    BackHandler(onBack = navigateBackWithMapCleanup)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            MyInvitationDetailTopBar(
                title = uiState.invitationContentsUiModel.title,
                hasThanksCard = uiState.hasThanksCard,
                showActions = !uiState.isLoading && !uiState.isError,
                onBack = navigateBackWithMapCleanup,
                onClickThanksCard = { onEvent(MyInvitationDetailUiEvent.ClickThanksCard) },
                onShare = { onEvent(MyInvitationDetailUiEvent.ClickShare) },
                onEdit = { onEvent(MyInvitationDetailUiEvent.ClickEdit) },
                onDelete = { onEvent(MyInvitationDetailUiEvent.ClickDelete) },
                onCreateThanksCard = { onEvent(MyInvitationDetailUiEvent.CreateThanksCard) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        if (uiState.isLoading) {
            InvitationLoadingIndicator(
                modifier = Modifier
                    .padding(paddingValues),
                text = stringResource(R.string.txt_loading_invitation),
            )
            return@Scaffold
        }

        if (uiState.isError) {
            InvitationLoadingError(
                onRetry = { onEvent(MyInvitationDetailUiEvent.RetryLoad) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            return@Scaffold
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            GenericTabRow(
                tabs = tabTitles,
                content =
                    { index ->
                        when (index) {
                            0 -> {
                                MyInvitationContentsScreen(
                                    uiState = uiState,
                                    onClickImage =
                                        { idx ->
                                            onEvent(
                                                MyInvitationDetailUiEvent.ClickImage(
                                                    uiState.invitationContentsUiModel.imageList,
                                                    idx,
                                                ),
                                            )
                                        },
                                    onClickEditCard = { onEvent(MyInvitationDetailUiEvent.ClickEditCard) },
                                    onMapError = { onEvent(MyInvitationDetailUiEvent.MapError) },
                                    isMapVisible = isMapVisible,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            1 -> {} // TODO: 방명록 조회 및 작성
                            2 -> {} // TODO: 미디어 모아보기
                        }
                    },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyInvitationDetailTopBar(
    title: String,
    onBack: () -> Unit,
    onClickThanksCard: () -> Unit,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateThanksCard: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    hasThanksCard: Boolean = false,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
        actions = {
            if (showActions) {
                if (hasThanksCard) {
                    IconButton(onClick = onClickThanksCard) {
                        Icon(
                            painter = painterResource(designR.drawable.ic_thankscard),
                            contentDescription = stringResource(R.string.desc_top_bar_thanks_card),
                            tint = Color.Unspecified,
                        )
                    }
                }
                IconButton(onClick = onShare) {
                    Icon(
                        painter = painterResource(R.drawable.ic_share_24),
                        contentDescription = stringResource(R.string.desc_top_bar_share),
                        tint = NachoTheme.colorScheme.iconSecondary,
                    )
                }

                InvitationMoreMenu(
                    hasThanksCard = hasThanksCard,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onCreateThanksCard = onCreateThanksCard,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
            ),
    )
}

@Composable
private fun InvitationMoreMenu(
    hasThanksCard: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateThanksCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { isMenuExpanded = true }) {
            Icon(
                painter = painterResource(designR.drawable.ic_more_vert_24),
                contentDescription = stringResource(R.string.desc_top_bar_more),
                tint = NachoTheme.colorScheme.iconSecondary,
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            modifier = Modifier.background(NachoTheme.colorScheme.backgroundPrimary),
            shape = RoundedCornerShape(NachoSpacing.medium),
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.txt_edit),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textPrimary,
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onEdit()
                },
            )

            NachoDivider()

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.txt_delete),
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textPrimary,
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onDelete()
                },
            )

            NachoDivider()

            DropdownMenuItem(
                text = {
                    Text(
                        text =
                            if (hasThanksCard) {
                                stringResource(R.string.txt_send_thanks_card)
                            } else {
                                stringResource(R.string.txt_create_thanks_card)
                            },
                        style = NachoTheme.typography.bodyMediumMedium,
                        color = NachoTheme.colorScheme.textPrimary,
                    )
                },
                onClick = {
                    isMenuExpanded = false
                    onCreateThanksCard()
                },
            )
        }
    }
}

@Composable
@PreviewTheme
private fun MyInvitationDetailScreenPreview() {
    NachoTheme {
        MyInvitationDetailScreen(
            uiState =
                MyInvitationDetailUiState(
                    isLoading = false,
                    hasThanksCard = true,
                    invitationContentsUiModel =
                        InvitationContentsUiModel(
                            title = "2026년 나초 개발 네트워킹 데이",
                            hostInfo =
                                HostInfo(
                                    name = "안드라이프",
                                    profileUrl = "https://picsum.photos/200",
                                ),
                            imageList = persistentListOf("https://picsum.photos/800/600?random=1"),
                            dateTime =
                                DateTimeInfo(
                                    date = LocalDate(2026, 1, 31),
                                    startTime = TimeUiModel(hour = 13, min = 0),
                                ),
                            location =
                                LocationInfo(
                                    name = "코드스쿼드",
                                    address = "서울시 강남구 테헤란로 521 3층",
                                    guide = "삼성역 5번 출구에서 도보 5분 거리입니다.",
                                    latLng =
                                        LatLngUiModel(
                                            latitude = 37.5111,
                                            longitude = 127.0601,
                                        ),
                                ),
                            invitationCard =
                                InvitationCardUiModel(
                                    contentJson = "안녕하세요! 2026년 새해를 맞아 개발자분들과 함께 지식을 나누는 자리를 마련했습니다.",
                                ),
                        ),
                ),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}
