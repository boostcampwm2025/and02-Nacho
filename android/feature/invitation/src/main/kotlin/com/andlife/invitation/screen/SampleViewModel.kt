package com.andlife.invitation.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlife.domain.model.MediaFile
import com.andlife.domain.repository.SampleMediaRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val sampleMediaRepository: SampleMediaRepository,
) : ViewModel() {

    fun uploadSingleMedia(mediaFile: MediaFile) {
        viewModelScope.launch {
            Log.d("SampleUpload", "단건 업로드 시작: ${mediaFile.fileName}, 크기: ${mediaFile.fileSize}")

            sampleMediaRepository.uploadMedia(listOf(mediaFile))
                .onSuccess { urls ->
                    val url = urls.firstOrNull()
                    if (url != null) {
                        Log.d("SampleUpload", "단건 업로드 성공: $url")
                    } else {
                        Log.e("SampleUpload", "단건 업로드 실패: URL이 null")
                    }
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "단건 업로드 실패: $error")
                }
        }
    }

    fun uploadMultipleMedia(mediaFiles: List<MediaFile>) {
        viewModelScope.launch {
            Log.d("SampleUpload", "배치 업로드 시작 - 파일 개수: ${mediaFiles.size}")

            mediaFiles.forEachIndexed { index, file ->
                Log.d("SampleUpload", "[$index] ${file.fileName}, 크기: ${file.fileSize} bytes, 타입: ${file.mediaType}")
            }

            sampleMediaRepository.uploadMedia(mediaFiles)
                .onSuccess { urls ->
                    val successCount = urls.count { it != null }
                    val failCount = urls.count { it == null }

                    Log.d("SampleUpload", "배치 업로드 완료 - 성공: $successCount, 실패: $failCount")

                    urls.forEachIndexed { index, url ->
                        if (url != null) {
                            Log.d("SampleUpload", "[$index] 성공: $url")
                        } else {
                            Log.e("SampleUpload", "[$index] 실패: ${mediaFiles[index].fileName}")
                        }
                    }
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "배치 업로드 에러: $error")
                }
        }
    }
}
