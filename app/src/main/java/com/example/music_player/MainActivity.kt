package com.example.music_player

import android.Manifest
import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.music_player.BottomSheetDialogs.CreatePlayListDialog
import com.example.music_player.BottomSheetDialogs.NamePlayListDialog
import com.example.music_player.BottomSheetDialogs.addSongPlayList
import com.example.music_player.Components.PlayerBar
import com.example.music_player.Components.addSongSheet
import com.example.music_player.Components.audioPlayer
import com.example.music_player.Components.createshowsheet
import com.example.music_player.Components.currentsong
import com.example.music_player.Components.customdialog
import com.example.music_player.Components.showsheet
import com.example.music_player.Components.songduration
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.RoomDatabse.Playlist
//import com.example.music_player.Components.playerBar
import com.example.music_player.Screens.SongsScreen
import com.example.music_player.Screens.fullSongScreen
import com.example.music_player.Screens.libraryScreen
import com.example.music_player.ui.theme.Music_PlayerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

data class BottomItem(
    val icon :Int,
    val label : String,
)

class MainActivity : ComponentActivity() {
    internal val PERMISSION_REQUEST_KEY = 100
    val bottomlist = listOf(
        BottomItem(R.drawable.home,"Home"),
        BottomItem(R.drawable.search,"Search"),
        BottomItem(R.drawable.lib,"Library"),
        BottomItem(R.drawable.add,"Create"),
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
          Music_PlayerTheme {
              if(CheckRequest())
              HomeScreen()
          }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if(requestCode == PERMISSION_REQUEST_KEY && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d("MusicDebug", "Permission granted by user.")


        } else {
            Log.e("MusicDebug", "Permission denied.")
        }


    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun MainActivity.HomeScreen() {
    var selectedindex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val navController = rememberNavController()
    var playlistid by remember { mutableStateOf(0L) }

    val db = Data.getInstance(context)


    Log.e("showsheet outside ", "HomeScreen: sheet = $showsheet and song = $showsheet ", )

    if(showsheet){
        ShowPlayBar(context)
    }

    if(createshowsheet){
        OpenPlayListDailog()

    }

    if(customdialog){
        NamePlayListDialog(dismissDialog = {
            customdialog = false
        }, createPlayList = {name->
            customdialog = false
            CoroutineScope(Dispatchers.IO).launch {
             playlistid = db.playListDao().CreatePlayList(Playlist(playlistname =  name))
                addSongSheet = true
            }
        })
    }

    if(addSongSheet){
        addSongPlayList(playlistid, ondismiss = {
            addSongSheet = false
        })
    }



    Scaffold(
        bottomBar = {

            Column(

            ){
                currentsong?.let {song->
                    Log.e("Like Song", "HomeScreen:like =   ${song.isLiked}", )
                    songduration = song.duration
                    PlayerBar(item = song,
                        onSeek = { postion->
                            audioPlayer.seekTo(postion)
                        },
                        onPlayPause = {
                            audioPlayer.togglePause()
                        }, modifier = Modifier.clickable{
                        },
                        onClick = {
                            showsheet = !showsheet
                            Log.e("showsheet inside onclick ", "HomeScreen: sheet = $showsheet and song = $song ", )

                        }
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(.7f),
                                    Color.Black.copy(1f)
                                )
                            )
                        )
                ){

            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                containerColor = Color.Transparent
            ){
                bottomlist.forEachIndexed{index, item->
                    NavigationBarItem(
                        modifier = Modifier.size(30.dp),
                        selected = selectedindex == index,
                        onClick = {
                            if(bottomlist[index].label == "Create"){
                                Log.e("DoNothing", "HomeScreen: sheet = $createshowsheet",)
                               createshowsheet = true
                            }
                            else{
                                selectedindex = index
                            navController.navigate(bottomlist[index].label){
                            popUpTo(navController.graph.startDestinationId){
                                saveState = true
                            }
                                launchSingleTop = true
                                restoreState = true
                            }
                            }

                        },
                        icon = {Image(
                            painter = painterResource(item.icon)
                            ,item.label,
                            colorFilter = ColorFilter.tint(
                                if(selectedindex == index ) Color.White else Color.Gray
                            )
                        )},
                        label = {Text(item.label, fontSize = 10.sp)},
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Black.copy(alpha = 0f),
                            unselectedTextColor = Color.White,

                        )
                    )
                }
            }

                }

            }
        },
        content = { innerpadding->

            NavHost(
                navController = navController,
                startDestination = "Home",
            ){

                composable("Home") {SongsScreen(innerpadding)  }
                composable("Library") { libraryScreen(innerpadding) }

            }
        }
    )
}

@Composable
fun OpenPlayListDailog() {
    CreatePlayListDialog(onDismiss = {
        createshowsheet = false
    }, openDialog = {
        Log.e("Custom dialog", "HomeScreen: ", )
        customdialog = true
    }
    )
}

@Composable
fun ShowPlayBar(context : Context) {
    currentsong?.let {song->
        Log.e("showsheet inside ", "HomeScreen: sheet = $showsheet and song = $song ", )
        fullSongScreen(song, onDismiss = {
            showsheet = false
        },
            tooglePlay = {
                audioPlayer.togglePause()
            },
            onSeek = {postion->
                audioPlayer.seekTo(postion)
            },
            onrRepeat = {
                Toast.makeText(context, "Song Set to repeat", Toast.LENGTH_SHORT).show()
                audioPlayer.playBackMode = if(audioPlayer.playBackMode != audioPlayer.PlayBackMode.REPEAT_ONE) audioPlayer.PlayBackMode.REPEAT_ONE else audioPlayer.PlayBackMode.NORMAL
            },
            onShuffle = {
                Toast.makeText(context, "List Set to shuffle", Toast.LENGTH_SHORT).show()

                audioPlayer.playBackMode = if(audioPlayer.playBackMode != audioPlayer.PlayBackMode.SHUFFLE) audioPlayer.PlayBackMode.SHUFFLE else audioPlayer.PlayBackMode.NORMAL
            }
        )
    }

}

private fun MainActivity.CheckRequest() : Boolean  {

    if(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

    ){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_KEY
        )


    } else{
        Log.d("MusicDebug", "Permission already granted.")
        return true
    }
    return false
}

