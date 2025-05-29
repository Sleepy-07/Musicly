package com.example.music_player.RoomDatabse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.nio.charset.CodingErrorAction.REPLACE
import kotlin.contracts.Returns

import kotlin.toString

@Dao
interface Inter {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertItem(data : List<SongMetadata>)

    @Query("Select * from songs")
    fun getData(): List<SongMetadata>

    @Query("Select * from songs where isLiked == true")
    fun getLikedSongs(): List<SongMetadata>

    @Query("SELECT * FROM songs WHERE lastPlayed > 0 ORDER BY lastPlayed DESC LIMIT 20")
    fun getRecentlyPlayedFlow(): Flow<List<SongMetadata>>

    @Query("SELECT * FROM songs WHERE isLiked == true ORDER BY likedTimeStamp DESC LIMIT 20")
    fun getRecentlyLikedFlow(): Flow<List<SongMetadata>>

    @Query("Update songs Set playCount = playCount+1, lastplayed = :lastTimePlayed where songId =:id")
    fun UpdatePlayCount(lastTimePlayed : Long, id : Long)

    @Query("UPDATE songs SET isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END, likedTimeStamp = :lastTimePlayed WHERE songId = :id")
    fun UpdateSongLike(lastTimePlayed: Long, id: Long)


    @Query("select * from songs where songId ==:id")
    fun getSingleItem(id: Long) : SongMetadata


}

@Dao
interface PlayListDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun CreatePlayList(data : Playlist) : Long



    @Transaction
    @Query("SELECT * FROM playList WHERE playlistId = :playlistId")
     fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs>


    @Query("SELECT * FROM playList")
    fun getAllPlaylistsFlow(): Flow<List<Playlist>>


    @Query("SELECT * FROM playList where playlistId ==:key")
    fun getPlaylistsFlow(key : Long): Playlist


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSongIntoPlayList(data : PlaylistEntry)

    @Delete
    fun removeSongFromPlaylist(crossRef: PlaylistEntry)


    @Transaction
    @Query("SELECT * FROM playList")
    suspend fun getAllPlaylistsWithSongs(): List<PlaylistWithSongs>

//
//    @Query("SELECT * FROM songs WHERE isLiked == true ORDER BY likedTimeStamp ")
//    fun getLikedSongs(
//        returns:
//    ) : Flow<List<SongMetadata>>








}