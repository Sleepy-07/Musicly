package com.example.music_player.RoomDatabse

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.music_player.R

@Database(entities = [SongMetadata::class, Playlist ::class, PlaylistEntry::class, Artist::class, Album::class], version=2 , exportSchema = true)
@TypeConverters(Converters::class)



abstract class Data() : RoomDatabase() {


    abstract fun inter() : Inter
    abstract fun playListDao() : PlayListDao
    abstract fun musicDao() : MusicDao

    companion object{

        private var data : Data? = null

        fun getInstance(context: Context):Data {
            if(data == null){
                data = Room.databaseBuilder(context,
                    Data::class.java,
                    "Data_BAse")
                    .allowMainThreadQueries()
                    .addCallback(object  : RoomDatabase.Callback(){
                        override fun onCreate(connection: SQLiteConnection) {
                            super.onCreate(connection)
                            val logouri = ("android.resources://${context.packageName}/${R.drawable.likesongs}")
                            Thread{
                                getInstance(context).playListDao().CreatePlayList(
                                    Playlist(playlistId = 1, playlistname = "Liked Songs", playlistlogo = logouri)
                                )
                            }.start()
                        }
                    })
                    .build()

            }
            return data!!
        }
    }

}