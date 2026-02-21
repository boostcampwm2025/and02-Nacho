package com.andlife.data.util.media

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.andlife.domain.util.MediaFileCopyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaFileCopyManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaFileCopyManager {
    override suspend fun copyPhotoPickerFileToInternal(uriString: String): String? = withContext(Dispatchers.IO) {
        if (!uriString.startsWith("content://")) {
            Log.d(TAG, "Content URI가 아님: $uriString")
            return@withContext null
        }

        val uri = uriString.toUri()

        return@withContext try {
            // 파일 확장자 추출
            val extension = getFileExtension(uri) ?: "tmp"
            val fileName = "media_${System.currentTimeMillis()}.$extension"
            val internalFile = File(context.filesDir, fileName)

            Log.d(TAG, "파일 복사 시작: $uri -> ${internalFile.absolutePath}")

            // 파일 복사
            context.contentResolver.openInputStream(uri)?.use { input ->
                internalFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Log.d(TAG, "파일 복사 완료: ${internalFile.absolutePath} (${internalFile.length()} bytes)")

            // 내부 저장소 파일의 절대 경로 반환 (file:// 스키마 포함)
            "file://${internalFile.absolutePath}"

        } catch (e: Exception) {
            Log.e(TAG, "파일 복사 실패: $uriString", e)
            null
        }
    }

    override suspend fun copyFilesToInternal(uriStrings: List<String>): List<String?> = withContext(Dispatchers.IO) {
        uriStrings.map { uriString ->
            copyPhotoPickerFileToInternal(uriString)
        }
    }

    // URI에서 확장자 추출
    private fun getFileExtension(uri: Uri): String? {
        return try {
            val mimeType = context.contentResolver.getType(uri)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        } catch (e: Exception) {
            Log.w(TAG, "확장자 추출 실패: $uri", e)
            null
        }
    }

    // 24시간보다 오래된 복사 파일들 정리
    override fun cleanupTempFiles(olderThanHours: Int) {
        try {
            val cutoffTime = System.currentTimeMillis() - (olderThanHours * 60 * 60 * 1000)
            val filesDir = context.filesDir

            filesDir.listFiles { file ->
                file.name.startsWith("media_") && file.lastModified() < cutoffTime
            }?.forEach { file ->
                if (file.delete()) {
                    Log.d(TAG, "임시 파일 삭제: ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "임시 파일 정리 실패", e)
        }
    }

    companion object {
        private const val TAG = "MediaFileCopyManager"
    }
}
