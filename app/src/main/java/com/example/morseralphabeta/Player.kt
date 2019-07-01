package com.example.morseralphabeta

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.util.*
import kotlin.concurrent.schedule

// plays sine wave sound
fun audioReapeater(input_duration: Int, release : Long) {

    // sound configuration
    val sampleRate = 22050
    val pitch = 641
    val numSamples: Int = input_duration
    val samples = DoubleArray(numSamples)
    val buffer = ShortArray(numSamples)
    for (i in 0 until numSamples) {
        samples[i] = Math.sin(2.0 * Math.PI * i.toDouble() / (sampleRate / pitch))
        buffer[i] = (samples[i] * java.lang.Short.MAX_VALUE).toShort()
    }

    // new AudioTrack
    val audioTrack = AudioTrack(
        AudioManager.STREAM_MUSIC,
        sampleRate, AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT, buffer.size,
        AudioTrack.MODE_STREAM
    )
    // releases audioTrack from use
    Timer("timer", false).schedule(release) {
        audioTrack.release()
    }

    // plays sound
    audioTrack.write(buffer, 0, buffer.size)
    audioTrack.play()


}