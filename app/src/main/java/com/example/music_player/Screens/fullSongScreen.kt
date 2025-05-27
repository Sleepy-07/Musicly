package com.example.music_player.Screens

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.music_player.Components.audioPlayer
import com.example.music_player.Components.bottomlist
import com.example.music_player.Components.currentsong
import com.example.music_player.Components.currentsonglist
import com.example.music_player.Components.getTotalDuration
import com.example.music_player.Components.songcurrenttime
import com.example.music_player.Components.songduration
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.SongMetadata
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs

//@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun fullSongScreen(item : SongMetadata, onDismiss : () -> Unit  ,modifier: Modifier = Modifier, tooglePlay : () -> Unit ={}, onSeek : (Float) -> Unit = {} , onrRepeat : () -> Unit ={} , onShuffle : () -> Unit = {} ){


    Log.e(
        "OpenFull Page",
        "fullSongScreen: currentindex = ${audioPlayer.index} and song = ${currentsonglist[audioPlayer.index].title}"
    )

    val db = Data.getInstance(LocalContext.current)
    val progress = audioPlayer.progress
    var isDragging by remember { mutableStateOf(false) }
    var isRepeat = audioPlayer.playBackMode
    var isShuffle = audioPlayer.playBackMode
    val context = LocalContext.current
    var from by remember { mutableStateOf(false) }

    var offsetx by remember { mutableFloatStateOf(0f) }

    var showQueue by remember { mutableStateOf(false)}

    if(showQueue){
        queueSongList(onDismissQueue = {
            showQueue=  false
        })
    }


    val max = 1f

    var isFav by remember { mutableIntStateOf(R.drawable.like) }
    isFav = if(item.isLiked) R.drawable.like_filled else R.drawable.like

    val isCurrentlyPlaying = audioPlayer.playBackState == audioPlayer.PlayBackState.PLAYING

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.height(1500.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101010)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


//        Header
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(top = 36.dp)
                    .padding(horizontal = 27.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Image(
                        painter = painterResource(R.drawable.arrow), "",
                    )

                }


                Column(modifier = Modifier.weight(1f)) {
                    Text("Playing From PlayList:", color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.title, color = Color.Gray, maxLines = 1,modifier = Modifier.width(100.dp))
                        Image(
                            painterResource(R.drawable.dropdown), ""
                        )
                    }
                }

                Image(
                    painterResource(R.drawable.more), "",

                    )


            }


