package com.example.music_player.Screens

import android.media.Image
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.music_player.BottomSheetDialogs.searchBar
import com.example.music_player.Components.LocalAppNavController
import com.example.music_player.Components.SongGridItems
import com.example.music_player.Components.allSongs
import com.example.music_player.Components.songListItemHome
import com.example.music_player.RoomDatabse.AlbumWithSongs
import com.example.music_player.RoomDatabse.Artist
import com.example.music_player.RoomDatabse.Data
import com.example.music_player.ui.theme.projectBlack
import kotlin.toString

@Composable
fun searchScreen(modifier: Modifier = Modifier,) {

    var searchqueery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = Data.getInstance(context)

    val albumArtists by db.musicDao().getAllAlbumsByArtist().collectAsState(initial = emptyList())

    val sortedAlbums = remember(albumArtists) {
        albumArtists.sortedBy { it.artist.artistName.lowercase() }
    }

    val groupedByInitial = remember(sortedAlbums) {
        sortedAlbums.groupBy { it.album.albumName.first().uppercaseChar() }
            .toSortedMap() // Ensures alphabetical order
    }

    val filtersongs = allSongs.filter {song->
        val title = song.title.trim().lowercase().replace(" ","")
        val match = title.contains(searchqueery)

        Log.e("Filter Songs", "addSongPlayList: title $title   match $match", )
        match
    }
    val filterartitst = albumArtists.filter {
        it.artist.artistName.trim().lowercase().replace(" ","").contains(searchqueery)
    }
    val filteralbums = sortedAlbums.filter {
        it.album.albumName.trim().lowercase().replace(" ","").contains(searchqueery)

    }

    Log.e("AlbumArt", "searchScreen: $sortedAlbums ", )



    val state = rememberLazyListState()

    Box(modifier = Modifier
        .fillMaxSize(1f)
        .padding(top = 32.dp)
        .padding(horizontal = 10.dp)){


        LazyColumn(state = state, modifier = Modifier.padding(top = 70.dp)) {
            if(searchqueery.isEmpty()){
            item {

                Row(modifier = Modifier.fillMaxWidth(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {

                    Text("Artist", fontSize = 20.sp,)

                }

                Spacer(modifier = Modifier.height(15.dp))


            }
            item {
                ArtitistUi(sortedAlbums)

            }

            item {
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {

                    Text("Albums", fontSize = 20.sp,)

                }
            }


            groupedByInitial.forEach { (initial, albumsForInitial) ->
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(projectBlack)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = initial.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                items(albumsForInitial) { albumWithSongs ->
                    AlbumItem(albumWithSongs)
                }
            }


            item {
                Spacer(modifier = Modifier.height(120.dp))
            }

            }
            else{
                if(filtersongs.isNotEmpty()){

               stickyHeader {
                   Box(
                       modifier = Modifier.fillMaxWidth(1f).background(projectBlack)
                   ){
                   Text("Songs", fontSize = 20.sp,modifier = Modifier.padding(vertical = 10.dp))
               }}

               itemsIndexed(filtersongs) {index, song->
                   SongGridItems(filtersongs,index) { }
               }
                }

                if(filterartitst.isNotEmpty()){
                stickyHeader{
                    Box(
                        modifier = Modifier.fillMaxWidth(1f).background(projectBlack)
                    ){

                    Text("Artist", fontSize = 20.sp,modifier = Modifier.padding(vertical = 10.dp))
                    }
                }
                item {
                ArtitistUi(filterartitst)
                }

                }
                if(filteralbums.isNotEmpty()){

                stickyHeader {
                    Box(
                        modifier = Modifier.fillMaxWidth(1f).background(projectBlack)
                    ){
                    Text("Albums", fontSize = 20.sp,modifier = Modifier.padding(vertical = 10.dp))
                }}

                items(filteralbums) {item->
                    AlbumItem(item)
                }
                }
                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }

            }

        }
        Box(
            modifier = Modifier.fillMaxWidth(1f)
                .background(projectBlack)
        ){
        searchBar(query = searchqueery, onQuerryChange = {
            searchqueery = it
        },modifier )
        }
    }







}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtitistUi(list : List<AlbumWithSongs>) {

    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
    LazyHorizontalGrid(
       rows = GridCells.Fixed(2),
        modifier = Modifier.height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list) {item->

            UiDes(item)
        }

    }
}
}

@Composable
fun UiDes(item: AlbumWithSongs) {

    Row {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.album.albumArtUri)
                .build(),"",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(item.artist.artistName, modifier = Modifier.width(100.dp), maxLines = 1, fontSize = 14.sp )
            Text(item.artist.numberOfSongs.toString()+" songs", fontSize = 12.sp)
            Text(item.artist.numberOfAlbums.toString() + "albums", fontSize = 12.sp)
        }
    }
}
@Composable
fun AlbumItem(albumWithSongs: AlbumWithSongs) {
    val navController = LocalAppNavController.current

    Row (modifier = Modifier.fillMaxWidth(1f).padding(vertical = 7.dp).clickable{
        val artisitid = albumWithSongs.artist.artistId
        navController.navigate("AlbumScreen/${artisitid}")
    },
        verticalAlignment = Alignment.CenterVertically){

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(albumWithSongs.album.albumArtUri)
                .build(),"",
            contentScale = ContentScale.Crop,

            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(10.dp))
        )


    Column(modifier = Modifier.padding(8.dp)) {

        Text(text = albumWithSongs.album.albumName, style = MaterialTheme.typography.bodyLarge)
        Text(text = albumWithSongs.artist.artistName, style = MaterialTheme.typography.bodySmall)
    }
    }
}



