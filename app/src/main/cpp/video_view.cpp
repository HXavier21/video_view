// Write C++ code here.
// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("video");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("video")
//      }
//    }
#include <jni.h>
#include <android/log.h>

extern "C" {
#include "fftools/ffmpeg.h"


JNIEXPORT void JNICALL
Java_com_example_FFmpegUtil_runffmpeg(JNIEnv *env, jobject thiz, jobjectArray commands) {
    int argc = (*env).GetArrayLength(commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        auto js = (jstring) (*env).GetObjectArrayElement(commands, i);
        argv[i] = (char *) (*env).GetStringUTFChars(js, 0);
    }
    ffmpeg_exec(argc, argv); //这里就是我们的 main 函数
}
}