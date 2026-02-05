package com.andlife.media

interface StoryMediaPlayerPool {
    // 플레이어 획득 및 미리 준비
    fun acquirePlayer(index: Int, url: String): StoryMediaPlayer

    // 비디오 프리캐싱 (다음 영상들을 미리 1MB만큼 다운로드)
    fun precacheVideos(urls: List<String>)

    // 재생 및 상태 관리
    fun play(index: Int)
    fun pause(index: Int)
    fun pauseAll()
    fun resumeLastPlayed(index: Int)

    // 리소스 관리
    fun resetPool()
    fun releaseAll()
}
