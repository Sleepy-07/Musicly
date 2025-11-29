package com.example.music_player.Screens

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music_player.Components.AritistList
import com.example.music_player.Components.SongGridItems
import com.example.music_player.Components.allSongs
import com.example.music_player.Components.audioPlayer
import com.example.music_player.Components.likedSongList
import com.example.music_player.Components.songListItemRest
import com.example.music_player.RoomDatabse.Album
import com.example.music_player.RoomDatabse.Artist
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.SongMetadata
import com.example.music_player.ui.theme.projectBlack
import kotlin.math.log


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongsScreen(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val db = Data.getInstance(context)
    val songs = allSongs
    var artistList = AritistList.current



    val recentlyPlayed by db.inter().getRecentlyPlayedFlow().collectAsState(initial = emptyList())


    // Sync once when the screen starts
    LaunchedEffect(Unit) {
        SyncSongs(context, db)
        likedSongList.clear()
        allSongs.clear()
        artistList.clear()
        allSongs.addAll(db.inter().getData())
        artistList.addAll(allSongs.map { it.artist })
        Log.e("Artitist", "SongsScreen: ${artistList.size} and \ndata ${artistList.map { it }} \nLcoallist = ${AritistList}", )

        likedSongList.addAll(db.inter().getLikedSongs())
    }


    val state = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(horizontal = 10.dp)
            .padding(top = 32.dp)
    ){

        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {

        LazyColumn(state = state) {

            item {
                Text("Music", fontSize = 20.sp, color = Color.White,)

            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recently Played", fontSize = 19.sp, color = Color.White)

                }


            }
            item {
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null
                ) {
                    songListItemRest(recentlyPlayed,"Recently PLayed") { (songId, item) ->
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
            }


            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .background(projectBlack)
                        .padding(vertical = 13.dp)

                ){
                    Text("All Songs", fontSize = 19.sp, color = Color.White)
                }
            }

            itemsIndexed(songs){index,song->

                        SongGridItems(songs,index, onSongClick = {id->
                            db.inter().UpdatePlayCount(System.currentTimeMillis(),id)
                            val index = songs.indexOfFirst { it.songId == id }
                        if (index != -1) {
                            val updated = db.inter().getSingleItem(id) // fetch the updated song
                            allSongs[index] = updated
                        }


                        } )


                }
            item {
                Spacer(modifier = Modifier.height(120.dp))
            }

            }

        }
    }
}


fun FetchLocalSongs(context: Context): List<SongMetadata> {
    val songs = mutableListOf<SongMetadata>()
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    // Updated projection to get more detailed metadata
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK
    )

    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    Log.d("MusicDebug", "Querying MediaStore for local songs...")

    context.contentResolver.query(
        collection,
        projection,
        selection,
        null,
        sortOrder
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val artistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
        val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

        var count = 0
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val title = cursor.getString(titleCol) ?: "Unknown Title"
            val artistName = cursor.getString(artistCol) ?: "Unknown Artist"
            val artistId = cursor.getLong(artistIdCol)
            val albumName = cursor.getString(albumCol) ?: "Unknown Album"
            val albumId = cursor.getLong(albumIdCol)
            val duration = cursor.getLong(durationCol)

            val songUri = ContentUris.withAppendedId(collection, id)
            val albumArtUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                albumId
            )

            songs.add(
                SongMetadata(
                    songId = id,
                    title = title,
                    artistId = artistId,  // Link to artist table
                    albumId = albumId,     // Link to album table
                    duration = duration,
                    uri = songUri,
                    album = albumArtUri,
                    // Default values for new fields
                    playCount = 0,
                    isLiked = false,
                    lastPlayed = 0L,
                    likedTimeStamp = 0L,
                    artist = artistName,
                    albumName = albumName
                )
            )

            Log.d("MusicDebug", "Added: $title | ArtistID: $artistId | AlbumID: $albumId")
            count++
        }
        Log.d("MusicDebug", "Total songs processed: $count")
    } ?: run {
        Log.e("MusicDebug", "Cursor is null - permission issue or no media")
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
        SongMetadata(
            songId = it.songId,
            title = it.title,
            artist = it.artist,
            duration = it.duration,
            uri = it.uri,
            album = it.album,
            artistId = it.artistId,
            albumId = it.albumId,
            albumName = it.albumName,
        )
    }

    val artistMap = mutableMapOf<Long, MutableList<SongMetadata>>()  // Group by artistId
    val albumMap = mutableMapOf<Long, MutableList<SongMetadata>>()   // Group by albumId

    newSongs.forEach { song ->
        artistMap.getOrPut(song.artistId) { mutableListOf() }.add(song)
        albumMap.getOrPut(song.albumId) { mutableListOf() }.add(song)
    }

// Convert to Artist list
    val artists = artistMap.map { (artistId, songs) ->
        Artist(
            artistId = artistId,
            artistName = songs.first().artist,
            numberOfSongs = songs.size,
            numberOfAlbums = songs.map { it.albumId }.distinct().size
        )
    }

// Convert to Album list
    val albums = albumMap.map { (albumId, songs) ->
        Album(
            albumId = albumId,
            albumName = songs.first().albumName,
            artistId = songs.first().artistId,
            albumArtUri = songs.first().album,
            numberOfSongs = songs.size
        )
    }


//    val artist = newSongs.map { Artist(it.artistId, it.artist,it.) }


    albums.forEach {
        db.musicDao().insertAlbum(it)
    }
    artists.forEach {
        db.musicDao().insertArtist(it)
    }
    db.inter().InsertItem(newSongs)

}




