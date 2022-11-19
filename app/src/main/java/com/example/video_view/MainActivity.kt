package com.example.video_view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.example.FFmpegUtil.runffmpeg
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    val startEditText by lazy { findViewById<EditText>(R.id.start_time) }
    val endEditText by lazy { findViewById<EditText>(R.id.end_time) }
    val clipVideo: (String, String, String) -> Unit = { startTime, endTime, filePath ->
        val outputfilepath = filePath.replace(
            Regex("\\.\\w+\$")
        ) { matchResult: MatchResult -> "_output" + matchResult.value }
        val commands = arrayOf(
            "ffmpeg",
            "-ss",
            startTime,
            "-i",
            filePath,
            "-t",
            endTime,
            "-c",
            "copy",
            "-y",
            outputfilepath
        )
        filePath.let {
            if (it.isNotEmpty()) {
                try {
                    runffmpeg(commands)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val intent = Intent(this, VideoProcessingActivity::class.java)
        intent.putExtra(FILE_PATH_KEY, outputfilepath)
        startActivity(intent)
    }

    val fromAlbum = 2
    var filePath: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        val choose: Button = findViewById(R.id.choose)
        super.onCreate(savedInstanceState)
        choose.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, fromAlbum)
        }
        val clip = findViewById<Button>(R.id.clip)
        startEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.isDigitsOnly()!=true)
                    Toast.makeText(this@MainActivity, "Not digit!", Toast.LENGTH_SHORT).show()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        clip.setOnClickListener {
            val start_time = startEditText.text.toString()
            val end_time = endEditText.text.toString()
            thread { clipVideo(start_time, end_time, filePath) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val playerview = findViewById<com.google.android.exoplayer2.ui.PlayerView>(R.id.playerview)
        val player = ExoPlayer.Builder(this).build()
        playerview.setPlayer(player)
        super.onActivityResult(requestCode, resultCode, data)
        val datauri = data?.data
        filePath =
            Environment.getExternalStorageDirectory().getPath() + "/" + datauri?.path.toString()
                .split("primary:")[1]
        when (requestCode) {
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    datauri?.let { uri ->
                        val mediaItem = MediaItem.fromUri(uri)
                        player.addMediaItem(mediaItem)
                        player.prepare()
                        player.setPlayWhenReady(true)
                    }
                }
            }
        }
    }


    companion object {
        const val FILE_PATH_KEY = "filePath"

        init {
            System.loadLibrary("video_view")
        }
    }

}