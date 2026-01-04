package com.andlife.invitation.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlife.domain.model.MediaType
import com.andlife.domain.repository.SampleMediaRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val sampleMediaRepository: SampleMediaRepository,
) : ViewModel() {

    fun uploadSingleMedia(file: File, mediaType: MediaType) {
        viewModelScope.launch {
            Log.d("SampleUpload", "단건 업로드 시작: ${file.name}, 크기: ${file.length()}")

            sampleMediaRepository.uploadMedias(listOf(file to mediaType))
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

    fun uploadMultipleMedia(files: List<Pair<File, MediaType>>) {
        viewModelScope.launch {

            files.forEachIndexed { index, (file, type) ->
                Log.d("SampleUpload", "[$index] ${file.name}, 크기: ${file.length()} bytes, 타입: $type")
            }

            sampleMediaRepository.uploadMedias(files)
                .onSuccess { urls ->
                    val successCount = urls.count { it != null }
                    val failCount = urls.count { it == null }

                    Log.d("SampleUpload", "배치 업로드 완료 - 성공: $successCount, 실패: $failCount")

                    urls.forEachIndexed { index, url ->
                        if (url != null) {
                            Log.d("SampleUpload", "[$index] 성공: $url")
                        } else {
                            Log.e("SampleUpload", "[$index] 실패: ${files[index].first.name}")
                        }
                    }
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "배치 업로드 에러: $error")
                }
        }
    }
}
