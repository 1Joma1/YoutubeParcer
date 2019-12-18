package com.joma.youtubeparcer.api

import com.joma.youtubeparcer.model.Playlist
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IYouTubeApi {
    @GET("youtube/v3/playlists")
    fun getPLayList(
        @Query("maxResults") maxResult: String,
        @Query("channelId") channelId: String,
        @Query("key") apiKey: String,
        @Query("part") part: String
    ): Call<Playlist>
}