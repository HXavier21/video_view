package com.example

import android.util.Log
import java.lang.Exception

private const val TAG = "FFmpegUtil"
object FFmpegUtil {
    external fun runffmpeg(commands: Array<String>)

    /**
     * @param maxTime duration of the video in player, in seconds
     */
    fun clipVideo(
        startTime: Int,
        endTime: Int,
        filePath: String,
        maxTime: Int,
        onSuccessCallback: (String) -> Unit
    ) {
        val outputFilePath = filePath.replace(
            Regex("\\.\\w+\$")
        ) { matchResult: MatchResult -> "_output" + matchResult.value }

        val time = endTime - startTime

        if (time <= 0 || endTime > maxTime) return

        val commands = arrayOf(
            "ffmpeg",
            "-ss",
            startTime.toString(),
            "-i",
            filePath,
            "-t",
            time.toString(),
            "-c:v",
            "libx264",
            "-c:a",
            "copy",
            "-y",
            outputFilePath
        )
        if (filePath.isNotEmpty()) {
            try {
                runffmpeg(commands)
                onSuccessCallback(outputFilePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun replaceBgm(videoPath:String, audioPath:String){
        val outputFilePath = videoPath.replace(
            Regex("\\.\\w+\$")
        ) { matchResult: MatchResult -> "_bgm" + matchResult.value }
        val commands = arrayOf(
            "ffmpeg",
            "-i",
            videoPath,
            "-i",
            audioPath,
            "-c:v",
            "libx264",
            "-map",
            "0:v:0",
            "-map",
            "1:a:0",
            "-strict",
            "-2",
            outputFilePath
        )
        audioPath.let {
            if (it.isNotEmpty()) {
                try {
                    runffmpeg(commands)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun toGif(videoPath: String) {
        val outputFilePath = videoPath.replace(
            Regex("\\.\\w+\$")
        ) { matchResult: MatchResult -> ".gif" }

        val commands = arrayOf(
            "ffmpeg",
            "-i",
            videoPath,
            "-vf",
            "fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse",
            "-loop",
            "0",
            outputFilePath
        )
        videoPath.let {
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
}