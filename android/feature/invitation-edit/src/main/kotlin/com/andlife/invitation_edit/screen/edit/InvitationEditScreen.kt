package com.andlife.invitation_edit.screen.edit

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.DeleteDialogContent
import com.andlife.invitation_edit.model.address.AddressUiModel
import com.andlife.invitation_edit.model.form.AnnouncementUiModel
import com.andlife.invitation_edit.model.form.InvitationFormSideEffect
import com.andlife.invitation_edit.model.form.InvitationFormUiEvent
import com.andlife.invitation_edit.model.form.InvitationFormUiState
import com.andlife.invitation_edit.section.AddressSection
import com.andlife.invitation_edit.section.AuthorSection
import com.andlife.invitation_edit.section.BottomBarSection
import com.andlife.invitation_edit.section.CardSection
import com.andlife.invitation_edit.section.DateSection
import com.andlife.invitation_edit.section.ImageSection
import com.andlife.invitation_edit.section.TimeSection
import com.andlife.invitation_edit.section.TitleSection
import com.andlife.invitation_edit.section.TopBarSection
import com.andlife.invitation_edit.section.announcementSection
import com.andlife.invitation_edit.viewmodel.InvitationEditViewModel
import com.andlife.ui.component.InvitationDatePickerBottomSheet
import com.andlife.ui.component.InvitationTimePickerBottomSheet
import com.andlife.ui.component.addannouncement.InvitationAddAnnouncementBottomSheet
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun InvitationEditRoute(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    onSuccessSave: () -> Unit,
    modifier: Modifier = Modifier,
    address: AddressUiModel? = null,
    viewModel: InvitationEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val res = LocalResources.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isShowDatePicker by remember { mutableStateOf(false) }
    var isShowStartTimePicker by remember { mutableStateOf(false) }
    var isShowEndTimePicker by remember { mutableStateOf(false) }
    var isShowAnnouncementSheet by remember { mutableStateOf(false) }
    var isShowDeleteAnnouncement by remember { mutableStateOf(false) }
    var selectedAnnouncement by remember { mutableStateOf<AnnouncementUiModel?>(null) }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            InvitationFormSideEffect.FullImage -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_full_image))
                }
            }

            InvitationFormSideEffect.OnBack -> {
                onNavigateBack()
            }

            InvitationFormSideEffect.FailSave -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_fail_save))
                }
            }

            InvitationFormSideEffect.FailLoad -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_fail_load))
                }
            }

            is InvitationFormSideEffect.SuccessSave -> {
                onSuccessSave()
            }

            is InvitationFormSideEffect.InvalidTime -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_load_error_time))
                }
            }
        }
    }

    BackHandler {
        viewModel.onEvent(InvitationFormUiEvent.OnClickBack)
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
            if (uris.isNotEmpty()) {
                val imageList = uris.map { it.toString() }
                viewModel.onEvent(InvitationFormUiEvent.UpdateImageList(imageList))
            } else {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_load_error_image))
                }
            }
        }

    LaunchedEffect(address) {
        if (address != null) {
            viewModel.onEvent(InvitationFormUiEvent.UpdateAddress(address))
        }
    }

    InvitationEditScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateToAddressSearch = onNavigateToAddressSearch,
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
        modifier = modifier,
    )

    if (isShowDatePicker) {
        InvitationDatePickerBottomSheet(
            onConfirm = { viewModel.onEvent(InvitationFormUiEvent.UpdateDate(it)) },
            onDismiss = { isShowDatePicker = false },
        )
    }

    if (isShowStartTimePicker) {
        InvitationTimePickerBottomSheet(
            onConfirm = { hour, min ->
                viewModel.onEvent(InvitationFormUiEvent.UpdateStartTime(hour, min))
            },
            onDismissRequest = { isShowStartTimePicker = false },
            initialHour = uiState.invitationFormUiModel.startTime?.hour ?: 9,
            initialMinute = uiState.invitationFormUiModel.startTime?.min ?: 0,
        )
    }

    if (isShowEndTimePicker) {
        InvitationTimePickerBottomSheet(
            onConfirm = { hour, min ->
                viewModel.onEvent(InvitationFormUiEvent.UpdateEndTime(hour, min))
            },
            onDismissRequest = { isShowEndTimePicker = false },
            initialHour = uiState.invitationFormUiModel.endTime?.hour ?: 9,
            initialMinute = uiState.invitationFormUiModel.endTime?.min ?: 0,
        )
    }

    if (isShowAnnouncementSheet) {
        InvitationAddAnnouncementBottomSheet(
            onConfirm = { title, content ->
                viewModel.onEvent(InvitationFormUiEvent.UpdateAnnouncement(title, content))
            },
            onDismiss = { isShowAnnouncementSheet = false },
        )
    }

    if (isShowDeleteAnnouncement) {
        NachoDialog(onDismiss = { isShowDeleteAnnouncement = false }) {
            DeleteDialogContent(
                onConfirm = {
                    selectedAnnouncement?.let {
                        viewModel.onEvent(InvitationFormUiEvent.RemoveAnnouncement(it))
                        selectedAnnouncement = null
                    }
                    isShowDeleteAnnouncement = false
                },
                onDismiss = { isShowDeleteAnnouncement = false }
            )
        }
    }
}

