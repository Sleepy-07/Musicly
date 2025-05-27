package com.example.music_player.BottomSheetDialogs

import android.app.Dialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.music_player.Components.createshowsheet
import com.example.music_player.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlayListDialog(onDismiss : ()-> Unit, openDialog : () -> Unit ={}) {
    val sheetstate = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetstate,
        onDismissRequest = onDismiss,
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
        ){
        Row(modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 20.dp)
            .clickable {
                createshowsheet = false
                Log.e("Custom dialog", "HomeScreen: ", )
                openDialog()


            },
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.playllistlogo),"",
                modifier = Modifier.size(70.dp)
            )
            Column(
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text("Create PlayList", fontSize = 16.sp)
                Text("Your sound. Your rules. Start a playlist", fontSize = 12.sp)
            }
        }
        }



    }




}

