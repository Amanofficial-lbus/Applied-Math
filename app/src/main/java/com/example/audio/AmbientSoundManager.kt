package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.sin

enum class AmbientSoundType(val displayName: String, val description: String) {
    BINAURAL_ALPHA("Binaural Alpha Waves (40Hz)", "Deep focus entrainment with 40Hz alpha rhythm"),
    BROWN_NOISE("Deep Brown Noise", "Low-frequency rumble for intense concentration"),
    RAIN_SOUND("Gentle Rain & Pink Noise", "Soothing rainfall pattern to mask room distractions"),
    QUIET_LIBRARY("Quiet Library Atmosphere", "Gentle resonant acoustic warmth"),
    OFF("Ambient Sound Off", "Silent mode")
}

class AmbientSoundManager {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    var currentSound: AmbientSoundType = AmbientSoundType.BINAURAL_ALPHA
        private set

    var isPlaying: Boolean = false
        private set

    fun setSoundType(type: AmbientSoundType) {
        currentSound = type
        if (isPlaying) {
            stop()
            if (type != AmbientSoundType.OFF) {
                play()
            }
        }
    }

    fun play() {
        if (currentSound == AmbientSoundType.OFF) return
        stop()

        try {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096) * 2

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (track.state != AudioTrack.STATE_INITIALIZED) {
                track.release()
                return
            }

            audioTrack = track
            track.play()
            isPlaying = true

            val soundType = currentSound
            playbackJob = scope.launch {
                val random = Random()
                val buffer = ShortArray(2048)
                var phase1 = 0.0
                var phase2 = 0.0
                var lastOut = 0.0

                while (isActive && isPlaying) {
                    for (i in buffer.indices) {
                        when (soundType) {
                            AmbientSoundType.BINAURAL_ALPHA -> {
                                // 200 Hz carrier + 40 Hz modulation beat
                                val s1 = sin(phase1)
                                val s2 = sin(phase2)
                                phase1 += 2.0 * Math.PI * 200.0 / sampleRate
                                phase2 += 2.0 * Math.PI * 240.0 / sampleRate
                                val sample = ((s1 + s2) * 0.5 * 4000).toInt().coerceIn(-32767, 32767)
                                buffer[i] = sample.toShort()
                            }
                            AmbientSoundType.BROWN_NOISE -> {
                                // Low-pass filtered noise
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                lastOut = (lastOut + (0.02 * white)) / 1.02
                                val sample = (lastOut * 18000).toInt().coerceIn(-32767, 32767)
                                buffer[i] = sample.toShort()
                            }
                            AmbientSoundType.RAIN_SOUND -> {
                                // Pink noise with gentle random rain drops
                                val white = (random.nextDouble() * 2.0 - 1.0)
                                lastOut = (lastOut * 0.95) + (white * 0.05)
                                val rainDrop = if (random.nextInt(400) == 0) (random.nextDouble() * 0.4) else 0.0
                                val sample = ((lastOut + rainDrop) * 8000).toInt().coerceIn(-32767, 32767)
                                buffer[i] = sample.toShort()
                            }
                            AmbientSoundType.QUIET_LIBRARY -> {
                                // Very gentle 120Hz warmth tone + subtle low hum
                                val s = sin(phase1)
                                phase1 += 2.0 * Math.PI * 128.0 / sampleRate
                                val sample = (s * 2500).toInt().coerceIn(-32767, 32767)
                                buffer[i] = sample.toShort()
                            }
                            AmbientSoundType.OFF -> {
                                buffer[i] = 0
                            }
                        }
                    }
                    try {
                        track.write(buffer, 0, buffer.size)
                    } catch (_: Exception) {
                        break
                    }
                }
            }
        } catch (_: Exception) {
            isPlaying = false
            audioTrack = null
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
