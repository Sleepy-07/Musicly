package com.example.music_player.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music_player.Components.createshowsheet
import com.example.music_player.Components.songListItemRest
import com.example.music_player.R
import com.example.music_player.RoomDatabse.Data
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun libraryScreen(innerpadding : PaddingValues) {
    val context = LocalContext.current
    val db = Data.getInstance(context)

    val recentlyLiked by db.inter().getRecentlyLikedFlow().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(horizontal = 10.dp)
            .padding(innerpadding)
    ) {
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

        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Liked Songs", fontSize = 19.sp, color = Color.White)
            Text("View All", fontSize = 13.sp, color = Color.White)
        }

        songListItemRest(recentlyLiked, onSongClick = { (id, item) ->

        })
    }


}