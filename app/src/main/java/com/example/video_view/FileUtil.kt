package com.example.video_view

import android.net.Uri
import android.os.Environment

object FileUtil {
    fun Uri?.toPath(): String =
        Environment.getExternalStorageDirectory().getPath() + "/" + this?.path.toString()
            .split("primary:")[1]
}