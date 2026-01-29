package com.andlife.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.util.audio.AudioRecorder
import com.andlife.ui.util.toFormatDuration
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingBottomSheet(
    audioRecorder: AudioRecorder,
    onRecordingComplete: (File) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val isRecording by audioRecorder.isRecording.collectAsStateWithLifecycle()
    val isPaused by audioRecorder.isPaused.collectAsStateWithLifecycle()
    val recordingDuration by audioRecorder.recordingDuration.collectAsStateWithLifecycle()

    BackHandler {
        scope.launch {
            if (isRecording) {
                audioRecorder.stopRecording { _ -> }
            }
            sheetState.hide()
        }.invokeOnCompletion { onDismiss() }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                audioRecorder.stopRecording { _ -> }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            if (isRecording) {
                audioRecorder.stopRecording { _ -> }
            }
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        dragHandle = {},
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = NachoSpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.label_audio_recording),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                    modifier = Modifier.align(Alignment.Center),
                )

                IconButton(
                    onClick = {
                        scope.launch {
                            if (isRecording) {
                                audioRecorder.stopRecording { _ -> }
                            }
                            sheetState.hide()
                        }.invokeOnCompletion { onDismiss() }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close_24),
                        contentDescription = stringResource(R.string.desc_close),
                        tint = NachoTheme.colorScheme.textPrimary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(NachoSpacing.large))

            // 녹음 시간 표시
            Text(
                text = recordingDuration.toFormatDuration(),
                style = NachoTheme.typography.headingSmallBold,
                color = NachoTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(NachoSpacing.large))

            // 녹음 관련 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 일시정지/재개 버튼 (녹음 시작되면 표시)
                if (isRecording) {
                    IconButton(
                        onClick = {
                            if (isPaused) {
                                audioRecorder.resumeRecording()
                            } else {
                                audioRecorder.pauseRecording()
                            }
                        },
                        modifier = Modifier
                            .size(NachoIconSize.xLarge)
                            .background(
                                color = NachoTheme.colorScheme.backgroundSecondary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isPaused) R.drawable.ic_play_arrow_24 else R.drawable.ic_pause_filled_24
                            ),
                            contentDescription = if (isPaused) {
                                stringResource(R.string.desc_resume_recording)
                            } else {
                                stringResource(R.string.desc_pause_recording)
                            },
                            tint = NachoTheme.colorScheme.textPrimary,
                            modifier = Modifier.size(NachoIconSize.small)
                        )
                    }
                }

                // 녹음 시작/완료 버튼
                IconButton(
                    onClick = {
                        if (isRecording) {
                            audioRecorder.stopRecording { file ->
                                if (file != null) {
                                    onRecordingComplete(file)
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion { onDismiss() }
                                }
                            }
                        } else {
                            // 녹음 시작
                            val audioRecordingsDir = File(context.cacheDir, "audio_recordings")
                            if (!audioRecordingsDir.exists()) {
                                audioRecordingsDir.mkdirs()
                            }
                            val audioFile = File(
                                audioRecordingsDir,
                                "audio_${System.currentTimeMillis()}.m4a"
                            )
                            audioRecorder.startRecording(audioFile) { _ ->
                                // 에러 처리
                            }
                        }
                    },
                    modifier = Modifier
                        .size(NachoIconSize.xLarge)
                        .background(
                            color = NachoTheme.colorScheme.brandPrimary,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isRecording) R.drawable.ic_upload_24 else R.drawable.ic_mic_24
                        ),
                        contentDescription = if (isRecording) {
                            stringResource(R.string.desc_stop_recording)
                        } else {
                            stringResource(R.string.desc_start_recording)
                        },
                        tint = NachoTheme.colorScheme.backgroundPrimary,
                        modifier = Modifier.size(NachoIconSize.small)
                    )
                }
            }

            Spacer(modifier = Modifier.height(NachoSpacing.large))

            // 안내 텍스트
            Text(
                text = when {
                    !isRecording -> stringResource(R.string.txt_recording_guide)
                    isPaused -> stringResource(R.string.txt_recording_paused)
                    else -> stringResource(R.string.txt_recording_in_progress)
                },
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewTheme
@Composable
private fun AudioRecordingBottomSheetPreview() {
    NachoTheme {
        AudioRecordingBottomSheet(
            audioRecorder = AudioRecorder(context = LocalContext.current),
            onRecordingComplete = {},
            onDismiss = {}
        )
    }
}
