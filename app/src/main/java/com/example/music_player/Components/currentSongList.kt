package com.example.music_player.Components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transformations
import com.example.music_player.Screens.PlayLocalSong
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.SongMetadata

@Composable
fun songListItemHome(list : List<SongMetadata>, modifier: Modifier = Modifier, onclick : (Pair<Long, SongMetadata>) ->Unit) {

    Box(
        modifier = Modifier.height(600.dp)
    ){

    }

}

@Composable
fun SongGridItems(songlist : List<SongMetadata>,index: Int, onSongClick: (Long) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, start = 3.dp)
            .clickable {
                PlayLocalSong(songlist, index ,context)
                onSongClick(songlist[index].songId)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(songlist[index].album)
                .crossfade(true)
                .error(R.drawable.default_album_art)
                .placeholder(R.drawable.default_album_art)
                .fallback(R.drawable.default_album_art)
                .build(),
            contentDescription = songlist[index].title,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(9.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = songlist[index].title,
                fontSize = 13.sp,
                maxLines = 1,
                color = Color.White
            )
            Text(
                text = songlist[index].artist,
                fontSize = 10.sp,
                maxLines = 1,
                color = Color(0xFFA8A3A3)
            )
            Text(
                text = songlist[index].playCount.toString() + " plays",
                fontSize = 10.sp,
                color = Color(0xFFA8A3A3)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun songListItemRest(list: List<SongMetadata>, playlistname : String, modifier: Modifier = Modifier, onSongClick: (Pair<Long, SongMetadata>) -> Unit) {

    val context = LocalContext.current
    currenetplaylistname = playlistname

    val db = Data.getInstance(context)

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        LazyRow (modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)){

            itemsIndexed(list) {index,item->

                Column(modifier = Modifier
                    .width(96.dp)
                    .clickable(
                        onClick = {
                            onSongClick(Pair(item.songId,item))
                            db.inter()
                                .UpdatePlayCount(System.currentTimeMillis(), item.songId)
                            PlayLocalSong(list,index,context )
                        }
                    )) {

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.album)
                            .error(R.drawable.default_album_art)
                            .placeholder(R.drawable.default_album_art)
                            .fallback(R.drawable.default_album_art)
                            .build(),


                        contentScale = ContentScale.Crop,
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Text(
                        item.title,
                        maxLines = 1,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        item.artist,
                        maxLines = 1,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}