package com.joma.youtubeparcer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.model.DetailVideo
import com.joma.youtubeparcer.model.Playlist

@Dao
interface YoutubeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlaylist(items: Playlist)

    @Query("SELECT * FROM play_list")
    suspend fun getAllPlayList(): Playlist

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailPlaylistData(items: DetailPlaylist)

    @Query("SELECT * FROM detail_playlist")
    suspend fun getDetailPlaylist(): List<DetailPlaylist>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailVideo(items: DetailVideo)

    @Query("SELECT * FROM detail_video")
    suspend fun getDetailVideo(): List<DetailVideo>?
}