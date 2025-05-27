package com.example.music_player.BottomSheetDialogs

import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.music_player.Components.allSongs
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.PlaylistEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun addSongPlayList(id : Long, ondismiss : () -> Unit) {

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,

    )

    val db = Data.getInstance(context)
    val songs = allSongs

    val existingIds = remember { mutableStateListOf<Long>() }
    LaunchedEffect(id) {
        val playlistWithSongs = db.playListDao().getPlaylistWithSongs(id)
        existingIds.clear()
        existingIds.addAll(playlistWithSongs.songs.map { it.songId })
    }


    Log.e("My Songs", "addSongPlayList:songs = $songs and\n allsongs $allSongs ", )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = ondismiss,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .padding(horizontal = 10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(1f),
            ) {
                IconButton(
                    onClick = {
                        ondismiss
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),"",modifier = Modifier.rotate(90f)
                    )
                }
                    Text("My Songs", color = androidx.compose.ui.graphics.Color.White, fontSize =  17.sp, modifier = Modifier.align(
                        Alignment.Center))
            }
        }

        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ){
            LazyColumn(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(songs){item->
                    val isAdd = item.songId in existingIds


                        Row(modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically) {

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(item.album)
                                .error(R.drawable.default_album_art)
                                .fallback(R.drawable.default_album_art)
                                .placeholder(R.drawable.default_album_art)
                                .build(),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))

                        )

                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp).weight(1f)
                        ) {
                            Text(item.title, fontSize = 15.sp, maxLines = 1)
                            Text(item.artist, fontSize = 12.sp, maxLines = 1, color = androidx.compose.ui.graphics.Color.Gray)

                        }
                        IconButton(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    if(isAdd){
                                        db.playListDao().removeSongFromPlaylist(PlaylistEntry(id,item.songId))
                                        existingIds.remove(item.songId)

                                    }
                                    else{

                                    db.playListDao().InsertSongIntoPlayList(PlaylistEntry(id,item.songId))
                                        existingIds.add(item.songId)
                                    }
                                }

                            },
                        ) {
                            Image(painterResource(
                                if(isAdd) R.drawable.itemadd else R.drawable.add2
                            ),"")
                        }


                    }


                }
            }
        }
    }



}
