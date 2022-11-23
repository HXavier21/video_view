
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("video_view")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             video_view

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        video_view.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.
add_library(avcodec

        SHARED

        IMPORTED)

add_library(avfilter

        SHARED

        IMPORTED)

add_library(avutil

        SHARED

        IMPORTED)

add_library(avformat

        SHARED

        IMPORTED)

add_library(swscale

        SHARED

        IMPORTED)

add_library(swresample

        SHARED

        IMPORTED)

add_library(postproc

        SHARED

        IMPORTED)

add_library(avdevice

        SHARED

        IMPORTED)

add_library(x264

        SHARED

        IMPORTED)

add_library(ffmpeg
        SHARED
        fftools/cmdutils.c
        fftools/ffmpeg.c
        fftools/ffmpeg_filter.c
        fftools/ffmpeg_hw.c
        fftools/ffmpeg_opt.c
        fftools/ffmpeg_demux.c
        fftools/ffmpeg_mux.c
        fftools/ffmpeg_mux_init.c
        fftools/opt_common.c
        fftools/sync_queue.c
        fftools/thread_queue.c
        fftools/objpool.c)

set_target_properties(x264

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libx264.so)

set_target_properties(avcodec

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libavcodec.so)

set_target_properties(avfilter

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libavfilter.so)

set_target_properties(avformat

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libavformat.so)

set_target_properties(avdevice

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libavdevice.so)

set_target_properties(avutil

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libavutil.so)

set_target_properties(swresample

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libswresample.so)

set_target_properties(swscale

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libswscale.so)

set_target_properties(postproc

        PROPERTIES IMPORTED_LOCATION

        ${CMAKE_SOURCE_DIR}/lib/${CMAKE_ANDROID_ARCH_ABI}/libpostproc.so)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

include_directories(
        ${CMAKE_SOURCE_DIR}/include)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        video_view
        ffmpeg)
target_link_libraries(
        ffmpeg
        avfilter
        avformat
        avdevice
        avcodec
        avutil
        swresample
        swscale
        postproc
        x264
        ${log-lib} )