package com.andlife.ui.section.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.invitation.LatLngUiModel
import com.andlife.model.invitation.LocationInfo
import com.andlife.ui.R
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.openExternalMap
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.util.MarkerIcons

private const val DEFAULT_ZOOM_LEVEL = 15.0

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun PlaceGuideSection(
    location: LocationInfo,
    onMapError: () -> Unit,
    isMapVisible: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(NachoTheme.colorScheme.backgroundPrimary)
                .padding(vertical = NachoSpacing.large, horizontal = NachoSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        Text(
            text = stringResource(R.string.txt_place_guide_title),
            style = NachoTheme.typography.headingSmallSemiBold,
            color = NachoTheme.colorScheme.textPrimary,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            if (isMapVisible) {
                PlaceMapCard(
                    location = location,
                    onMapError = onMapError,
                )
            }

            location.guide?.takeIf {it.isNotBlank() }?.let { guide ->
                Text(
                    text = guide,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                    modifier =
                        Modifier
                            .padding(horizontal = NachoSpacing.small),
                )
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun PlaceMapCard(
    location: LocationInfo,
    onMapError: () -> Unit,
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            LatLng(location.latLng.latitude, location.latLng.longitude), DEFAULT_ZOOM_LEVEL
        )
    }

    val isMapReady = cameraPositionState.contentBounds != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = NachoTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = NachoTheme.colorScheme.backgroundSecondary,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            contentAlignment = Alignment.Center,
        ) {
            NaverMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    isScrollGesturesEnabled = true,
                    isZoomGesturesEnabled = true,
                    isZoomControlEnabled = true,
                    isLogoClickEnabled = false
                ),
            ) {
                Marker(
                    state = MarkerState(LatLng(location.latLng.latitude, location.latLng.longitude)),
                    icon = MarkerIcons.BLACK,
                    iconTintColor = NachoTheme.colorScheme.brandPrimary,
                    captionText = location.name,
                )
            }
            if (isMapReady) {
                NachoButton(
                    onClick = {
                        context.openExternalMap(
                            lat = location.latLng.latitude,
                            lng = location.latLng.longitude,
                            label = location.name,
                            onFail = onMapError
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(NachoSpacing.small),
                    containerColor = NachoTheme.colorScheme.brandOnPrimary,
                    contentColor = NachoTheme.colorScheme.brandPrimary,
                    contentPadding = PaddingValues(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall),
                ) {
                    Text(stringResource(R.string.btn_open_map_app))
                }
            }
            if (!isMapReady) {
                InvitationLoadingIndicator(
                    modifier = Modifier.background(NachoTheme.colorScheme.backgroundSecondary),
                    text = stringResource(R.string.txt_map_loading),
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun PlaceGuideSectionPreview() {
    NachoTheme {
        PlaceGuideSection(
            location = LocationInfo(
                name = "코드스쿼드",
                address = "서울시 강남구 테헤란로 521 3층",
                guide = "삼성역 5번 출구에서 도보 20분",
                latLng = LatLngUiModel(
                    latitude = 37.5111,
                    longitude = 127.0601,
                ),
            ),
            onMapError = {},
        )
    }
}
