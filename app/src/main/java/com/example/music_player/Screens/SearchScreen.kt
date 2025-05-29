package com.example.music_player.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun searchScreen(modifier: Modifier = Modifier,) {

    var searchqueery by remember { mutableStateOf("") }



    Box(modifier = Modifier.fillMaxSize(1f).padding(top = 32.dp).padding(horizontal = 10.dp)){

        LazyColumn {
            item {

                Row(modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {

                    Text("Artist", fontSize = 17.sp,)
                    Text("View All", fontSize = 14.sp,)

                }
            }
        }


    }


}