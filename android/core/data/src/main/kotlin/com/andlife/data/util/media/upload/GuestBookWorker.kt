package com.andlife.data.util.media.upload

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.Result as DomainResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class GuestBookWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val guestBookRepository: GuestBookRepository,
    private val notificationManager: UploadNotificationManager,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 입력 데이터 가져오기
            val invitationId = inputData.getLong(UploadKey.INVITATION_ID, -1L)
            val guestBookText = inputData.getString(UploadKey.GUEST_BOOK_TEXT) ?: ""
            val editingGuestBookId = inputData.getLong(UploadKey.EDITING_GUEST_BOOK_ID, -1L)

            // 업로드된 미디어 정보 가져오기 (필드별 리스트)
            val mediaIds: List<Long?> = Json.decodeFromString(inputData.getString(UploadKey.MEDIA_IDS) ?: "")
            val mediaUris: List<String> = Json.decodeFromString(inputData.getString(UploadKey.MEDIA_URIS) ?: "")
            val mediaTypes: List<String> = Json.decodeFromString(inputData.getString(UploadKey.MEDIA_TYPES) ?: "")
            val mediaDurations: List<Int?> =
                Json.decodeFromString(inputData.getString(UploadKey.MEDIA_DURATIONS) ?: "")
            val thumbnailUrls: List<String?> =
                Json.decodeFromString(inputData.getString(UploadKey.MEDIA_THUMBNAIL_URLS) ?: "")

            // 필드별 리스트를 GuestBookMedia 객체 리스트로 변환
            val guestBookMedias: List<GuestBookMedia> = mediaTypes.mapIndexed { index, mediaTypeString ->
                val mediaType = when (mediaTypeString) {
                    MediaType.IMAGE.name -> MediaType.IMAGE
                    MediaType.VIDEO.name -> MediaType.VIDEO
                    MediaType.AUDIO.name -> MediaType.AUDIO
                    else -> throw IllegalArgumentException("알 수 없는 미디어 타입: $mediaTypeString")
                }

                GuestBookMedia(
                    id = mediaIds.getOrNull(index),
                    type = mediaType,
                    url = mediaUris.getOrNull(index) ?: "",
                    thumbnailUrl = thumbnailUrls.getOrNull(index),
                    durationSeconds = mediaDurations.getOrNull(index),
                    displayOrder = index
                )
            }

            // 방명록 생성/수정
            val result = if (editingGuestBookId != -1L) {
                guestBookRepository.updateGuestBook(
                    guestBookId = editingGuestBookId,
                    textContent = guestBookText,
                    existingImageIds = guestBookMedias.filter { it.id != null && it.type == MediaType.IMAGE }
                        .map { it.id!! },
                    existingVideoIds = guestBookMedias.filter { it.id != null && it.type == MediaType.VIDEO }
                        .map { it.id!! },
                    existingAudioIds = guestBookMedias.filter { it.id != null && it.type == MediaType.AUDIO }
                        .map { it.id!! },
                    newMedias = guestBookMedias.filter { it.id == null }
                )
            } else {
                guestBookRepository.createGuestBook(
                    invitationId = invitationId,
                    textContent = guestBookText,
                    medias = guestBookMedias
                )
            }

            when (result) {
                is DomainResult.Success -> {
                    notificationManager.notifyComplete(id)
                    Result.success(
                        workDataOf(
                            UploadKey.RESULT_URLS to guestBookMedias.map { it.url }.toTypedArray(),
                            UploadKey.PROGRESS to 100
                        )
                    )
                }

                is DomainResult.Error -> {
                    Result.failure(
                        workDataOf(
                            UploadKey.ERROR_MESSAGE to "방명록 처리 실패: ${result.message ?: "알 수 없는 오류"}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "방명록 처리 중 오류 발생", e)
            Result.failure(
                workDataOf(
                    UploadKey.ERROR_MESSAGE to (e.message ?: UploadError.UNKNOWN)
                )
            )
        }
    }

    companion object {
        private const val TAG = "GuestBookWorker"
    }
}
