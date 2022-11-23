package com.example.video_view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.FFmpegUtil.clipVideo
import com.example.FFmpegUtil.runffmpeg
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.lang.Exception
import kotlin.concurrent.thread

private const val TAG = "MainActivity"

private fun EditText.getIntOrNull(): Int? = this.text.toString().toIntOrNull()
class MainActivity : AppCompatActivity() {
    val startEditText by lazy { findViewById<EditText>(R.id.start_time) }
    val endEditText by lazy { findViewById<EditText>(R.id.end_time) }
    val playerview by lazy { findViewById<StyledPlayerView>(R.id.playerview) }
    val player: ExoPlayer by lazy { ExoPlayer.Builder(this).build() }
    val fromAlbum = 2
    var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        val choose: Button = findViewById(R.id.choose)
        super.onCreate(savedInstanceState)
        playerview.setPlayer(player)
        choose.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }, fromAlbum)
        }
        val clip = findViewById<Button>(R.id.clip)
        clip.setOnClickListener {
            val start = startEditText.getIntOrNull()
            val end = endEditText.getIntOrNull()
            val maxTime = (player?.duration ?: 0).toInt() / 1000
            if (end != null && start != null) {
                thread {
                    clipVideo(
                        startTime = start,
                        endTime = end,
                        filePath = filePath,
                        maxTime = maxTime,
                        onSuccessCallback = {
                            val intent = Intent(this, VideoProcessingActivity::class.java)
                            intent.putExtra(FILE_PATH_KEY, it)
                            startActivity(intent)
                        })
                }
            } else {
                Toast.makeText(this, "NaN", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, data.toString())
        data?.let {
            val datauri = data.data
            filePath =
                Environment.getExternalStorageDirectory().getPath() + "/" + datauri?.path.toString()
                    .split("primary:")[1]
            when (requestCode) {
                fromAlbum -> {
                    if (resultCode == Activity.RESULT_OK) {
                        datauri?.let { uri ->
                            ExoPlayerUtil.playTheSelected(playerview, player, uri)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val FILE_PATH_KEY = "filePath"
        init {
            System.loadLibrary("x264")
            System.loadLibrary("video_view")
        }
    }
}