@Composable
private fun InvitationEditScreen(
    uiState: InvitationFormUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (InvitationFormUiEvent) -> Unit,
    onAddImageClick: () -> Unit,
    onDateClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onNavigateToAddressSearch: () -> Unit,
    onAddAnnouncementClick: () -> Unit,
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
                title = stringResource(R.string.txt_edit_title),
                onBackClick = { onEvent(InvitationFormUiEvent.OnClickBack) },
                onPreviewClick = {},
                isLoading = uiState.isLoading
            )
        },
        bottomBar = {
            BottomBarSection(
                title = stringResource(R.string.btn_edit),
                enabled = uiState.isValid && !uiState.isLoading,
                onClick = { onEvent(InvitationFormUiEvent.OnClickSave) },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundTertiary),
            ) {
                item {
                    TitleSection(
                        title = uiState.invitationFormUiModel.title,
                        onTitleChange = { onEvent(InvitationFormUiEvent.UpdateTitle(it)) },
                        isLoading = uiState.isLoading,
                    )
                }

                item {
                    AuthorSection(
                        authorName = uiState.invitationFormUiModel.author,
                        onAuthorNameChange = { onEvent(InvitationFormUiEvent.UpdateAuthor(it)) },
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    ImageSection(
                        imageList = uiState.invitationFormUiModel.imageList,
                        onAddImageClick = onAddImageClick,
                        onRemoveClick = { onEvent(InvitationFormUiEvent.RemoveImage(it)) },
                        isLoading = uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    DateSection(
                        date = uiState.invitationFormUiModel.date,
                        onDateClick = onDateClick,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    TimeSection(
                        startTime = uiState.invitationFormUiModel.startTime,
                        endTime = uiState.invitationFormUiModel.endTime,
                        onStartTimeClick = onStartTimeClick,
                        onEndTimeClick = onEndTimeClick,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    AddressSection(
                        placeName = uiState.invitationFormUiModel.placeName,
                        placeAddress = uiState.invitationFormUiModel.placeAddress,
                        addressGuide = uiState.invitationFormUiModel.placeGuide,
                        onChangePlaceAddress = { onEvent(InvitationFormUiEvent.UpdatePlaceAddress(it)) },
                        onChangeAddressGuide = { onEvent(InvitationFormUiEvent.UpdateAddressGuide(it)) },
                        onNavigateToAddressSearch = onNavigateToAddressSearch,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                announcementSection(
                    announcementList = uiState.invitationFormUiModel.announcement,
                    onAddAnnouncementClick = onAddAnnouncementClick,
                    onRemoveAnnouncementClick = onRemoveAnnouncementClick,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(top = NachoSpacing.medium),
                )
            }

            if (uiState.isLoading) {
                InvitationLoadingIndicator()
            }
        }
    }
}

@PreviewTheme
@Composable
fun InvitationEditScreenPreview() {
    NachoTheme {
        InvitationEditScreen(
            uiState = InvitationFormUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onAddImageClick = {},
            onDateClick = {},
            onStartTimeClick = {},
            onEndTimeClick = {},
            onNavigateToAddressSearch = {},
            onAddAnnouncementClick = {},
            onRemoveAnnouncementClick = {},
        )
    }
}
