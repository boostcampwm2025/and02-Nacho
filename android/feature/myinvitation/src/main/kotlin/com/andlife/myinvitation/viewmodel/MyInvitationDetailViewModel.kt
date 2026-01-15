package com.andlife.myinvitation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andlife.model.invitation.AnnouncementUiModel
import com.andlife.model.invitation.DateTimeInfo
import com.andlife.model.invitation.HostInfo
import com.andlife.model.invitation.InvitationCardUiModel
import com.andlife.model.invitation.InvitationContentsUiModel
import com.andlife.model.invitation.InvitationTimeUiModel
import com.andlife.model.invitation.LatLngUiModel
import com.andlife.model.invitation.LocationInfo
import com.andlife.myinvitation.MyInvitationDetail
import com.andlife.myinvitation.manager.KakaoShareManager
import com.andlife.myinvitation.model.detail.MyInvitationDetailSideEffect
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiEvent
import com.andlife.myinvitation.model.detail.MyInvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import com.andlife.ui.util.toDateTimeSingleLine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class MyInvitationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kakaoShareManager: KakaoShareManager,
) : BaseViewModel<MyInvitationDetailUiState, MyInvitationDetailUiEvent, MyInvitationDetailSideEffect>(
    initialState = MyInvitationDetailUiState(),
) {
    private val myInvitationId: Long = savedStateHandle.toRoute<MyInvitationDetail>().id

    override val uiState: StateFlow<MyInvitationDetailUiState> =
        mutableUiState
            .onStart {
                loadInvitationDetail()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyInvitationDetailUiState(),
            )

    private fun loadInvitationDetail() {
        viewModelScope.launch {
            updateState { copy(id = myInvitationId, isLoading = true) }

            // TODO: 실제 서버에서 데이터를 가져오는 로직 (임시 데이터로 대체)
            // val result = invitationRepository.getDetail(myInvitationId)

            // 성공 시 상태 업데이트
            updateState {
                copy(
                    id = 1L,
                    title = "2026년 나초 개발 네트워킹 데이",
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
                                    startTime = InvitationTimeUiModel(hour = 13, min = 30),
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
                            announcement =
                                persistentListOf(
                                    AnnouncementUiModel(
                                        title = "준비물",
                                        content = " - 코딩할 수 있는 노트북 \n - 건강한 정신",
                                    ),
                                    AnnouncementUiModel(
                                        title = "이벤트 안내",
                                        content = " - 코딩할 수 있는 노트북 \n - 건강한 정신",
                                    ),
                                ),
                        ),
                )
            }
        }
    }

    override fun onEvent(event: MyInvitationDetailUiEvent) {
        when (event) {
            is MyInvitationDetailUiEvent.ClickBack -> clickClose()
            is MyInvitationDetailUiEvent.ClickThanksCard -> showThanksCardOnboarding()
            is MyInvitationDetailUiEvent.ClickShare -> shareInvitation()
            is MyInvitationDetailUiEvent.ClickEdit -> navigateToEditInvitation()
            is MyInvitationDetailUiEvent.ClickDelete -> deleteInvitation()
            is MyInvitationDetailUiEvent.CreateThanksCard -> navigateToCreateThanksCard()
            is MyInvitationDetailUiEvent.ClickEditCard -> navigateToEditCard()
            is MyInvitationDetailUiEvent.ClickImage -> navigateToFullScreenImage(event.imageList, event.index)
            is MyInvitationDetailUiEvent.MapError -> showMapErrorSnackbar()
        }
    }

    private fun clickClose() {
        sendEffect(MyInvitationDetailSideEffect.NavigateBack)
    }

    private fun shareInvitation() {
        val state = uiState.value
        val content = state.invitationContentsUiModel
        val dateText = content.dateTime.toDateTimeSingleLine()
        val locationText = content.location.name
        val firstImage = content.imageList.firstOrNull()

        kakaoShareManager.share(
            invitationId = state.id,
            title = content.title,
            imageUrl = firstImage,
            date = dateText,
            location = locationText,
        )
    }

    private fun deleteInvitation() { /* TODO: 초대장 삭제 로직 */ }
    private fun navigateToEditInvitation() { /* TODO: 초대장 편집 이동 */ }
    private fun showThanksCardOnboarding() { /* TODO: 감사카드 온보딩 */ }
    private fun navigateToCreateThanksCard() { /* TODO: 감사카드 작성 이동 */ }

    private fun navigateToEditCard() { // TODO: 초대카드 편집 이동
        sendEffect(MyInvitationDetailSideEffect.NavigateToEditCard(myInvitationId))
    }

    private fun navigateToFullScreenImage(imageList: ImmutableList<String>, index: Int) { /* TODO: 이미지 풀스크린*/ }

    private fun showMapErrorSnackbar() {
        sendEffect(MyInvitationDetailSideEffect.ShowMapErrorSnackbar)
    }
}
