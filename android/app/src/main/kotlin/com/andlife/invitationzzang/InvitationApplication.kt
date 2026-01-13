package com.andlife.invitationzzang

import android.app.Application
import com.andlife.ui.player.VideoPlayerPool
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InvitationApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        VideoPlayerPool.initializeCache(this)
    }
}
