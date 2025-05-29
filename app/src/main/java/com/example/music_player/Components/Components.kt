package com.example.music_player.Components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController

val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

val AritistList = staticCompositionLocalOf{
    mutableStateListOf<String>()
}

var currenetplaylistname by  mutableStateOf("")

