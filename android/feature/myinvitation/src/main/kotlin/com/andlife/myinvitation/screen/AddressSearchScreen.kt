package com.andlife.myinvitation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andlife.designsystem.component.InvitationTextField
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationStroke
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.domain.model.Address
import com.andlife.myinvitation.R
import com.andlife.myinvitation.model.AddressSearchSideEffect
import com.andlife.myinvitation.model.AddressSearchUiEvent
import com.andlife.myinvitation.model.AddressSearchUiState
import com.andlife.myinvitation.viewmodel.AddressSearchViewModel
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.flow.flowOf

@Composable
fun AddressSearchRoute(
    onBack: () -> Unit,
    onAddressSelected: (Address) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddressSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val addressItems = viewModel.addresses.collectAsLazyPagingItems()

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is AddressSearchSideEffect.NavigateBackWithAddress -> {
                onAddressSelected(effect.address)
            }

            AddressSearchSideEffect.NavigateBack -> {
                onBack()
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
    addressItems: LazyPagingItems<Address>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AddressSearchTopBar(
                onBack = { onEvent(AddressSearchUiEvent.ClickBack) },
            )
        },
        containerColor = InvitationTheme.colorScheme.backgroundPrimary,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = InvitationSpacing.medium),
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
                            itemCount = addressItems.itemCount,
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
                                onAddressClick = { onEvent(AddressSearchUiEvent.SelectAddress(it)) },
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
                text = stringResource(R.string.label_address_search_title),
                style = InvitationTheme.typography.headingSmallSemiBold,
                color = InvitationTheme.colorScheme.textPrimary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back_24),
                    contentDescription = stringResource(R.string.des_address_search_back),
                    tint = InvitationTheme.colorScheme.iconSecondary,
                )
            }
        },
        windowInsets = WindowInsets(),
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = InvitationTheme.colorScheme.backgroundPrimary,
            ),
    )
}

@Composable
private fun SearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    InvitationTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = stringResource(R.string.label_address_search_input_hint),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search_24),
                contentDescription = stringResource(R.string.des_address_search_input),
                tint = InvitationTheme.colorScheme.iconDisabled,
            )
        },
        modifier = modifier.padding(vertical = InvitationSpacing.medium),
    )
}

@Composable
private fun EmptySearchGuide(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.label_address_search_guide),
            style = InvitationTheme.typography.bodyMediumRegular,
            color = InvitationTheme.colorScheme.textTertiary,
        )
    }
}

@Composable
private fun SearchResultCount(
    itemCount: Int,
    loadState: androidx.paging.LoadState,
    modifier: Modifier = Modifier,
) {
    if (loadState is androidx.paging.LoadState.NotLoading) {
        Row(
            modifier = modifier.padding(InvitationSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall),
        ) {
            Text(
                text = stringResource(R.string.label_address_search_result),
                style = InvitationTheme.typography.bodyMediumRegular,
                color = InvitationTheme.colorScheme.textPrimary,
            )
            Text(
                text = "$itemCount",
                style = InvitationTheme.typography.bodyMediumRegular,
                color = InvitationTheme.colorScheme.brandPrimary,
            )
        }
    }
}

@Composable
private fun AddressResultList(
    itemCount: Int,
    getItem: (Int) -> Address?,
    onAddressClick: (Address) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
        contentPadding = PaddingValues(vertical = InvitationSpacing.medium),
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
    address: Address,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        shape = InvitationTheme.shapes.small,
        colors =
            CardDefaults.cardColors(
                containerColor = InvitationTheme.colorScheme.backgroundPrimary,
            ),
        border =
            BorderStroke(
                InvitationStroke.small,
                InvitationTheme.colorScheme.backgroundBorder,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(InvitationSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = address.roadAddress,
                    style = InvitationTheme.typography.bodyMediumRegular,
                    color = InvitationTheme.colorScheme.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = address.zipCode,
                    style = InvitationTheme.typography.bodySmallRegular,
                    color = InvitationTheme.colorScheme.textTertiary,
                )
            }

            Text(
                text = address.placeName,
                style = InvitationTheme.typography.bodySmallRegular,
                color = InvitationTheme.colorScheme.textSecondary,
            )

            Text(
                text = address.streetAddress,
                style = InvitationTheme.typography.bodySmallRegular,
                color = InvitationTheme.colorScheme.textTertiary,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun AddressSearchScreenPreview() {
    val emptyPagingItems = flowOf(PagingData.empty<Address>()).collectAsLazyPagingItems()
    InvitationTheme {
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
            Address(
                id = 1,
                roadAddress = "서울특별시 강남구 강남대로62길 23",
                placeName = "코드스쿼드",
                streetAddress = "서울특별시 강남구 강남대로62길 23 4층",
                zipCode = "06175",
                latitude = 37.5012743,
                longitude = 127.0396597,
            ),
            Address(
                id = 2,
                roadAddress = "서울특별시 서초구 강남대로 202",
                placeName = "양재역",
                streetAddress = "서울특별시 서초구 강남대로 202",
                zipCode = "06752",
                latitude = 37.4845239,
                longitude = 127.0343395,
            ),
        )

    InvitationTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(InvitationSpacing.medium),
        ) {
            SearchResultCount(
                itemCount = fakeAddresses.size,
                loadState = androidx.paging.LoadState.NotLoading(endOfPaginationReached = true),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
                contentPadding = PaddingValues(vertical = InvitationSpacing.medium),
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
