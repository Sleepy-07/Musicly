package com.example.music_player.Screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import coil.compose.AsyncImage
import com.example.music_player.Components.AritistList
import com.example.music_player.Components.LocalAppNavController
import com.example.music_player.Components.createshowsheet
import com.example.music_player.Components.likedSongList
import com.example.music_player.Components.songListItemRest
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.Playlist
import com.example.music_player.RoomDatabse.PlaylistWithSongs
import com.example.music_player.RoomDatabse.SongMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.log

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun libraryScreen(innerpadding : PaddingValues) {
    val context = LocalContext.current
    val db = Data.getInstance(context)
    val navController = LocalAppNavController.current

    val playlistsFromDb by db.playListDao().getAllPlaylistsFlow().collectAsState(initial = emptyList())

    val recentlyLiked by db.inter().getRecentlyLikedFlow().collectAsState(initial = emptyList())

    Log.e("PlayListFromdb", "libraryScreen: ${playlistsFromDb.map { it.playlistname }} ", )

    Box(modifier = Modifier.fillMaxSize(1f).padding(top = 32.dp).padding(horizontal = 10.dp)){

        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
        LazyColumn(modifier = Modifier.padding(top = 40.dp)) {
            items(playlistsFromDb){item->

                Log.e("Check Playlist", "PlaylistUI: playlistname ${item.playlistname}  \nplaylistid ${item.playlistId} \nplaylist logo${item.playlistlogo} ", )

                PlaylistSection(item,recentlyLiked,db,navController)
            }
            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }

        Row(modifier = Modifier.fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painter = painterResource(R.drawable.music),"", tint = Color(0xFF0D99FF)
            )
            Text("My Library", fontSize = 30.sp, color = Color(0xFF0D99FF), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    createshowsheet = true
                }
            ) {
                Image(painterResource(R.drawable.add),"", colorFilter = ColorFilter.tint(Color.White))
            }

        }




}
}

@Composable
fun PlaylistSection(item: Playlist, recentlyLiked: List<SongMetadata>, db: Data,navController: NavController) {


    Log.e("PlayListFromdb in Section", "libraryScreen: $item ", )


        val playlistWithSongs by db.playListDao()
            .getPlaylistWithSongs(item.playlistId)
            .collectAsState(initial = PlaylistWithSongs(item, emptyList()))
        PlaylistUI(item,playlistWithSongs.songs,navController)
    }


@Composable
fun PlaylistUI(playlist: Playlist,songs: List<SongMetadata>,navController: NavController) {


    Log.e("Check Playlist", "PlaylistUI: playlistname ${playlist.playlistname}  \nplaylistid ${playlist.playlistId} \nplaylist logo${playlist.playlistlogo} ", )


    val name = playlist.playlistname

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, fontSize = 19.sp, color = Color.White)
        Text("Edit", fontSize = 19.sp, color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.clickable{
                navController.navigate("EditPlaylist/${playlist.playlistId}")

            })

    }


    Log.e("Check Playlist clicked", "PlaylistUI: playlistname ${playlist.playlistname}  \nplaylistid ${playlist.playlistId} \nplaylist logo${playlist.playlistlogo} ", )
    songListItemRest(songs,name, onSongClick = { (id, item) -> })
}
