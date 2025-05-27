package com.example.music_player.RoomDatabse

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "songs")
data class SongMetadata(
    @PrimaryKey val songId: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: Uri,
    val album : Uri,
    var playCount: Int = 0,
    var isLiked: Boolean = false,
    var lastPlayed: Long = 0L, // timestamp
    var likedTimeStamp : Long = 0L
)


@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val playlistname: String
)

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistEntry(
    val playlistId: Long,
    val songId: Long
)

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "songId",
        associateBy = Junction(PlaylistEntry::class)
    )
    val songs: List<SongMetadata>
)

