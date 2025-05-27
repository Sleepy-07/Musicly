package com.example.music_player.RoomDatabse

import android.net.Uri
import androidx.room.TypeConverter
import kotlin.collections.joinToString
import kotlin.text.isEmpty
import kotlin.text.split

class Converters {

    @TypeConverter
    fun fromList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }


    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }
    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}
