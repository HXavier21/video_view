package com.example.video_view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.FFmpegUtil.replaceBgm
import com.example.FFmpegUtil.runffmpeg
import com.example.FFmpegUtil.toGif
import com.example.video_view.ExoPlayerUtil.playTheSelected
import com.example.video_view.FileUtil.toPath
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

private const val TAG = "VideoProcessingActivity"

class VideoProcessingActivity : AppCompatActivity() {
    val fromAudio = 2
    var filePath = ""
    val inputFilePath by lazy { intent.getStringExtra(MainActivity.FILE_PATH_KEY) }
    val selectButton: Button by lazy { findViewById(R.id.select) }
    val replaceButton: Button by lazy { findViewById(R.id.replace) }
    val gifButton: Button by lazy { findViewById(R.id.gif) }
    val playerView: StyledPlayerView by lazy { findViewById(R.id.playerview) }
    val playerView2: StyledPlayerView by lazy { findViewById(R.id.playerview2) }
    val player: ExoPlayer by lazy { ExoPlayer.Builder(this).build() }
    val player2: ExoPlayer by lazy { ExoPlayer.Builder(this).build() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_processing)
        val uri = Uri.fromFile(inputFilePath?.let { File(it) })
        playTheSelected(playerView, player, uri)
        selectButton.setOnClickListener {
            player.volume = 0f
            startActivityForResult(
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "audio/*"
                },
                fromAudio
            )
        }
        replaceButton.setOnClickListener {
            Log.d(TAG, inputFilePath.toString())
            thread { inputFilePath?.let { replaceBgm(it, filePath) } }
        }
        gifButton.setOnClickListener {
            Log.d(TAG, "gif" + inputFilePath.toString())
            thread { inputFilePath?.let { path -> toGif(path) } }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            val datauri = data.data
            filePath = datauri.toPath()
            when (requestCode) {
                fromAudio -> {
                    if (resultCode == Activity.RESULT_OK) {
                        datauri?.let { uri ->
                            playTheSelected(playerView2, player2, uri)
                        }
                    }
                }
            }
        }
    }

    companion object {
        init {
            System.loadLibrary("video_view")
        }
    }
}


