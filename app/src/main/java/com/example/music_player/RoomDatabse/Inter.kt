package com.example.music_player.RoomDatabse

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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

    @Query("Update songs Set isLiked = Not isLiked, likedTimeStamp = :lastTimePlayed where songId =:id")
    fun UpdateSongLike(lastTimePlayed : Long, id : Long)


    @Query("select * from songs where songId ==:id")
    fun getSingleItem(id: Long) : SongMetadata




}