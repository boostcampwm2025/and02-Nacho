package com.andlife.invitation_edit.screen.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.DeleteDialogContent
import com.andlife.invitation_edit.model.AddressUiModel
import com.andlife.invitation_edit.model.create.AnnouncementUiModel
import com.andlife.invitation_edit.model.create.CreateInvitationSideEffect
import com.andlife.invitation_edit.model.create.CreateInvitationUiEvent
import com.andlife.invitation_edit.model.create.CreateInvitationUiState
import com.andlife.invitation_edit.section.AddressSection
import com.andlife.invitation_edit.section.AuthorSection
import com.andlife.invitation_edit.section.BottomBarSection
import com.andlife.invitation_edit.section.DateSection
import com.andlife.invitation_edit.section.ImageSection
import com.andlife.invitation_edit.section.TimeSection
import com.andlife.invitation_edit.section.TitleSection
import com.andlife.invitation_edit.section.TopBarSection
import com.andlife.invitation_edit.section.announcementSection
import com.andlife.invitation_edit.viewmodel.CreateInvitationViewModel
import com.andlife.ui.component.InvitationDatePickerBottomSheet
import com.andlife.ui.component.InvitationTimePickerBottomSheet
import com.andlife.ui.component.addannouncement.InvitationAddAnnouncementBottomSheet
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun MyInvitationCreateRoute(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    address: AddressUiModel? = null,
    onNavigateToEditor: () -> Unit,
    viewModel: CreateInvitationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalResources.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isShowDatePicker by remember { mutableStateOf(false) }
    var isShowStartTimePicker by remember { mutableStateOf(false) }
    var isShowEndTimePicker by remember { mutableStateOf(false) }
    var isShowAnnouncementSheet by remember { mutableStateOf(false) }
    var isShowDeleteAnnouncement by remember { mutableStateOf(false) }
    var selectedAnnouncement by remember { mutableStateOf<AnnouncementUiModel?>(null) }

    viewModel.effectFlow.collectWithLifecycle { event ->
        when (event) {
            CreateInvitationSideEffect.FullImage -> {
                snackbarHost.showSnackbar(message = context.getString(R.string.snack_full_image))
            }
        }
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
            if (uris.isNotEmpty()) {
                val imageList = uris.map { it.toString() }
                viewModel.onEvent(CreateInvitationUiEvent.UpdateImageList(imageList))
            } else {
                scope.launch {
                    snackbarHost.showSnackbar(message = context.getString(R.string.snack_load_error_image))
                }
            }
        }

    LaunchedEffect(address) {
        if (address != null) {
            viewModel.onEvent(CreateInvitationUiEvent.UpdateAddress(address))
        }
    }

    MyInvitationCreateScreen(
        uiState = uiState,
        snackbarHostState = snackbarHost,
        onEvent = viewModel::onEvent,
        onNavigateToAddressSearch = onNavigateToAddressSearch,
        onNavigateBack = onNavigateBack,
        onAddImageClick = {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onDateClick = {
            isShowDatePicker = true
        },
        onStartTimeClick = {
            isShowStartTimePicker = true
        },
        onEndTimeClick = {
            isShowEndTimePicker = true
        },
        onAddAnnouncementClick = {
            isShowAnnouncementSheet = true
        },
        onRemoveAnnouncementClick = {
            selectedAnnouncement = it
            isShowDeleteAnnouncement = true
        },
        onNavigateToEditor = onNavigateToEditor,
        modifier = modifier,
    )

    if (isShowDatePicker) {
        InvitationDatePickerBottomSheet(
            onConfirm = { viewModel.onEvent(CreateInvitationUiEvent.UpdateDate(it)) },
            onDismiss = { isShowDatePicker = false },
        )
    }

    if (isShowStartTimePicker) {
        InvitationTimePickerBottomSheet(
            onConfirm = { hour, min ->
                viewModel.onEvent(CreateInvitationUiEvent.UpdateStartTime(hour, min))
            },
            onDismissRequest = { isShowStartTimePicker = false },
            initialHour = uiState.createInvitationUiModel.startTime?.hour ?: 9,
            initialMinute = uiState.createInvitationUiModel.startTime?.min ?: 0,
        )
    }

    if (isShowEndTimePicker) {
        InvitationTimePickerBottomSheet(
            onConfirm = { hour, min ->
                viewModel.onEvent(CreateInvitationUiEvent.UpdateEndTime(hour, min))
            },
            onDismissRequest = { isShowEndTimePicker = false },
            initialHour = uiState.createInvitationUiModel.endTime?.hour ?: 9,
            initialMinute = uiState.createInvitationUiModel.endTime?.min ?: 0,
        )
    }

    if (isShowAnnouncementSheet) {
        InvitationAddAnnouncementBottomSheet(
            onConfirm = { title, content ->
                viewModel.onEvent(CreateInvitationUiEvent.UpdateAnnouncement(title, content))
            },
            onDismiss = { isShowAnnouncementSheet = false },
        )
    }

    if (isShowDeleteAnnouncement) {
        NachoDialog(onDismiss = { isShowDeleteAnnouncement = false }) {
            DeleteDialogContent(
                onConfirm = {
                    selectedAnnouncement?.let {
                        viewModel.onEvent(CreateInvitationUiEvent.RemoveAnnouncement(it))
                        selectedAnnouncement = null
                    }
                    isShowDeleteAnnouncement = false
                },
                onDismiss = { isShowDeleteAnnouncement = false },
            )
        }
    }
}

@Composable
private fun MyInvitationCreateScreen(
    uiState: CreateInvitationUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CreateInvitationUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onAddImageClick: () -> Unit,
    onDateClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onNavigateToAddressSearch: () -> Unit,
    onAddAnnouncementClick: () -> Unit,
    onNavigateToEditor: () -> Unit,
    onRemoveAnnouncementClick: (AnnouncementUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopBarSection(
                title = stringResource(R.string.txt_create),
                onBackClick = onNavigateBack,
                onPreviewClick = {},
            )
        },
        bottomBar = {
            BottomBarSection(
                title = stringResource(R.string.txt_create_button),
                onClick = {},
            )
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundTertiary),
        ) {
            item {
                TitleSection(
                    title = uiState.createInvitationUiModel.title,
                    onTitleChange = { onEvent(CreateInvitationUiEvent.UpdateTitle(it)) },
                )
            }

            item {
                AuthorSection(
                    authorName = uiState.createInvitationUiModel.author,
                    onAuthorNameChange = { onEvent(CreateInvitationUiEvent.UpdateAuthor(it)) },
                    modifier = Modifier.padding(top = NachoSpacing.medium),
                )
            }

            item {
                ImageSection(
                    imageList = uiState.createInvitationUiModel.imageList,
                    onAddImageClick = onAddImageClick,
                    onRemoveClick = { onEvent(CreateInvitationUiEvent.RemoveImage(it)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = NachoSpacing.medium),
                )
            }

            item {
                DateSection(
                    date = uiState.createInvitationUiModel.date,
                    onDateClick = onDateClick,
                    modifier = Modifier.padding(top = NachoSpacing.medium),
                )
            }

            item {
                TimeSection(
                    startTime = uiState.createInvitationUiModel.startTime,
                    endTime = uiState.createInvitationUiModel.endTime,
                    onStartTimeClick = onStartTimeClick,
                    onEndTimeClick = onEndTimeClick,
                    modifier = Modifier.padding(top = NachoSpacing.medium),
                )
            }

            item {
                AddressSection(
                    placeName = uiState.createInvitationUiModel.placeName,
                    placeAddress = uiState.createInvitationUiModel.placeAddress,
                    addressGuide = uiState.createInvitationUiModel.placeGuide,
                    onChangePlaceAddress = { onEvent(CreateInvitationUiEvent.UpdatePlaceAddress(it)) },
                    onChangeAddressGuide = { onEvent(CreateInvitationUiEvent.UpdateAddressGuide(it)) },
                    onNavigateToAddressSearch = onNavigateToAddressSearch,
                    modifier = Modifier.padding(top = NachoSpacing.medium),
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NachoTheme.colorScheme.backgroundPrimary)
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = "초대카드",
                        style = NachoTheme.typography.bodyMediumSemiBold,
                        color = NachoTheme.colorScheme.textPrimary,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        modifier = Modifier.padding(end = 16.dp),
                        onClick = onNavigateToEditor
                    ) {
                        Text(
                            text = "초대카드 작성",
                            style = NachoTheme.typography.bodyMediumSemiBold,
                            color = NachoTheme.colorScheme.brandPrimary,
                        )
                    }
                }
            }

            announcementSection(
                announcementList = uiState.createInvitationUiModel.announcement,
                onAddAnnouncementClick = onAddAnnouncementClick,
                onRemoveAnnouncementClick = onRemoveAnnouncementClick,
                modifier = Modifier.padding(top = NachoSpacing.medium),
            )
        }
    }
}
