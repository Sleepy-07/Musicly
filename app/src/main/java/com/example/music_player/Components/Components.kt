package com.example.music_player.Components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.music_player.RoomDatabse.AlbumWithSongs
import com.example.music_player.RoomDatabse.SongMetadata

val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

val AritistList = staticCompositionLocalOf{
    mutableStateListOf<String>()
}

var currenetplaylistname by  mutableStateOf("")



var isPlaying by mutableStateOf(true)



var allSongs = mutableStateListOf<SongMetadata>()
var currentsonglist by mutableStateOf< List<SongMetadata>>(emptyList())

var likedSongList = mutableStateListOf<SongMetadata>()
//var CurrentRuningPlayList by mutableStateOf< List<SongMetadata>>(emptyList())

var createshowsheet by mutableStateOf(false)
var customdialog by mutableStateOf(false)

var showsheet by   mutableStateOf(false)
var addSongSheet by   mutableStateOf(false)

var currentsong by mutableStateOf<SongMetadata?>(null)

var songduration by mutableStateOf(0L)
var songcurrenttime by mutableStateOf(0f)

var ShuffleList by mutableStateOf<List<SongMetadata>>(emptyList())
var OriginalList by mutableStateOf<List<SongMetadata>>(emptyList())

fun getTotalDuration(duration: Long) : String{
    val duration = formatDuration(duration)
    return duration

}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}


