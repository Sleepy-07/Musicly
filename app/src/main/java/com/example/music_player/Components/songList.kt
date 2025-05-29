package com.example.music_player.Components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.music_player.BottomItem
import com.example.music_player.R
import com.example.music_player.RoomDatabse.SongMetadata
import kotlin.time.Duration


var allSongs = mutableStateListOf<SongMetadata>()
var currentsonglist by mutableStateOf< List<SongMetadata>>(emptyList())

var likedSongList = mutableStateListOf<SongMetadata>()
var CurrentRuningPlayList by mutableStateOf< List<SongMetadata>>(emptyList())

var createshowsheet by mutableStateOf(false)
var customdialog by mutableStateOf(false)

var showsheet by   mutableStateOf(false)
var addSongSheet by   mutableStateOf(false)






val bottomlist = listOf(
    BottomItem(R.drawable.home,"Home"),
    BottomItem(R.drawable.search,"Search"),
    BottomItem(R.drawable.lib,"Library"),
    BottomItem(R.drawable.add,"Create"),
    )

var currentsong by mutableStateOf<SongMetadata?>(null)

var songduration by mutableStateOf(0L)
var songcurrenttime by mutableStateOf(0f)
var songtext by mutableStateOf("0:00")


@Composable
fun showtime(modifier: Modifier = Modifier) {


    val currentTimeMs = (songcurrenttime * songduration).toLong()
    val currentFormatted = formatDuration(currentTimeMs)
    val durFormatted = formatDuration(songduration)

    Log.e("Time", "showtime: dur = $durFormatted and time = $currentFormatted, and perrefresh = $songcurrenttime")

    Text("$currentFormatted / $durFormatted",
        fontSize = 12.sp,
        color = Color.White,
        modifier = modifier
    )
}


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
fun formatDurationAccurate(durationFloat: Float): String {
    val totalSeconds = durationFloat.toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = ((durationFloat - totalSeconds) * 10).toInt()
    return "%d:%02d.%d".format(minutes, seconds, tenths)
}