//        Album Art


            AlbumCarousel()


            Column(
                modifier = Modifier
                    .padding(top = 90.dp)
                    .padding(horizontal = 37.dp)
                    .fillMaxSize(1f)
            ) {


                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {

//                Song title , artitst , like and add playlist

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(item.title, fontSize = 20.sp, color = Color.White, maxLines = 1)
                        Text(item.artist, fontSize = 16.sp, color = Color.Gray, maxLines = 1)
                    }
                    Row {
                        IconButton(
                            onClick = {}
                        ) {
                            Image(
                                painter = painterResource(
                                    R.drawable.add
                                ), "", colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        IconButton(
                            onClick = {
                                isFav = if(isFav == R.drawable.like) R.drawable.like_filled else R.drawable.like
                                item.isLiked = !item.isLiked
                                db.inter().UpdateSongLike(System.currentTimeMillis(),item.songId)
                                currentsong = item
                            }
                        ) {
                            Image(
                                painter = painterResource(
                                    isFav
                                ), "", colorFilter = ColorFilter.tint(if(isFav == R.drawable.like_filled) Color.Red else Color.White)
                            )
                        }
                    }
                }


//            Music Bar

                Musicbar(
                    progress = progress,
                    onSeek = onSeek

                )





//            Music Time

                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        getTotalDuration((songcurrenttime * songduration).toLong()),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        getTotalDuration((songduration).toLong()),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                }


//            Player Buttons

                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = {
                      onShuffle()
                    }) {

                        Image(
                            painterResource(R.drawable.shuffle), "",
                            colorFilter = ColorFilter.tint(if(isShuffle == audioPlayer.PlayBackMode.SHUFFLE) Color.White else Color.Gray)
                        )
                    }

                    IconButton(onClick = {
                        audioPlayer.playPrevious(context)
                    }) {

                        Image(
                            painterResource(R.drawable.pre), "",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF1657C4), CircleShape)
                            .clickable {
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            tooglePlay()
                        }) {
                            Image(
                                painterResource(
                                    if( isCurrentlyPlaying) R.drawable.pause
                                    else R.drawable.play), "",
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }

                    IconButton(onClick = {
                        Log.e("Nect Button CLicked", "fullSongScreen: ", )
                        audioPlayer.playNext(context)
                    }) {

                        Image(
                            painterResource(R.drawable.next), "",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }

                    IconButton(onClick = {
                        Log.e("playback", "fullSongScreen: ${audioPlayer.playBackMode} ", )
                        onrRepeat()

                    }) {

                        Image(
                            painterResource(R.drawable.repeat), "",
                            colorFilter = ColorFilter.tint(if(isRepeat == audioPlayer.PlayBackMode.REPEAT_ONE || isRepeat == audioPlayer.PlayBackMode.REPEAT_ALL) Color.White else Color.Gray)
                        )
                    }


                }



                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.End)
                ) {
                    IconButton(onClick = {}) {
                        Image(
                            painterResource(R.drawable.share), "",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                    IconButton(onClick = {
                       showQueue = true

                    }) {
                        Image(
                            painterResource(R.drawable.queue), "",
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun Musicbar(
    progress: Float,
    max: Float = 1f,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf<Float?>(null) }

    val currentProgress = dragProgress ?: progress

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(newProgress * max)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 10.dp)
                .fillMaxWidth(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragProgress = progress
                        },
                        onDragEnd = {
                            isDragging = false
                            dragProgress?.let { onSeek(it * max) }
                            dragProgress = null
                        },
                        onDrag = { change, _ ->
                            val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                            dragProgress = newProgress
                        }
                    )
                }
        ) {
            val lineThickness = 10f
            val thumbThickness = 25f
            val centerY = center.y

            // Draw background line (full width)
            drawLine(
                color = Color.Gray,
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = lineThickness
            )

            // Draw progress line
            drawLine(
                color = Color(0xFF1657C4),
                start = Offset(0f, centerY),
                end = Offset(size.width * currentProgress, centerY),
                strokeWidth = lineThickness
            )

            // Draw thumb
            drawCircle(
                color = Color(0xFF1657C4),
                radius = thumbThickness,
                center = Offset(size.width * currentProgress, centerY)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun queueSongList(modifier: Modifier = Modifier, onDismissQueue: () -> Unit) {

    val sheetstate = rememberModalBottomSheetState()
    val currentIndex = audioPlayer.index
    val context = LocalContext.current


    ModalBottomSheet(onDismissRequest =  onDismissQueue, sheetState = sheetstate, modifier = Modifier) {


        Text("Queue", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier
            .padding(horizontal = 15.dp)
            .padding(
                bottom = 10.dp
            ))
    LazyColumn {
        itemsIndexed(currentsonglist) {index,item->
            val isCurrent = currentIndex == index

            Row (modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 15.dp, vertical = 7.dp)
                .clickable {
                    audioPlayer.playIndex(index, context)
                },
                verticalAlignment = Alignment.CenterVertically){

                Box(modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.album)
                            .fallback(R.drawable.default_album_art)
                            .error(R.drawable.default_album_art)
                            .placeholder(R.drawable.default_album_art)
                            .build(),
                        "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize(1f)
                            .clip(RoundedCornerShape(10.dp)),
                    )


                }


                Column(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)) {

                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        maxLines = 1,
                        color = if(isCurrent) Color.Green else Color.White
                    )
                    Text(
                        text = item.artist,
                        fontSize = 10.sp,
                        maxLines = 1,
                        color = Color(0xFFA8A3A3)
                    )
                }

                IconButton(onClick = {

                }) {
                    Image(
                        painterResource(
                            if(isCurrent) R.drawable.play  else R.drawable.grab ),"",
                        colorFilter = ColorFilter.tint(if(isCurrent) Color.Green else Color.White)
                    )
                }
            }
        }
    }
    }
}

@Composable
fun AlbumCarousel(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = audioPlayer.index.coerceIn(0, currentsonglist.size - 1),
        pageCount = { currentsonglist.size }
    )

    // Track if the page change was user-initiated
    var isUserSwipe by remember { mutableStateOf(false) }

    LaunchedEffect(audioPlayer.index) {
        // Only respond to index changes if they weren't caused by user swipe
        if (!isUserSwipe && audioPlayer.index != pagerState.currentPage) {
            pagerState.animateScrollToPage(audioPlayer.index)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { currentPage ->
                // Only handle manual swipes when not in shuffle mode
                if (audioPlayer.playBackMode != audioPlayer.PlayBackMode.SHUFFLE) {
                    when {
                        currentPage > audioPlayer.index -> {
                            isUserSwipe = true
                            audioPlayer.playNext(context)
                            isUserSwipe = false
                        }
                        currentPage < audioPlayer.index -> {
                            isUserSwipe = true
                            audioPlayer.playPrevious(context)
                            isUserSwipe = false
                        }
                    }
                }
            }
    }

    HorizontalPager(
        state = pagerState,
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            snapAnimationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ),
        modifier = modifier
            .fillMaxWidth(1f)
            .aspectRatio(1f)
    ) { page ->
        val song = currentsonglist[page]
        Box(
            modifier = Modifier.fillMaxWidth(1f),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(song.album)
                    .fallback(R.drawable.default_album_art)
                    .error(R.drawable.default_album_art)
                    .placeholder(R.drawable.default_album_art)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 50.dp)
                    .width(322.dp)
                    .height(300.dp)
                    .clip(RoundedCornerShape(60.dp))
            )
        }
    }
}