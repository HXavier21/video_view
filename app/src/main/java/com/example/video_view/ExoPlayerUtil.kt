package com.example.video_view

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

object ExoPlayerUtil {
    fun playTheSelected(playerView: StyledPlayerView, player: ExoPlayer, uri: Uri) {
        playerView.setPlayer(player)
        val mediaItem = MediaItem.fromUri(uri)
        with(player) {
            setMediaItem(mediaItem)
            prepare()
            setPlayWhenReady(true)
        }
    }
}