package com.example.music_player.Screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transformations
import com.example.music_player.BottomSheetDialogs.addSongPlayList
import com.example.music_player.Components.LocalAppNavController
import com.example.music_player.Components.audioPlayer
import com.example.music_player.Components.currenetplaylistname
import com.example.music_player.Components.formatDuration
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.Playlist
import com.example.music_player.RoomDatabse.PlaylistWithSongs
import com.example.music_player.RoomDatabse.SongMetadata
import com.example.music_player.ui.theme.projectBlack
import com.example.music_player.ui.theme.projectBlue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ViewplayList(innerPadding: PaddingValues, playListId : Long) {

    val context = LocalContext.current
    val db = Data.getInstance(context)
    val navController = LocalAppNavController.current

    val playlist = db.playListDao().getPlaylistsFlow(playListId)

    val playlistsongs by db.playListDao().getPlaylistWithSongs(playListId).collectAsState(
        initial = PlaylistWithSongs(Playlist(playListId,"", ""),emptyList())
    )

    var addSongs by remember { mutableStateOf(false) }
   val duration = playlistsongs.songs.sumOf { it.duration }

    Log.e("PlayListEdit", "ViewplayList: $playlistsongs", )
    val scrollState = rememberLazyListState()
    val buttonVisible by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 100
        }
    }

    val buttonOffset by animateDpAsState(
        targetValue = if (buttonVisible) 0.dp else 40.dp,
        label = "buttonOffset"
    )



    val density = LocalDensity.current

    val scrollOffset by remember(scrollState) {
        derivedStateOf {

            calculateTotalScroll(scrollState, density).also {
                Log.d("Scroll", "Index: ${scrollState.firstVisibleItemIndex}, Offset: ${scrollState.firstVisibleItemScrollOffset}, Total: $it px")
            }
        }
    }



    if(addSongs){
        addSongPlayList(playListId, ondismiss ={
            addSongs = false
        })
    }

    LaunchedEffect(scrollState) {
        snapshotFlow {
            scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset
        }.collect { (index,offset) ->
            val totalScroll = calculateTotalScroll(scrollState,density)
        }
    }



    val maxSize = 150.dp
    val minSize = 80.dp

    // Define the offset at which progress reaches 1f (e.g., 300px)
    val maxOffset = 400f
    val maxOffset2 = 650f

