package com.andlife.invitation_edit.screen.edit

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.model.address.AddressUiModel
import com.andlife.invitation_edit.model.create.CreateInvitationUiState
import com.andlife.invitation_edit.section.BottomBarSection
import com.andlife.invitation_edit.section.TopBarSection
import com.andlife.invitation_edit.viewmodel.MyInvitationEditViewModel
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.section.AddressSection
import com.andlife.invitation_edit.section.AuthorSection
import com.andlife.invitation_edit.section.CardSection
import com.andlife.invitation_edit.section.DateSection
import com.andlife.invitation_edit.section.ImageSection
import com.andlife.invitation_edit.section.TimeSection
import com.andlife.invitation_edit.section.TitleSection
import com.andlife.invitation_edit.section.announcementSection
import com.andlife.ui.component.loading.InvitationLoadingIndicator

@Composable
fun MyInvitationEditRoute(
    onNavigateToAddressSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateCreateCard: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    address: AddressUiModel? = null,
    viewModel: MyInvitationEditViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    MyInvitationEditScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHost,
        modifier = modifier,
    )
}

@Composable
fun MyInvitationEditScreen(
    uiState: CreateInvitationUiState,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
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
                onBackClick = onNavigateBack,
                onPreviewClick = {},
                isLoading = false
            )
        },
        bottomBar = {
            BottomBarSection(
                title = stringResource(R.string.btn_edit),
                onClick = {},
                enabled = true,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundTertiary),
            ) {
                item {
                    TitleSection(
                        title = uiState.createInvitationUiModel.title,
                        onTitleChange = {},
                        isLoading = uiState.isLoading,
                    )
                }

                item {
                    AuthorSection(
                        authorName = uiState.createInvitationUiModel.author,
                        onAuthorNameChange = {},
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    ImageSection(
                        imageList = uiState.createInvitationUiModel.imageList,
                        onAddImageClick = {},
                        onRemoveClick = {},
                        isLoading = uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    DateSection(
                        date = uiState.createInvitationUiModel.date,
                        onDateClick = {},
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    TimeSection(
                        startTime = uiState.createInvitationUiModel.startTime,
                        endTime = uiState.createInvitationUiModel.endTime,
                        onStartTimeClick = {},
                        onEndTimeClick = {},
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                item {
                    AddressSection(
                        placeName = uiState.createInvitationUiModel.placeName,
                        placeAddress = uiState.createInvitationUiModel.placeAddress,
                        addressGuide = uiState.createInvitationUiModel.placeGuide,
                        onChangePlaceAddress = {},
                        onChangeAddressGuide = {},
                        onNavigateToAddressSearch = {},
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),

                    )
                }

                item {
                    CardSection(
                        cardUiModel = uiState.createInvitationUiModel.card,
                        onClickCreatedCard = {},
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(top = NachoSpacing.medium),
                    )
                }

                announcementSection(
                    announcementList = uiState.createInvitationUiModel.announcement,
                    onRemoveAnnouncementClick = {},
                    onAddAnnouncementClick = {},
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(top = NachoSpacing.medium)
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
fun MyInvitationEditScreenPreview() {
    NachoTheme {
        MyInvitationEditScreen(
            uiState = CreateInvitationUiState(),
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
