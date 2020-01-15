package com.joma.youtubeparcer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.model.DetailVideo
import com.joma.youtubeparcer.model.Playlist

@Database(
    entities = [
        Playlist::class,
        DetailPlaylist::class,
        DetailVideo::class],
    version = 3, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ytVideoDao(): YoutubeDao
}