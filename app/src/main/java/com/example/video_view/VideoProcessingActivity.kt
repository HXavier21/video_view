package com.example.video_view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import com.example.FFmpegUtil.runffmpeg
import com.example.video_view.FileUtil.toPath
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread
import kotlin.math.log

private const val TAG = "VideoProcessingActivity"

class VideoProcessingActivity : AppCompatActivity() {
    val fromAudio = 2
    var filePath = ""
    val playerview2: PlayerView by lazy { findViewById(R.id.playerview2) }
    val inputfilepath by lazy { intent.getStringExtra(MainActivity.FILE_PATH_KEY) }
    val replace_bgm: (String, String) -> Unit = { videopath, audiopath ->
        val outputfilepath = videopath.replace(
            Regex("\\.\\w+\$")
        ) { matchResult: MatchResult -> "_bgm" + matchResult.value }
        val commands = arrayOf(
            "ffmpeg",
            "-i",
            videopath,
            "-i",
            audiopath,
            "-c:v",
            "copy",
            "-map",
            "0:v:0",
            "-map",
            "1:a:0",
            "-strict",
            "-2",
            outputfilepath
        )
        audiopath.let {
            if (it.isNotEmpty()) {
                try {
                    runffmpeg(commands)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    val to_gif: (String) -> Unit = { videopath ->
        val outputfilepath = videopath.replace(
            Regex("\\.\\w+\$")
        ) { matchResult: MatchResult -> ".gif" }

        val commands = arrayOf(
            "ffmpeg",
            "-i",
            videopath,
            "-vf",
            "fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse",
            "-loop",
            "0",
            outputfilepath
        )
        videopath.let {
            if (it.isNotEmpty()) {
                try {
                    Log.d(TAG, "start")
                    runffmpeg(commands)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_processing)

        val select: Button = findViewById(R.id.select)
        val replace: Button = findViewById(R.id.replace)
        val gif: Button = findViewById(R.id.gif)
        val playerview: PlayerView = findViewById(R.id.playerview)

        val player = ExoPlayer.Builder(this).build()
        playerview.setPlayer(player)
        val uri = Uri.fromFile(inputfilepath?.let { File(it) })
        val mediaItem = MediaItem.fromUri(uri)
        player.addMediaItem(mediaItem)
        player.prepare()
        player.duration
        player.playWhenReady = true
        select.setOnClickListener {
            player.volume = 0f
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "audio/*"
            startActivityForResult(intent, fromAudio)
        }
        replace.setOnClickListener {
            Log.d(TAG, inputfilepath.toString())
            thread { inputfilepath?.let { replace_bgm(it, filePath) } }
        }
        gif.setOnClickListener {
            Log.d(TAG, "gif" + inputfilepath.toString())

            inputfilepath?.let { path -> to_gif(path) }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val datauri = data?.data
        filePath = datauri.toPath()

        when (requestCode) {
            fromAudio -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    datauri?.let { uri ->
                        val player2 = ExoPlayer.Builder(this).build()
                        playerview2.setPlayer(player2)
                        val mediaItem2 = MediaItem.fromUri(uri)
                        player2.addMediaItem(mediaItem2)
                        player2.prepare()
                        player2.setPlayWhenReady(true)
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