// Clamp and normalize
    val scrollProgress = (scrollOffset / maxOffset).coerceIn(0f, 1f)
    val scrollProgress2 = (scrollOffset  / maxOffset2).coerceIn(0f, 1f)
    Log.e("Scroll Progress", "ViewplayList: $scrollProgress ", )
    Log.e("Scroll Progress2", "ViewplayList: $scrollProgress2 ", )

    val imagesize by animateDpAsState(
        targetValue = max(minSize, maxSize - (scrollOffset / 3.5).dp),
        tween(100),
        label= ""
    )

    val animatedAlpha by animateFloatAsState(
        targetValue =  scrollProgress, // Fully visible at top, invisible at scroll
        label = "AlphaScroll"
    )
    val animatedAlphaContent by animateFloatAsState(
        targetValue =  scrollProgress2 , // Fully visible at top, invisible at scroll
        label = "AlphaScroll"
    )



    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 32.dp)
        ) {

        LazyColumn(state = scrollState, modifier = Modifier.padding(horizontal = 15.dp)) {
            // Large Cover Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model =  if(playListId==1L){
                            ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.likesongs)
                                .build()
                        }else{
                            ImageRequest.Builder(LocalContext.current)
                                .data(if (playlist.playlistlogo.isNullOrBlank()) null else playlist.playlistlogo)
                                .error(R.drawable.default_album_art)
                                .placeholder(R.drawable.default_album_art)
                                .fallback(R.drawable.default_album_art)
                                .build()
                        }, "",
                        modifier = Modifier.size(imagesize)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                    Text(playlist.playlistname, fontSize = 17.sp)
                    Text(playlistsongs.songs.size.toString() +" songs", fontSize = 14.sp)
                    Text("playback "+formatDuration(duration) +" min", fontSize = 14.sp, color = Color.Gray)

                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, color = projectBlue, RoundedCornerShape(100.dp)),
                            contentAlignment = Alignment.Center
                        ){

                        IconButton(
                            onClick = {
                                addSongs = true
                            }
                        ) {
                            Icon(painterResource(R.drawable.add),"")
                        }
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, color = projectBlue, RoundedCornerShape(100.dp)),
                            contentAlignment = Alignment.Center
                        ){

                        IconButton(
                            onClick = {}
                        ) {
                            Icon(painterResource(R.drawable.shuffle),"", modifier = Modifier.size(35.dp))
                        }
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(1.dp, color = projectBlue, RoundedCornerShape(100.dp)),
                            contentAlignment = Alignment.Center
                        ){

                            IconButton(
                                onClick = {
                                    audioPlayer.playSongPlayList(playlistsongs.songs,0,context)
                                }
                            ) {
                                Icon(painterResource(R.drawable.play),"")
                            }
                        }

                    }
                }
            }


            // Song List
            itemsIndexed(playlistsongs.songs) { index,song ->
             Row(modifier = Modifier
                 .fillMaxWidth(1f)
                 .padding(bottom = 10.dp)
                 .clickable{
                     currenetplaylistname = playlist.playlistname
                     audioPlayer.playIndexPlayList(index,playlistsongs.songs, context)
                 },
                 horizontalArrangement = Arrangement.spacedBy(10.dp),
                 verticalAlignment = Alignment.CenterVertically){
                 AsyncImage(
                     model = ImageRequest.Builder(LocalContext.current)
                         .data(song.album)
                         .error(R.drawable.default_album_art)
                         .fallback(R.drawable.default_album_art)
                         .placeholder(R.drawable.default_album_art)
                         .build(),
                     "",
                     contentScale = ContentScale.Crop,
                     modifier = Modifier
                         .size(50.dp)
                         .clip(RoundedCornerShape(10.dp))


                 )

                 Column() {
                     Text(song.title, maxLines = 1, fontSize = 13.sp,)
                     Text(song.artist, maxLines = 1, fontSize = 11.sp, color = Color.Gray)
                     Text(song.playCount.toString() + " plays", maxLines = 1, fontSize = 11.sp, color = Color.Gray)
                 }

             }
            }

            item{
                Spacer(modifier = Modifier.height(120.dp))
            }

        }

        // Sticky Header that fades in after scrolling
            Box(modifier = Modifier
                .fillMaxWidth(1f)
                .background(Color(0x112F2F2F).copy(alpha = animatedAlpha))
                .padding(horizontal = 10.dp)){
                Row(modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically) {
                 IconButton(onClick = {
                     navController.popBackStack()
                 }) {
                Icon(
                    painterResource(R.drawable.arrow),
                    "",
                    modifier = Modifier.rotate(90f),
                    tint = projectBlue
                )
            }
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(playlist.playlistname, modifier = Modifier.alpha(animatedAlphaContent))
                    }


                }

            }

    }
}



fun calculateTotalScroll(state: LazyListState, density: Density): Int {
    // Define all item heights in pixels
    val firstItemHeightPx = with(density) { 64.dp.toPx() + 150.dp.toPx() + 24.dp.toPx() } // Header section
    val secondItemHeightPx = with(density) { 100.dp.toPx() } // Info row
    val songItemHeightPx = with(density) { 70.dp.toPx() } // Each song item

    return when {
        state.firstVisibleItemIndex == 0 -> state.firstVisibleItemScrollOffset
        state.firstVisibleItemIndex == 1 -> firstItemHeightPx.toInt() + state.firstVisibleItemScrollOffset
        else -> {
            // For song items (index >= 2)
            firstItemHeightPx.toInt() +
                    secondItemHeightPx.toInt() +
                    ((state.firstVisibleItemIndex - 2) * songItemHeightPx.toInt()) +
                    state.firstVisibleItemScrollOffset
        }
    }
}
