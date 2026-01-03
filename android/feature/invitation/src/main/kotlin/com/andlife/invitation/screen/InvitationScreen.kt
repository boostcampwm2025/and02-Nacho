package com.andlife.invitation.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.domain.model.MediaType
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationScreen(
    modifier: Modifier = Modifier,
    viewModel: SampleViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val mediaType = getMediaTypeFromUri(context, it)
            val file = uriToFile(context, it, mediaType)
            viewModel.uploadSampleMedia(file, mediaType)
        }
    }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val files = uris.map { uri ->
                val mediaType = getMediaTypeFromUri(context, uri)
                val file = uriToFile(context, uri, mediaType)
                file to mediaType
            }
            viewModel.uploadSampleMedias(files)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sample Media Upload") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Button(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("이미지 1개 업로드")
            }

            Button(
                onClick = {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageAndVideo
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("이미지/비디오 여러개 업로드")
            }
        }
    }
}

private fun getMediaTypeFromUri(context: Context, uri: Uri): MediaType {
    val mimeType = context.contentResolver.getType(uri)
    return when {
        mimeType?.startsWith("image/") == true -> MediaType.IMAGE
        mimeType?.startsWith("video/") == true -> MediaType.VIDEO
        mimeType?.startsWith("audio/") == true -> MediaType.AUDIO
        else -> {
            // mimeType을 못 가져온 경우 확장자로 판별
            val extension = context.contentResolver.openInputStream(uri)?.use {
                getExtensionFromUri(context, uri)
            } ?: ""

            when (extension.lowercase()) {
                "jpg", "jpeg", "png", "gif", "webp" -> MediaType.IMAGE
                "mp4", "mov", "avi", "mkv" -> MediaType.VIDEO
                "mp3", "wav", "m4a", "aac" -> MediaType.AUDIO
                else -> MediaType.IMAGE // 기본값
            }
        }
    }
}

private fun getExtensionFromUri(context: Context, uri: Uri): String {
    return when (uri.scheme) {
        "content" -> {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        val displayName = it.getString(displayNameIndex)
                        displayName.substringAfterLast('.', "")
                    } else ""
                } else ""
            } ?: ""
        }
        else -> uri.path?.substringAfterLast('.', "") ?: ""
    }
}

private fun uriToFile(context: Context, uri: Uri, mediaType: MediaType): File {
    val contentResolver = context.contentResolver

    // 원본 파일명 가져오기 (가능하면)
    val originalFileName = getOriginalFileName(context, uri)

    // 확장자 결정
    val extension = when (mediaType) {
        MediaType.IMAGE -> {
            // MIME 타입 확인해서 정확한 확장자 사용
            when (contentResolver.getType(uri)) {
                "image/png" -> "png"
                "image/gif" -> "gif"
                "image/webp" -> "webp"
                else -> "jpg"
            }
        }
        MediaType.VIDEO -> {
            when (contentResolver.getType(uri)) {
                "video/quicktime" -> "mov"
                "video/x-msvideo" -> "avi"
                "video/x-matroska" -> "mkv"
                else -> "mp4"
            }
        }
        MediaType.AUDIO -> {
            when (contentResolver.getType(uri)) {
                "audio/wav" -> "wav"
                "audio/x-m4a" -> "m4a"
                "audio/aac" -> "aac"
                else -> "mp3"
            }
        }
    }

    val fileName = originalFileName ?: "temp_${System.currentTimeMillis()}.$extension"
    val tempFile = File(context.cacheDir, fileName)

    contentResolver.openInputStream(uri)?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return tempFile
}

private fun getOriginalFileName(context: Context, uri: Uri): String? {
    return when (uri.scheme) {
        "content" -> {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        it.getString(displayNameIndex)
                    } else null
                } else null
            }
        }
        else -> uri.path?.substringAfterLast('/')
    }
}
