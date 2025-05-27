package com.example.music_player.Components

import android.content.Context
import android.util.Log
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.SongMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object audioPlayer{

    private var exoplayer : ExoPlayer? = null

    var index by  mutableIntStateOf(0)

    var mediaitem by mutableStateOf<MediaItem?>(null)

    var progress by mutableStateOf(0f) // 0.0 to 1.0
        private set

    var playBackState by mutableStateOf(PlayBackState.IDLE)
        private set

    var playBackMode by mutableStateOf(PlayBackMode.NORMAL)
        internal set

    private var progressJob: Job? = null

    enum class PlayBackState{ IDLE, LOADING, PLAYING, PAUSED, ENDED }
    enum class PlayBackMode { NORMAL, SHUFFLE, REPEAT_ONE, REPEAT_ALL}

    fun intilize(context: Context): ExoPlayer{
        return exoplayer ?: ExoPlayer.Builder(context).build().also {
            exoplayer = it
            setupPlayerListeners(it,context)
        }

    }

    fun setupPlayerListeners(player: ExoPlayer,context: Context){
        player.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(state: Int) {
               playBackState =  when (state) {

                   Player.STATE_IDLE -> PlayBackState.IDLE
                   Player.STATE_BUFFERING -> PlayBackState.LOADING
                   Player.STATE_READY -> if(player.isPlaying) PlayBackState.PLAYING else PlayBackState.PAUSED
                    Player.STATE_ENDED -> {
                       playNext(context)
                        PlayBackState.ENDED
                    }
                   else -> PlayBackState.IDLE
                }

            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
               if(isPlaying){
                   startProgress()
               }
                else{
                    stopProgress()
               }

            }
        })
    }

    fun playSong(song : SongMetadata, context: Context) {
        val player = intilize(context)
        player.apply {
            val mediaItem = MediaItem.fromUri(song.uri)
            setMediaItem(mediaItem)
            prepare()
            play()
            startProgress()
        }
    }


    fun playSongPlayList( songs : List<SongMetadata>, startIndex : Int = 0 ,context: Context){
        val player = intilize( context)
        currentsonglist = songs
        index= startIndex

        val mediaitem = MediaItem.fromUri(songs[startIndex].uri)

        player.setMediaItem(mediaitem)
        player.seekTo(startIndex,0L)
        player.prepare()
        player.play()
        currentsong = songs[startIndex]
        startProgress()

    }


    private fun startProgress(){
        stopProgress()
        progressJob = CoroutineScope(Dispatchers.Main).launch{
            while (true){
                exoplayer?.let {
                    val duration = it.duration.toFloat()

                    if(duration > 0){
                        progress = it.currentPosition.toFloat() / duration

                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgress() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playNext(context: Context){
        val db = Data.getInstance(context)
        exoplayer?.let {player ->
            when(playBackMode){

                PlayBackMode.SHUFFLE -> playRandomeSong(context)

                PlayBackMode.REPEAT_ONE -> {
                    player.seekTo(player.currentMediaItemIndex,0L)
                    db.inter().UpdatePlayCount(System.currentTimeMillis(),currentsonglist[index].songId)
                }
               else -> {
                    if(hasNextMediaItem()){

                        index++
                        Log.e("Check Next", "playNext: index = $index", )
                        if(index > currentsonglist.size -1){
                            index = 0
                        }

                        Log.e("Nect Button after changing  indexed Next", "playNext: index = $index and playlist size = ${currentsonglist}.size}", )
                        db.inter().UpdatePlayCount(System.currentTimeMillis(),currentsonglist[index].songId)
                        playSong(currentsonglist[index],context)

                        currentsong = currentsonglist[index]
                        mediaitem = MediaItem.fromUri(currentsong!!.uri)
                        progress = 0f

                    }

                    else if(

                        playBackMode == PlayBackMode.REPEAT_ALL

                    ){

                        player.seekTo(0,0L)
                    }
                }
            }
        }
    }

    private fun hasNextMediaItem(): Boolean {
        return index < currentsonglist.size -1

    }
    private fun hasPreviousMediaItem(): Boolean {
        return  index >0
    }


    fun playPrevious(context: Context){
        exoplayer?.let { player->
            when(playBackMode){

                PlayBackMode.SHUFFLE -> playRandomeSong(context)

                PlayBackMode.REPEAT_ONE -> {
                    player.seekTo(player.currentMediaItemIndex,0L)
                }
                else -> {
                    if(hasPreviousMediaItem()){
                        index--
                        if(index < 0){
                            index = 0
                        }
                        Log.e("Nect Button after changing  indexed Prev", "playNext: index = $index and playlist size = ${currentsonglist.size}", )

                        val db = Data.getInstance(context)

                        db.inter().UpdatePlayCount(System.currentTimeMillis(),currentsonglist[index].songId)

                        playSong(currentsonglist[index],context)

                        currentsong = currentsonglist[index]
                        mediaitem = MediaItem.fromUri(currentsong!!.uri)
                        progress = 0f
                    }
                    else if(
                        playBackMode == PlayBackMode.REPEAT_ALL
                    ){
                        player.seekTo(0,0L)
                    }
                }
            }
        }
    }


    fun seekTo(position: Float){
        exoplayer?.let {
            val durationMs = it.duration.coerceAtLeast(0L)
            it.seekTo((position * durationMs).toLong())
        }
    }

    fun getCurrentPostion() : Int{
        return exoplayer?.currentMediaItemIndex!!

    }

    fun isPlaying() : Boolean{
        if(playBackState== PlayBackState.PLAYING) return true else return false
    }

    private fun playRandomeSong(context: Context){
        currentsonglist.takeIf { it.size> 1  }?.let { playlist->
            val currentIndex= exoplayer?.currentMediaItemIndex ?: 0
            var newIndex = currentIndex
            while (newIndex == currentIndex && playlist.size > 1){
                newIndex = (0 until playlist.size).random()
            }
            playIndex(newIndex,context)
        }
    }

    fun toggelShuffle(){
        playBackMode = when(playBackMode){
            PlayBackMode.SHUFFLE -> PlayBackMode.NORMAL
            else -> PlayBackMode.SHUFFLE
        }
        exoplayer?.shuffleModeEnabled = (playBackMode == PlayBackMode.SHUFFLE)
    }

    fun toggelRepeat(){
        playBackMode = when (playBackMode){
            PlayBackMode.REPEAT_ONE -> PlayBackMode.REPEAT_ALL
            PlayBackMode.REPEAT_ALL -> PlayBackMode.NORMAL
            else -> PlayBackMode.REPEAT_ONE
        }
        exoplayer?.let {
            it.repeatMode = when (playBackMode){

                PlayBackMode.REPEAT_ONE -> Player.REPEAT_MODE_ONE
                PlayBackMode.REPEAT_ALL -> Player.REPEAT_MODE_ALL
                else -> Player.REPEAT_MODE_OFF
            }
        }

    }

    fun playIndex(ind : Int,context: Context){
        index = ind

        val db = Data.getInstance(context)
        db.inter().UpdatePlayCount(System.currentTimeMillis(),currentsonglist[index].songId)
        playSong(currentsonglist[index],context)

        currentsong = currentsonglist[index]
        mediaitem = MediaItem.fromUri(currentsong!!.uri)
        progress = 0f
    }

    fun getPlayer() : ExoPlayer{
        return exoplayer!!
    }

    fun togglePause(){
        exoplayer?.let {player->

            playBackState = when(playBackState){
                PlayBackState.PLAYING -> PlayBackState.PAUSED
                else -> PlayBackState.PLAYING
            }

            if(playBackState == PlayBackState.PLAYING) exoplayer?.play() else exoplayer?.pause()

        }
    }

    fun release(){
        stopProgress()
        exoplayer?.release()
        exoplayer = null
        currentsong = null
        currentsonglist = emptyList()
        playBackState = PlayBackState.IDLE
    }
}