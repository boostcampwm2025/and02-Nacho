package com.andlife.invitation_edit.screen.create

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
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.dragdrop.rememberDragDropState
import com.andlife.ui.component.dialog.NachoInfoDialog
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
import com.andlife.invitation_edit.viewmodel.InvitationCreateViewModel
import com.andlife.ui.component.InvitationDatePickerBottomSheet
import com.andlife.ui.component.InvitationTimePickerBottomSheet
import com.andlife.ui.component.addannouncement.InvitationAddAnnouncementBottomSheet
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.launch

private const val MAX_IMAGE_COUNT = 10

@Composable
fun InvitationCreateRoute(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateToPreview: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateCreateCard: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    address: AddressUiModel? = null,
    viewModel: InvitationCreateViewModel = hiltViewModel(),
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
    var selectedAnnouncementForEdit by remember { mutableStateOf<AnnouncementUiModel?>(null) }

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

            is InvitationFormSideEffect.SuccessSave -> {
                onNavigateToInvitationDetail(effect.id)
            }

            is InvitationFormSideEffect.InvalidTime -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_load_error_time))
                }
            }

            InvitationFormSideEffect.NavigateToPreview -> onNavigateToPreview
            InvitationFormSideEffect.FailLoad -> {}
        }
    }

    BackHandler {
        viewModel.onEvent(InvitationFormUiEvent.OnClickBack)
    }

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT)) { uris ->
            if (uris.isNotEmpty()) {
                val imageList = uris.map { it.toString() }
                viewModel.onEvent(InvitationFormUiEvent.UpdateImageList(imageList))
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar(message = res.getString(R.string.snack_load_error_image))
                }
            }
        }

    LaunchedEffect(address) {
        if (address != null) {
            viewModel.onEvent(InvitationFormUiEvent.UpdateAddress(address))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getCardEditorResult()
    }

    InvitationCreateScreen(
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
            selectedAnnouncementForEdit = null
            isShowAnnouncementSheet = true
        },
        onRemoveAnnouncementClick = {
            selectedAnnouncement = it
            isShowDeleteAnnouncement = true
        },
        onEditAnnouncementClick = { announcement ->
            selectedAnnouncementForEdit = announcement
            isShowAnnouncementSheet = true
        },
        onClickCreateCard = {
            onNavigateCreateCard()
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
            initialTitle = selectedAnnouncementForEdit?.title ?: "",
            initialContent = selectedAnnouncementForEdit?.content ?: "",
            onConfirm = { title, content ->
                viewModel.onEvent(
                    InvitationFormUiEvent.UpdateAnnouncement(
                        id = selectedAnnouncementForEdit?.id,
                        title = title,
                        content = content
                    )
                )
                selectedAnnouncementForEdit = null
                isShowAnnouncementSheet = false
            },
            onDismiss = {
                selectedAnnouncementForEdit = null
                isShowAnnouncementSheet = false
            },
        )
    }

    if (isShowDeleteAnnouncement) {
        NachoInfoDialog(
            title = stringResource(R.string.txt_remove_announcement_title),
            message = stringResource(R.string.txt_remove_announcement),
            confirmText = stringResource(R.string.txt_remove),
            dismissText = stringResource(R.string.txt_cancel),
            onConfirm = {
                selectedAnnouncement?.let {
                    viewModel.onEvent(InvitationFormUiEvent.RemoveAnnouncement(it))
                    selectedAnnouncement = null
                }
                isShowDeleteAnnouncement = false
            },
            onDismiss = { isShowDeleteAnnouncement = false },
        )
    }
}

@Composable
private fun InvitationCreateScreen(
    uiState: InvitationFormUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (InvitationFormUiEvent) -> Unit,
    onAddImageClick: () -> Unit,
    onDateClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onNavigateToAddressSearch: () -> Unit,
    onAddAnnouncementClick: () -> Unit,
    onClickCreateCard: () -> Unit,
    onRemoveAnnouncementClick: (AnnouncementUiModel) -> Unit,
    onEditAnnouncementClick: (AnnouncementUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(
        lazyListState = listState,
        onMove = { fromIndex, toIndex ->
            onEvent(InvitationFormUiEvent.ReorderAnnouncement(fromIndex, toIndex))
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopBarSection(
                title = stringResource(R.string.txt_create),
                onBackClick = { onEvent(InvitationFormUiEvent.OnClickBack) },
                onPreviewClick = { onEvent(InvitationFormUiEvent.OnClickPreview) },
                isLoading = uiState.isLoading
            )
        },
        bottomBar = {
            BottomBarSection(
                title = stringResource(R.string.txt_create_button),
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
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(NachoTheme.colorScheme.backgroundTertiary),
            ) {
                item {
                    TitleSection(
                        title = uiState.invitationFormUiModel.title,
                        onTitleChange = { onEvent(InvitationFormUiEvent.UpdateTitle(it)) },
                        isLoading = uiState.isLoading
                    )
                }

                item {
                    AuthorSection(
                        authorName = uiState.invitationFormUiModel.author,
                        onAuthorNameChange = { onEvent(InvitationFormUiEvent.UpdateAuthor(it)) },
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                        isLoading = uiState.isLoading
                    )
                }

                item {
                    ImageSection(
                        imageList = uiState.invitationFormUiModel.imageList,
                        onAddImageClick = onAddImageClick,
                        onRemoveClick = { onEvent(InvitationFormUiEvent.RemoveImage(it)) },
                        isLoading = uiState.isLoading,
                        modifier =
                            Modifier
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

                item {
                    CardSection(
                        cardUiModel = uiState.invitationFormUiModel.card,
                        onClickCreatedCard = onClickCreateCard,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                announcementSection(
                    announcementList = uiState.invitationFormUiModel.announcement,
                    onAddAnnouncementClick = onAddAnnouncementClick,
                    onRemoveAnnouncementClick = onRemoveAnnouncementClick,
                    onEditAnnouncementClick = onEditAnnouncementClick,
                    isLoading = uiState.isLoading,
                    dragDropState = dragDropState,
                    modifier = Modifier.padding(top = NachoSpacing.medium),
                )
            }
            if (uiState.isLoading) {
                InvitationLoadingIndicator()
            }
        }
    }
}
