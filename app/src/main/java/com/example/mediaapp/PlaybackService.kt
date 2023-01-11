package com.example.mediaapp

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {

    private lateinit var session : MediaSession
    private lateinit var player : ExoPlayer


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return session
    }


    override fun onCreate() {
        super.onCreate()

        MediaList.initialize(assets)

        player = ExoPlayer
            .Builder(this)
            .build()

        session = MediaSession
            .Builder(this, player)
            .build()

        MediaList.mediaItems.forEach { item ->
            player.addMediaItem(item)
        }
    }

    override fun onDestroy() {
        player.release()
        session.release()
        super.onDestroy()
    }
}