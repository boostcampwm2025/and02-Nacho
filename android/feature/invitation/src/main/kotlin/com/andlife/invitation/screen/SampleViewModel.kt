package com.andlife.invitation.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.SampleMediaRepository
import com.andlife.domain.repository.MediaType
import com.andlife.domain.util.onSuccess
import com.andlife.domain.util.onFailure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val sampleMediaRepository: SampleMediaRepository,
) : ViewModel() {

    // 단건 업로드
    fun uploadSampleMedia(file: File, mediaType: MediaType) {
        viewModelScope.launch {
            sampleMediaRepository.uploadSingleMedia(file, mediaType)
                .onSuccess { url ->
                    Log.d("SampleUpload", "단건 업로드 성공: $url")
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "단건 업로드 실패, $error")
                }
        }
    }

    // 다건 업로드
    fun uploadSampleMedias(files: List<Pair<File, MediaType>>) {
        viewModelScope.launch {
            Log.d("SampleUpload", "파일 개수: ${files.size}")
            files.forEachIndexed { index, (file, type) ->
                Log.d("SampleUpload", "[$index] ${file.name}, 크기: ${file.length()}, 타입: $type")
            }

            sampleMediaRepository.uploadMultipleMedia(files)
                .onSuccess { urls ->
                    urls.forEachIndexed { index, url ->
                        Log.d("SampleUpload", "[$index] $url")
                    }
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "배치 업로드 에러: $error")
                }
        }
    }
}
