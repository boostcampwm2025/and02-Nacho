package com.andlife.ui.util.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AudioRecorder(
    private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null
    private var recordingJob: Job? = null
    private var currentAudioFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0)
    val recordingDuration: StateFlow<Int> = _recordingDuration.asStateFlow()

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> = _amplitude.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    fun startRecording(audioFile: File, onError: (Exception) -> Unit = {}) {
        try {
            if (_isRecording.value) {
                return
            }

            // 디렉토리 생성
            audioFile.parentFile?.let { parentDir ->
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
            }

            currentAudioFile = audioFile

            // MediaRecorder 초기화
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(audioFile.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                try {
                    prepare()
                    start()

                    _isRecording.value = true
                    _recordingDuration.value = 0
                    _amplitude.value = 0

                    // 타이머와 볼륨 모니터링 시작
                    startTimer()
                } catch (e: IOException) {
                    cleanup()
                    onError(e)
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun pauseRecording(onError: (Exception) -> Unit = {}) {
        if (!_isRecording.value || _isPaused.value) return
        try {
            mediaRecorder?.pause()
            _isPaused.value = true
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun resumeRecording(onError: (Exception) -> Unit = {}) {
        if (!_isRecording.value || !_isPaused.value) return
        try {
            mediaRecorder?.resume()
            _isPaused.value = false
        } catch (e: Exception) {
            onError(e)
        }
    }


    fun stopRecording(onComplete: (File?) -> Unit = {}) {
        try {
            if (!_isRecording.value) {
                onComplete(null)
                return
            }

            val recordedFile = currentAudioFile

            mediaRecorder?.apply {
                try {
                    stop()
                    reset()
                    release()
                } catch (e: Exception) {
                    // 이미 중지된 상태일 수 있음
                }
            }

            mediaRecorder = null
            _isRecording.value = false
            _isPaused.value = false
            _recordingDuration.value = 0
            _amplitude.value = 0
            recordingJob?.cancel()
            currentAudioFile = null

            // 녹음된 파일이 존재하고 크기가 0보다 큰지 확인
            if (recordedFile?.exists() == true && recordedFile.length() > 0) {
                onComplete(recordedFile)
            } else {
                onComplete(null)
            }
        } catch (e: Exception) {
            _isRecording.value = false
            recordingJob?.cancel()
            currentAudioFile = null
            onComplete(null)
        }
    }

    private fun startTimer() {
        recordingJob?.cancel()
        recordingJob = scope.launch {
            var secondsCounter = 0
            while (isActive && _isRecording.value) {
                delay(100) // 100ms마다 체크
                if (_isPaused.value) continue
                
                // 볼륨 업데이트 (100ms마다)
                try {
                    val amplitude = mediaRecorder?.maxAmplitude ?: 0
                    _amplitude.value = amplitude
                } catch (e: Exception) {
                    // 무시
                }
                
                // 시간 업데이트 (1초마다)
                secondsCounter++
                if (secondsCounter >= 10) { // 100ms * 10 = 1초
                    _recordingDuration.value += 1
                    secondsCounter = 0
                }
            }
        }
    }

    private fun cleanup() {
        mediaRecorder?.apply {
            try {
                reset()
                release()
            } catch (e: Exception) {
                // 이미 해제된 상태일 수 있음
            }
        }
        mediaRecorder = null
        _isRecording.value = false
        recordingJob?.cancel()
        currentAudioFile = null
    }

    fun release() {
        stopRecording()
        cleanup()
    }

    fun isCurrentlyRecording(): Boolean = _isRecording.value

    fun getCurrentDuration(): Int = _recordingDuration.value
}
