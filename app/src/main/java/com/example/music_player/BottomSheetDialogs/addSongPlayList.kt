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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import coil.request.ImageRequest
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transformations
import com.example.music_player.Components.allSongs
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.PlaylistEntry
import com.example.music_player.RoomDatabse.PlaylistWithSongs
import com.example.music_player.ui.theme.projectBlue
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

    var queerySearch by remember { mutableStateOf("") }

    val db = Data.getInstance(context)


    val songs = if(queerySearch =="") allSongs else{
        val query = queerySearch.trim().lowercase()
        allSongs.filter {songs->
            val title = songs.title.trim().lowercase().replace(" ","")
            val match = title.contains(query)

            Log.e("Filter Songs", "addSongPlayList: title $title   match $match", )
            match
        }
    }

    val existingIds = remember { mutableStateListOf<Long>() }

    LaunchedEffect(id) {
        db.playListDao().getPlaylistWithSongs(id).collect { playlistWithSongs ->
            existingIds.clear()
            existingIds.addAll(playlistWithSongs.songs.map { it.songId })
        }
    }

    Log.e("My Songs", "addSongPlayList:songs = $songs and\n allsongs $allSongs ", )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = ondismiss,
        dragHandle = null,
        modifier = Modifier.height(1500.dp)
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


           searchBar(queerySearch, onQuerryChange = {returnString ->
               queerySearch = returnString
           }, modifier = Modifier.padding(bottom = 10.dp))

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
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))

                        )

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .weight(1f)
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

                                    db.playListDao().insertSongIntoPlayList(PlaylistEntry(id,item.songId))
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


@Composable
fun searchBar(
    query : String,
    onQuerryChange : (String) -> Unit,
    modifier: Modifier
              ) {


    OutlinedTextField(
        value = query,
        modifier = modifier.fillMaxWidth(1f),
        onValueChange = {
            onQuerryChange(it)
        },
        placeholder = { Text("Search any Songs", fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.search), "",
                modifier = Modifier.size(28.dp)
            )
        },
        shape = RoundedCornerShape(20.dp),

        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedBorderColor = projectBlue,
            focusedBorderColor = projectBlue,
            )
    )
    
}
