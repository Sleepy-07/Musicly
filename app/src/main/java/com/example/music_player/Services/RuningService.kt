package com.example.music_player.Services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.session.MediaSession
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import com.example.music_player.Components.audioPlayer
import com.example.music_player.Components.currentsong
import com.example.music_player.Components.isPlaying
import com.example.music_player.R

class MusicServices : Service(){
    private lateinit var mediaSession: MediaSessionCompat


    override fun onCreate() {
        super.onCreate()

        if(audioPlayer.isPlaying()){
            isPlaying = true
        }
        else{
            isPlaying = false
        }

        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            isActive = true
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            setCallback(object : MediaSessionCompat.Callback() {

                override fun onPlay() {
                    Log.d("MediaButton", "onPlay triggered")
                    audioPlayer.togglePause(this@MusicServices)
                    updateNotification()
                }

                override fun onPause() {
                    Log.d("MediaButton", "onPause triggered")
                    audioPlayer.togglePause(this@MusicServices)
                    updateNotification()
                }

                override fun onSkipToNext() {
                    Log.d("MediaButton", "onSkipToNext triggered")
                    audioPlayer.playNext(this@MusicServices)
                    updateNotification()
                }

                override fun onSkipToPrevious() {
                    Log.d("MediaButton", "onSkipToPrevious triggered")
                    audioPlayer.playPrevious(this@MusicServices)
                    updateNotification()
                }

                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    val event = mediaButtonEvent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    if (event != null && event.action == KeyEvent.ACTION_DOWN) {
                        when (event.keyCode) {
                            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                                Log.d("MediaButton", "KEYCODE_MEDIA_PLAY received")
//                                isPlaying = false
                                audioPlayer.togglePause(this@MusicServices)
                                updateNotification()
                            }
                            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                                Log.d("MediaButton", "KEYCODE_MEDIA_PAUSE received")
                                isPlaying = true

                                audioPlayer.togglePause(this@MusicServices)
                                updateNotification()

                            }
                            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                                Log.d("MediaButton", "KEYCODE_MEDIA_NEXT received")
                                audioPlayer.playNext(this@MusicServices)
                                updateNotification()

                            }
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                                Log.d("MediaButton", "KEYCODE_MEDIA_PREVIOUS received")
                                audioPlayer.playPrevious(this@MusicServices)
                                updateNotification()

                            }
                            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> Log.d("MediaButton", "KEYCODE_MEDIA_PLAY_PAUSE received")
                            KeyEvent.KEYCODE_HEADSETHOOK -> Log.d("MediaButton", "KEYCODE_HEADSETHOOK received (headset button)")
                            else -> Log.d("MediaButton", "Other key received: ${event.keyCode}")
                        }
                    }
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }
            })
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(bluetoothReceiver, filter)
    }



    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession,intent)

        when (intent?.action) {
            "PLAY" -> {
                audioPlayer.togglePause(this)
                updateNotification() // Update when play/pause changes
            }
            "NEXT" -> {
                audioPlayer.playNext(this)
                updateNotification() // Update when song changes
            }
            "PREV" -> {
                audioPlayer.playPrevious(this)
                updateNotification() // Update when song changes
            }
            "CLOSE" -> {
                stopForeground(true)
                stopSelf()
                audioPlayer.playBackState = audioPlayer.PlayBackState.PAUSED
                audioPlayer.getPlayer().pause()
//                updateNotification()

            }
            "UPDATE" -> {
                updateNotification()
            }
        }

        // Only create new notification if service is starting fresh
        if (intent?.action == null) {
            val notification = createNotification()
            startForeground(1, notification)
        }



        return START_STICKY
    }


    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        // Make sure the notification channel exists
        createNotificationChannel()

        val song = currentsong
        val songTitle = song?.title ?: "Unknown Title"
        val artist = song?.artist ?: "Unknown Artist"

        val duration = audioPlayer.getPlayer().duration
        val currentPostion = audioPlayer.getPlayer().duration

        val art = try {
            song?.album?.let { uri ->
                contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                }
            }
        } catch (e: Exception) {
            null
        }

        val playIntent = getAction("PLAY")
        val nextIntent = getAction("NEXT")
        val prevIntent = getAction("PREV")
        val closeIntent = getAction("CLOSE")



        return NotificationCompat.Builder(this, "music_channel")
            .setContentTitle(songTitle)
            .setContentText(artist)
            .setSmallIcon(R.mipmap.app_logo) // use a proper small icon
            .setLargeIcon(art ?: BitmapFactory.decodeResource(resources, R.drawable.default_album_art)) // fallback
            .addAction(R.drawable.pre, "Prev", prevIntent)
            .addAction(if(isPlaying) R.drawable.pause else R.drawable.play  , "Pause", playIntent)
            .addAction(R.drawable.next, "Next", nextIntent)
            .addAction(R.drawable.close, "Close", closeIntent)
            .setColorized(false)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }


    private fun getAction(action: String): PendingIntent {
        val intent = Intent(this, MusicServices::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, action.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }


    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    Log.d("BluetoothReceiver", "Bluetooth device connected")
                    audioPlayer.togglePause(this@MusicServices)
                    updateNotification()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Log.d("BluetoothReceiver", "Bluetooth device disconnected")
                    audioPlayer.pause()
                    updateNotification()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        audioPlayer.getPlayer().pause()
        unregisterReceiver(bluetoothReceiver)
    }


}

