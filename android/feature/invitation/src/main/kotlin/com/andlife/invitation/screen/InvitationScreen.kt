package com.andlife.invitation.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
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
import com.andlife.domain.model.MediaFile
import com.andlife.domain.model.MediaType

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
            val mediaFile = createMediaFile(context, it)
            if (mediaFile != null) {
                viewModel.uploadSingleMedia(mediaFile)
            } else {
                Log.e("SampleUpload", "파일 정보를 가져올 수 없습니다")
            }
        }
    }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val mediaFiles = uris.mapNotNull { uri ->
                createMediaFile(context, uri)
            }

            if (mediaFiles.isNotEmpty()) {
                viewModel.uploadMultipleMedia(mediaFiles)
            } else {
                Log.e("SampleUpload", "유효한 파일이 없습니다")
            }
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

// Uri로부터 MediaFile 객체 생성
private fun createMediaFile(context: Context, uri: Uri): MediaFile? {
    val (fileName, fileSize) = getFileInfoFromUri(context, uri) ?: return null
    val mediaType = getMediaTypeFromUri(context, uri)

    return MediaFile(
        uriString = uri.toString(),
        mediaType = mediaType,
        fileName = fileName,
        fileSize = fileSize
    )
}

// Uri로부터 파일 이름과 크기 가져오기
private fun getFileInfoFromUri(context: Context, uri: Uri): Pair<String, Long>? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

        if (cursor.moveToFirst() && nameIndex != -1 && sizeIndex != -1) {
            val name = cursor.getString(nameIndex)
            val size = cursor.getLong(sizeIndex)
            name to size
        } else null
    }
}

// Uri로부터 MediaType 결정
private fun getMediaTypeFromUri(context: Context, uri: Uri): MediaType {
    val mimeType = context.contentResolver.getType(uri)

    return when {
        mimeType?.startsWith("image/") == true -> MediaType.IMAGE
        mimeType?.startsWith("video/") == true -> MediaType.VIDEO
        mimeType?.startsWith("audio/") == true -> MediaType.AUDIO
        else -> {
            val extension = getExtensionFromUri(context, uri)

            when (extension.lowercase()) {
                "jpg", "jpeg", "png", "gif", "webp" -> MediaType.IMAGE
                "mp4", "mov", "avi", "mkv" -> MediaType.VIDEO
                "mp3", "wav", "m4a", "aac" -> MediaType.AUDIO
                else -> MediaType.IMAGE
            }
        }
    }
}

// Uri로부터 파일 확장자 가져오기
private fun getExtensionFromUri(context: Context, uri: Uri): String {
    return when (uri.scheme) {
        "content" -> {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        val displayName = cursor.getString(displayNameIndex)
                        displayName.substringAfterLast('.', "")
                    } else ""
                } else ""
            } ?: ""
        }
        else -> uri.path?.substringAfterLast('.', "") ?: ""
    }
}
