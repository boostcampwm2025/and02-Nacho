package com.andlife.invitation_edit.screen.address

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andlife.designsystem.component.NachoTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.model.AddressSearchSideEffect
import com.andlife.invitation_edit.model.AddressSearchUiEvent
import com.andlife.invitation_edit.model.AddressSearchUiState
import com.andlife.invitation_edit.model.AddressUiModel
import com.andlife.invitation_edit.viewmodel.AddressSearchViewModel
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.flow.flowOf
import com.andlife.designsystem.R as designR

@Composable
fun AddressSearchRoute(
    onNavigateBack: () -> Unit,
    onAddressSelect: (AddressUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddressSearchViewModel = hiltViewModel(),
) {
    val keyboardManager = LocalSoftwareKeyboardController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val addressItems = viewModel.addresses.collectAsLazyPagingItems()

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is AddressSearchSideEffect.NavigateBackWithAddress -> {
                keyboardManager?.hide()
                onAddressSelect(effect.addressUiModel)
            }

            AddressSearchSideEffect.NavigateBack -> {
                onNavigateBack()
            }
        }
    }

    AddressSearchScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        addressItems = addressItems,
        modifier = modifier,
    )
}

@Composable
private fun AddressSearchScreen(
    uiState: AddressSearchUiState,
    onEvent: (AddressSearchUiEvent) -> Unit,
    addressItems: LazyPagingItems<AddressUiModel>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddressSearchTopBar(
                onBack = { onEvent(AddressSearchUiEvent.ClickBack) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = NachoSpacing.medium),
        ) {
            SearchInputField(
                query = uiState.query,
                onQueryChange = { onEvent(AddressSearchUiEvent.UpdateQuery(it)) },
            )

            if (uiState.query.isBlank()) {
                EmptySearchGuide(
                    modifier = Modifier.weight(1f),
                )
            } else {
                key(uiState.query) {
                    Column {
                        SearchResultCount(
                            itemCount = uiState.totalCount,
                            loadState = addressItems.loadState.refresh,
                        )

                        PagingStateContent(
                            loadState = addressItems.loadState.refresh,
                            itemCount = addressItems.itemCount,
                            onRetry = { addressItems.retry() },
                        ) {
                            AddressResultList(
                                itemCount = addressItems.itemCount,
                                getItem = { index -> addressItems[index] },
                                onAddressClick = { addressUiModel ->
                                    onEvent(AddressSearchUiEvent.SelectAddress(addressUiModel))
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressSearchTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.txt_address_search_title),
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
}

@Composable
private fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NachoTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = stringResource(R.string.txt_address_input_hint),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search_24),
                contentDescription = stringResource(R.string.desc_search_input_icon),
                tint = NachoTheme.colorScheme.iconDisabled,
            )
        },
        modifier = modifier.padding(vertical = NachoSpacing.medium),
    )
}

@Composable
private fun EmptySearchGuide(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.msg_search_guide),
            style = NachoTheme.typography.bodyMediumRegular,
            color = NachoTheme.colorScheme.textTertiary,
        )
    }
}

@Composable
private fun SearchResultCount(
    itemCount: Int,
    loadState: LoadState,
    modifier: Modifier = Modifier,
) {
    if (loadState is LoadState.NotLoading) {
        Row(
            modifier = modifier.padding(NachoSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
        ) {
            Text(
                text = stringResource(R.string.txt_search_result),
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textPrimary,
            )
            Text(
                text = "$itemCount",
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.brandPrimary,
            )
        }
    }
}

@Composable
private fun AddressResultList(
    itemCount: Int,
    getItem: (Int) -> AddressUiModel?,
    onAddressClick: (AddressUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
        contentPadding = PaddingValues(vertical = NachoSpacing.medium),
    ) {
        items(count = itemCount) { index ->
            val address = getItem(index)
            address?.let {
                AddressItem(
                    address = it,
                    onClick = { onAddressClick(it) },
                )
            }
        }
    }
}

@Composable
private fun AddressItem(
    address: AddressUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier.fillMaxWidth(),
        shape = NachoTheme.shapes.small,
        colors =
            CardDefaults.cardColors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
            ),
        border =
            BorderStroke(
                NachoStroke.small,
                NachoTheme.colorScheme.backgroundBorder,
            ),
        onClick = onClick,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = address.addressName,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = address.zipCode,
                    style = NachoTheme.typography.bodySmallRegular,
                    color = NachoTheme.colorScheme.textTertiary,
                )
            }

            Text(
                text = address.placeName,
                style = NachoTheme.typography.bodySmallRegular,
                color = NachoTheme.colorScheme.textSecondary,
            )

            Text(
                text = address.roadAddressName,
                style = NachoTheme.typography.bodySmallRegular,
                color = NachoTheme.colorScheme.textTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun AddressSearchScreenPreview() {
    val emptyPagingItems = flowOf(PagingData.empty<AddressUiModel>()).collectAsLazyPagingItems()
    NachoTheme {
        AddressSearchScreen(
            uiState = AddressSearchUiState("강남"),
            onEvent = {},
            addressItems = emptyPagingItems,
        )
    }
}

@PreviewTheme
@Composable
private fun AddressSearchResultPreview() {
    val fakeAddresses =
        listOf(
            AddressUiModel(
                id = 1,
                roadAddressName = "서울특별시 강남구 강남대로62길 23",
                placeName = "코드스쿼드",
                addressName = "서울특별시 강남구 강남대로62길 23 4층",
                zipCode = "06175",
                latitude = 37.5012743,
                longitude = 127.0396597,
            ),
            AddressUiModel(
                id = 2,
                roadAddressName = "서울특별시 서초구 강남대로 202",
                placeName = "양재역",
                addressName = "서울특별시 서초구 강남대로 202",
                zipCode = "06752",
                latitude = 37.4845239,
                longitude = 127.0343395,
            ),
        )

    NachoTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(NachoSpacing.medium),
        ) {
            SearchResultCount(
                itemCount = fakeAddresses.size,
                loadState = LoadState.NotLoading(endOfPaginationReached = true),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
                contentPadding = PaddingValues(vertical = NachoSpacing.medium),
            ) {
                items(count = fakeAddresses.size) { index ->
                    AddressItem(
                        address = fakeAddresses[index],
                        onClick = { },
                    )
                }
            }
        }
    }
}
