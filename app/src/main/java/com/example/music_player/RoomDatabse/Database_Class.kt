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
    val artistId: Long,
    val albumId: Long,
    val albumName : String,
    val album : Uri,
    var playCount: Int = 0,
    var isLiked: Boolean = false,
    var lastPlayed: Long = 0L, // timestamp
    var likedTimeStamp : Long = 0L
)


@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey val artistId: Long,
    val artistName: String,
    val numberOfSongs: Int = 0,
    val numberOfAlbums: Int = 0
)

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey val albumId: Long,
    val albumName: String,
    val artistId: Long,
    val albumArtUri: Uri,
    val numberOfSongs: Int = 0
)

@Entity(tableName = "playList")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0,
    val playlistname: String,
    val playlistlogo : String,
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


data class ArtistWithSongsAndAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId"
    )
    val albums: List<Album>,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId"
    )
    val songs: List<SongMetadata>
)

data class AlbumWithSongs(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId"
    )
    val songs: List<SongMetadata>,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId"
    )
    val artist: Artist
)

data class SongWithArtistAndAlbum(
    @Embedded val song: SongMetadata,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "artistId"
    )
    val artist: Artist,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId"
    )
    val album: Album
)




data class IndexedSong(
    val index: Int,
    val song: SongMetadata
)