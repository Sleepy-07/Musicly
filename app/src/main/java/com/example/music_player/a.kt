package com.example.music_player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

data class LyricLine(val timeMillis: Long, val text: String)


fun parseLrcText(lrcText: String): List<LyricLine> {
    val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})] ?(.+)""")
    return lrcText.lines().mapNotNull { line ->
        val match = regex.find(line) ?: return@mapNotNull null

        val (minStr, secStr, millisStr, lyricText) = match.destructured
        val minutes = minStr.toLongOrNull() ?: 0
        val seconds = secStr.toLongOrNull() ?: 0
        val millis = if (millisStr.length == 3) millisStr.toLongOrNull() ?: 0 else (millisStr.toLongOrNull() ?: 0) * 10
        val totalMillis = minutes * 60_000 + seconds * 1000 + millis
        LyricLine(timeMillis = totalMillis, text = lyricText)
    }.sortedBy { it.timeMillis }
}

@Composable
fun loadLyricsFromAssets(filename: String): List<LyricLine> {
    val context = LocalContext.current
    return remember {
        val lrcText = context.assets.open(filename).bufferedReader().use { it.readText() }
        parseLrcText(lrcText)
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val lyrics = loadLyricsFromAssets("prettylittlebaby_lyrics.lrc")
    LyricAnimatedFlowPlayer(lyrics)
}
@Composable
fun LyricAnimatedFlowPlayer(lyrics: List<LyricLine>) {
    var currentTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.preety_little_baby}")
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    LaunchedEffect(player) {
        while (true) {
            currentTime = player.currentPosition
            delay(100L)
        }
    }

    val currentIndex = lyrics.indexOfLast { it.timeMillis <= currentTime }.coerceAtLeast(0)

    // State for animation key
    val currentLine = lyrics.getOrNull(currentIndex)?.text ?: ""

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentLine,
            transitionSpec = {
                // Slide in from bottom + fade in

                slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(
                    initialAlpha = 0.6f
                ) togetherWith
                        // Slide out up + fade out
                        slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut(
                    targetAlpha = 0f
                )
            },
            contentAlignment = Alignment.Center,
            label = "Lyric Line Animation"
        ) { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun PreviewLyricsList() {
    val mockLyrics = List(10) {
        LyricLine(timeMillis = it * 3000L, text = "Line ${it + 1}")
    }
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        mockLyrics.forEach {
            Text(text = it.text, modifier = Modifier.padding(8.dp))
        }
    }
}









//var isPlaying = true
//object audioPlayer{
//    private var exoplayer : ExoPlayer? = null
//
//    fun assignPlayer(context: Context) : ExoPlayer{
//        if(exoplayer == null){
//            exoplayer = ExoPlayer.Builder(context).build()
//        }
//        return exoplayer!!
//    }
//
//    fun playUri(uri : Uri, context: Context){
//        val player = assignPlayer(context = context)
//        player.apply {
//            val mediaitem = MediaItem.fromUri(uri)
//            setMediaItem(mediaitem)
//            prepare()
//            playWhenReady = true
//        }
//    }
//
//    fun getPlayer() : ExoPlayer{
//        return exoplayer!!
//    }
//
//    fun playerStop(){
//        exoplayer?.stop()
//    }
//    fun  playerPause(){
//        exoplayer?.pause()
//    }
//    fun playerresume(){
//        exoplayer?.play()
//    }
//}
//
//// Add this to your audioPlayer object or create a new class
//object PlayerState {
//    var currentSong by mutableStateOf<SongMetadata?>(null)
//    var isPlaying by mutableStateOf(false)
//    var progress by mutableStateOf(0f)
//    var duration by mutableStateOf(0L)
//}





//@Preview(showSystemUi = true)
//@Composable
//fun MusicBar(modifier: Modifier = Modifier, onExpand: () -> Unit) {
//
//    val button = listOf(
//        BottomItem(R.drawable.add,"Add"),
//        BottomItem(R.drawable.like,"like"),
//        BottomItem(R.drawable.play,"play")
//
//    )
//
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(1f)
//                .height(80.dp)
//                .padding(horizontal = 8.dp)
//                .clip(RoundedCornerShape(15.dp))
//                .background(Color(0xFF1B263B)),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            Image(
//                painter = painterResource(R.drawable.default_album_art),"",
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(RoundedCornerShape(15.dp))
//                    .padding(start = 11.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .padding(start = 6.dp)
//                    .weight(1f)
//            ) {
//                Text("Grainy Days", maxLines = 1, color = Color(0xFFE0E0E0), fontSize = 16.sp)
//                Text("moody.", maxLines = 1, color = Color(0xFFCCCCCC), fontSize = 10.sp)
//            }
//
//            Row (
//                modifier = Modifier.padding(end = 11.dp)
//            ){
//                button.forEachIndexed {index , item->
//
//                    Image(
//                        painter = painterResource(item.icon),item.label,
//                        modifier = Modifier.clickable{
//                            when(index){
//
//                                2->{
//                                    if(isPlaying) audioPlayer.playerPause() else audioPlayer.playerresume()
//                                }
//
//                            }
//                        }
//                    )
//
//
//                }
//
//            }
//
//        }
//
//    }






//@Composable
//fun PlayerBar(
//    modifier: Modifier = Modifier,
//    onExpand: () -> Unit = {}
//) {
//    val context = LocalContext.current
//    val currentSong = PlayerState.currentSong
//
//    if (currentSong != null) {
//        Column(
//            modifier = modifier
//                .fillMaxWidth()
//                .height(80.dp)
//                .padding(horizontal = 8.dp)
//                .clip(RoundedCornerShape(15.dp))
//                .background(Color(0xFF1B263B))
//                .clickable { onExpand() },
//            verticalArrangement = Arrangement.Center
//        ) {
//            // Progress bar
//            Slider(
//                value = PlayerState.progress,
//                onValueChange = { newProgress ->
//                    audioPlayer.getPlayer().seekTo((newProgress * PlayerState.duration).toLong())
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                colors = SliderDefaults.colors(
//                    thumbColor = Color.White,
//                    activeTrackColor = Color.White,
//                    inactiveTrackColor = Color.Gray
//                )
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Album art
//                AsyncImage(
//                    model = ImageRequest.Builder(context)
//                        .data(currentSong.album)
//                        .crossfade(true)
//                        .error(R.drawable.default_album_art)
//                        .placeholder(R.drawable.default_album_art)
//                        .fallback(R.drawable.default_album_art)
//                        .build(),
//                    contentDescription = currentSong.title,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(RoundedCornerShape(15.dp))
//                )
//
//                // Song info
//                Column(
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 8.dp),
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        text = currentSong.title,
//                        maxLines = 1,
//                        color = Color.White,
//                        fontSize = 14.sp
//                    )
//                    Text(
//                        text = currentSong.artist,
//                        maxLines = 1,
//                        color = Color.LightGray,
//                        fontSize = 12.sp
//                    )
//                }
//
//                // Controls
//                IconButton(
//                    onClick = {
//                        if (PlayerState.isPlaying) {
//                            audioPlayer.playerPause()
//                        } else {
//                            audioPlayer.playerresume()
//                        }
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(
//                            if (PlayerState.isPlaying) R.drawable.play else R.drawable.home
//                        ),
//                        contentDescription = if (PlayerState.isPlaying) "Pause" else "Play",
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
//        }
//    }
//}


//fun formatDuration(durationMs: Long): String {
//    val totalSeconds = durationMs / 1000
//    val minutes = totalSeconds / 60
//    val seconds = totalSeconds % 60
//    return "%d:%02d".format(minutes, seconds)
//}




//@Composable
//fun SongGridView(songs: List<SongMetadata>, onSongPlayed: (Long) -> Unit) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.fillMaxSize(),
//
//    ) {
//        items(songs) { item ->
//            SongGridItems(song = item, onSongClick = { onSongPlayed(item.songId) })
//        }
//    }
//}
//
//@Composable
//fun SongGridItems(song: SongMetadata, onSongClick: () -> Unit) {
//    val context = LocalContext.current
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp)
//            .clickable {
//                PlayLocalSong(song, context)
//                onSongClick()
//            },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        AsyncImage(
//            model = ImageRequest.Builder(context)
//                .data(song.album)
//                .crossfade(true)
//                .error(R.drawable.default_album_art)
//                .placeholder(R.drawable.default_album_art)
//                .fallback(R.drawable.default_album_art)
//                .build(),
//            contentDescription = song.title,
//            modifier = Modifier
//                .size(75.dp)
//                .clip(RoundedCornerShape(9.dp)),
//            contentScale = ContentScale.Crop
//        )
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .padding(start = 10.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = song.title,
//                fontSize = 13.sp,
//                maxLines = 1,
//                color = Color.White
//            )
//            Text(
//                text = song.artist,
//                fontSize = 10.sp,
//                maxLines = 1,
//                color = Color(0xFFA8A3A3)
//            )
//            Text(
//                text = song.playCount.toString(),
//                fontSize = 10.sp,
//                color = Color(0xFFA8A3A3)
//            )
//        }
//    }
//}




//                LazyRow (modifier = Modifier
//                    .fillMaxWidth(1f)
//                    .padding(top = 18.dp)
//                    .padding(start = 33.dp),
//                    horizontalArrangement = Arrangement.spacedBy(15.dp)){
//
//                    items(recentlyPlayed) {item->
//
//                        Column(modifier = Modifier
//                            .width(96.dp)
//                            .clickable(
//                                onClick = {
//                                    db.inter()
//                                        .UpdatePlayCount(System.currentTimeMillis(), item.songId)
//                                    PlayLocalSong(item, context)
//                                    currentsong = item
//                                }
//                            )) {
//
//                            AsyncImage(
//                                model = ImageRequest.Builder(context)
//                                    .data(item.album)
//                                    .error(R.drawable.default_album_art)
//                                    .placeholder(R.drawable.default_album_art)
//                                    .fallback(R.drawable.default_album_art)
//                                    .build(),
//
//
//                                contentScale = ContentScale.Crop,
//                                contentDescription = item.title,
//                                modifier = Modifier
//                                    .size(96.dp)
//                                    .clip(RoundedCornerShape(10.dp))
//                            )
//
//                            Text(
//                                item.title,
//                                maxLines = 1,
//                                color = Color.White,
//                                fontSize = 12.sp
//                            )
//                            Text(
//                                item.artist,
//                                maxLines = 1,
//                                color = Color.White,
//                                fontSize = 10.sp
//                            )
//
//
//
//
//
//                        }
//
//
//                    }
//
//
//                }
