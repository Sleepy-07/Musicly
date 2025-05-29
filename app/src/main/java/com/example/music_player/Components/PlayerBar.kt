package com.example.music_player.Components

import android.annotation.SuppressLint
import android.media.Image
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import coil3.request.transformations
import com.example.music_player.BottomItem
import com.example.music_player.Components.audioPlayer.progress
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.PlaylistEntry
import com.example.music_player.RoomDatabse.SongMetadata
import kotlin.math.abs
import kotlin.math.max

//@Preview
@Composable
fun PlayerBar(
    item: SongMetadata,
    modifier: Modifier = Modifier,
    onSeek: (Float) -> Unit = {},
    onPlayPause: () -> Unit = {},
    onClick: () -> Unit = {}
) {



    var offsetx by remember { mutableFloatStateOf(0f) }


    val context = LocalContext.current
    val db = Data.getInstance(context)

    // Get current playback state
    val isCurrentlyPlaying = audioPlayer.playBackState == audioPlayer.PlayBackState.PLAYING
    var isFav by remember { mutableIntStateOf(R.drawable.like) }
    isFav = if(item.isLiked) R.drawable.like_filled else R.drawable.like

    val progress = progress
    songcurrenttime = audioPlayer.progress

    Log.e("Current Progress2 ", "PlayerBar: $progress ", )


    Column(modifier = Modifier.padding(horizontal = 8.dp)){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFF1B263B))
            .clickable{onClick()}

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp)
                .padding(bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(item.album)
                    .fallback(R.drawable.default_album_art)
                    .error(R.drawable.default_album_art)
                    .placeholder(R.drawable.default_album_art)
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Song info
            Column(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .weight(1f)
                    .graphicsLayer{
                        translationX = offsetx
                    }
                    .pointerInput(Unit){
                        detectDragGestures(
                            onDrag = {_, offset->

                                offsetx += offset.x
                            },
                            onDragEnd = {
                                if(offsetx > 100 ){
                                    audioPlayer.playPrevious(context)
                                }
                                else if (offsetx < -100f){
                                    audioPlayer.playNext(context)
                                }
                                offsetx = 0f
                            }
                        )
                    },
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    item.title,
                    maxLines = 1,
                    color = Color(0xFFE0E0E0),
                    fontSize = 12.sp
                )
                Text(
                    item.artist,
                    maxLines = 1,
                    color = Color(0xFFCCCCCC),
                    fontSize = 11.sp
                )
            }

            // Controls
            Row(
            ) {
                IconButton(onClick = {

                }) {
                    Image(
                        painter = painterResource(R.drawable.add),
                        contentDescription = "Add to playlist"
                    )
                }

                IconButton(onClick = {
                    if(item.isLiked){
                        db.inter().UpdateSongLike(System.currentTimeMillis(),item.songId)
                        Log.e("Song Liked ", "fullSongScreen: ${item.isLiked}", )
                        db.playListDao().removeSongFromPlaylist(PlaylistEntry(playlistId = 1, songId = item.songId))
                    }
                    else{
                        db.inter().UpdateSongLike(System.currentTimeMillis(),item.songId)
                        Log.e("Song Liked ", "fullSongScreen: ${item.isLiked}", )

                        db.playListDao().insertSongIntoPlayList(PlaylistEntry(playlistId = 1, songId = item.songId))
                    }
                    isFav = if(isFav == R.drawable.like) R.drawable.like_filled else R.drawable.like
                    item.isLiked = !item.isLiked
                    currentsong = item
                }) {
                    Log.e("Like Song inside playbar ", "HomeScreen:like =   ${currentsong?.isLiked}", )
                    Image(
                        painter = painterResource(isFav),
                        contentDescription = "Like song"
                    )
                }

                IconButton(onClick = onPlayPause) {
                    Image(
                        painter = painterResource(
                            if (isCurrentlyPlaying) R.drawable.pause
                            else R.drawable.play
                        ),
                        contentDescription = if (isCurrentlyPlaying) "Pause" else "Play"
                    )
                }
            }
        }

        // Progress slider
        CustomSpotifySlider(
            progress = progress,
            onSeek = onSeek,
        )

    }
    }
}
@Composable
fun CustomSpotifySlider(
    progress: Float,  // Current progress (0f to 1f)
    max: Float = 1f,  // Maximum value (default 1 for percentage)
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    val trackHeight = 2.dp
    val thumbRadius = 4.dp


    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
//                    onSeek(newProgress * max)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false }
                    ) { change, _ ->
                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
//                        onSeek(newProgress * max)
                    }
                }
        ) {
            // Inactive track (background)
            drawLine(
                color = Color.Gray,
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = trackHeight.toPx()
            )

            // Active track (progress)
            drawLine(
                color = Color.White,
                start = Offset(0f, center.y),
                end = Offset(size.width * (progress), center.y),
                strokeWidth = trackHeight.toPx()
            )

            // Thumb
//            drawCircle(
//                color = Color.White,
//                radius = thumbRadius.toPx(),
//                center = Offset(size.width * progress, center.y)
//            )
        }
    }
}






