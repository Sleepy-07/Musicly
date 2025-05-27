package com.example.music_player.Screens

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music_player.Components.PlayerBar
import com.example.music_player.Components.allSongs
import com.example.music_player.Components.audioPlayer
import com.example.music_player.Components.currentsong
import com.example.music_player.Components.likedSongList
import com.example.music_player.Components.showtime
import com.example.music_player.Components.songListItemHome
import com.example.music_player.Components.songListItemRest
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.SongMetadata
import kotlin.math.log


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongsScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val db = Data.getInstance(context)
    val songs = allSongs

    val recentlyPlayed by db.inter().getRecentlyPlayedFlow().collectAsState(initial = emptyList())


    // Sync once when the screen starts
    LaunchedEffect(Unit) {
        SyncSongs(context, db)
        likedSongList.clear()
        allSongs.clear()
        allSongs.addAll(db.inter().getData())
        likedSongList.addAll(db.inter().getLikedSongs())
    }

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(horizontal = 10.dp)
            .padding(paddingValues)
    ) {
        Text("Music", fontSize = 20.sp, color = Color.White,)


        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recently Played", fontSize = 19.sp, color = Color.White)
            Text("View All", fontSize = 13.sp, color = Color.White)

        }


        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            songListItemRest(recentlyPlayed) { (songId, item) ->
                Log.e("Song Id", "SongsScreen:  $songId",)
                db.inter().UpdatePlayCount(System.currentTimeMillis(), songId)
                // Update the item in the UI
                val index = songs.indexOfFirst { it.songId == songId }
                if (index != -1) {
                    val updated = db.inter().getSingleItem(songId) // fetch the updated song
                    allSongs[index] = updated
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 20.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("All Songs", fontSize = 19.sp, color = Color.White)
        }
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            songListItemHome(songs, onclick = { (songId, item) ->
                Log.e("Song Id", "SongsScreen:  $songId",)
                db.inter().UpdatePlayCount(System.currentTimeMillis(), songId)
                // Update the item in the UI
                val index = songs.indexOfFirst { it.songId == songId }
                if (index != -1) {
                    val updated = db.inter().getSingleItem(songId) // fetch the updated song
                    allSongs[index] = updated
                }
            })
        }
    }


}
fun FetchLocalSongs(context: Context): List<SongMetadata> {
    val songs = mutableListOf<SongMetadata>()
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ALBUM_ID
    )
    val selection = null
    val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

    Log.d("MusicDebug", "Querying MediaStore for local songs...")

    val cursor = context.contentResolver.query(
        collection, projection, selection, null, sortOrder
    )

    if (cursor == null) {
        Log.e("MusicDebug", "Cursor is null — permission denied or media unavailable")
        return emptyList()
    }

    cursor.use {
        val idIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val album = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

        var count = 0
        while (it.moveToNext()) {
            val id = it.getLong(idIndex)
            val title = it.getString(titleIndex)
            val artist = it.getString(artistIndex)
            val duration = it.getLong(durationIndex)
            val uri = ContentUris.withAppendedId(collection, id)
            val album = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                it.getLong(album)
            )

            songs.add(SongMetadata(
                songId = id, title = title, artist =  artist, duration = duration,uri = uri,album = album))
            Log.d("MusicDebug", "Found song: $title by $artist ($duration ms)")
            count++
        }

        Log.d("MusicDebug", "Total songs fetched: $count")
    }

    return songs
}

fun PlayLocalSong(song : List<SongMetadata>,index: Int, context: Context) {
    Log.e("Song List", "PlayLocalSong: size = ${song.size}\nsongs ${song.map { it.title }} ", )

    audioPlayer.playSongPlayList(song,index,context)
}



fun SyncSongs(context: Context, db : Data){
    val mediaKeys = db.inter().getData().map { it.songId }.toSet()
    val fetchsongs = FetchLocalSongs(context)
    val newSongs = fetchsongs.filter { it.songId !in mediaKeys }.map {
        SongMetadata( songId = it.songId, title = it.title, artist =  it.artist, duration = it.duration,uri = it.uri,album = it.album)
    }

    db.inter().InsertItem(newSongs)
}




