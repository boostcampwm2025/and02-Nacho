package com.andlife.invitation.screen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun uploadSingleMedia(uri: Uri) {
        viewModelScope.launch {
            Log.d("SampleUpload", "단건 업로드 시작")

            sampleMediaRepository.uploadMedias(listOf(uri.toString()))
                .onSuccess { urls ->
                    val url = urls.firstOrNull()
                    if (url != null) {
                        Log.d("SampleUpload", "업로드 성공: $url")
                    } else {
                        Log.e("SampleUpload", "업로드 실패")
                    }
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "업로드 실패: $error")
                }
        }
    }

    fun uploadMultipleMedia(uris: List<Uri>) {
        viewModelScope.launch {
            Log.d("SampleUpload", "배치 업로드 시작 - ${uris.size}개")

            sampleMediaRepository.uploadMedias(uris.map { it.toString() })
                .onSuccess { urls ->
                    Log.d("SampleUpload", "업로드 성공: ${urls.size}개")
                    urls.forEachIndexed { index, url ->
                        if (url != null) {
                            Log.d("SampleUpload", "[$index] 성공: $url")
                        } else {
                            Log.e("SampleUpload", "[$index] 실패: 업로드 실패")
                        }
                    }
                }
                .onFailure { error ->
                    Log.e("SampleUpload", "에러: $error")
                }
        }
    }
}
