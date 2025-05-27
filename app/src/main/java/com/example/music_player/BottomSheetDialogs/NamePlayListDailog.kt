package com.example.music_player.BottomSheetDialogs

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties


@Composable
fun NamePlayListDialog(dismissDialog : () -> Unit ={}, createPlayList :(String) -> Unit ={}) {
    val context = LocalContext.current
    androidx.compose.ui.window.Dialog(
        onDismissRequest = {
            dismissDialog()
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = true
        )
    ) {
        var playlistname  by remember {mutableStateOf("")}

        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(220.dp)
                .background(
                    Color(0xFFD9D9D9),
                    RoundedCornerShape(20.dp)
                ),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            Text("PlayList Name?", fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                modifier = Modifier.padding(top = 28.dp),
                value = playlistname,
                onValueChange = { playlistname = it },
                placeholder = {Text("PlayList 1")},
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = Color.Black,
                    unfocusedPlaceholderColor = Color.Gray,
                    focusedPlaceholderColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp),

                )
            Row (modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 25.dp)
                .padding(top = 25.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)){
                OutlinedButton(
                    onClick = {
                        dismissDialog()
                    },

                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                    ),
                    border = BorderStroke(1.dp, Color.Red),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .weight(1f),

                    ) {
                    Text("Cancle", fontSize = 18.sp, color = Color.Black)
                }
                Button(
                    onClick = {
                        if(playlistname.trim() == "" ){
                            Toast.makeText(context, "Enter the PlayList Name ", Toast.LENGTH_SHORT).show()
                        }
                        else
                       createPlayList(playlistname)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1657C4)
                    ),
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .weight(1f),


                    ) {
                    Text("Create",fontSize = 18.sp, color = Color.White)
                }


            }
        }
    }

}
