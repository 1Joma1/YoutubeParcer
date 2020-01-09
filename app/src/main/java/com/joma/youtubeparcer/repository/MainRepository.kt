package com.joma.youtubeparcer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joma.youtubeparcer.api.IYouTubeApi
import com.joma.youtubeparcer.api.RetrofitClient
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.model.DetailVideo
import com.joma.youtubeparcer.model.Playlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainRepository {
    companion object{

        val channel = "UC5WjFrtBdufl6CZojX3D8dQ"
        val apiKey = "AIzaSyCSaIXlL22VkW6tvxCxPxhcj1rgd0-om_o"
        val part = "snippet,contentDetails"
        val maxResult = "20"

        private lateinit var apiService: IYouTubeApi
        fun fetchYoutubePlaylistData(): LiveData<Playlist> {
            apiService = RetrofitClient.create()
            val data = MutableLiveData<Playlist>()

            apiService.getPLayList(maxResult, channel, apiKey, part).enqueue(object :
                Callback<Playlist> {
                override fun onFailure(call: Call<Playlist>, t: Throwable) {
                    data.value = null
                }
                override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                    data.value = response.body()
                }
            })
            return data
        }

        fun fetchYoutubeDetailPlaylistData(playlistId: String): LiveData<DetailPlaylist>? {
            apiService = RetrofitClient.create()
            val data = MutableLiveData<DetailPlaylist>()
            apiService.getDetailPlaylist(part, apiKey, playlistId, maxResult).enqueue(object : Callback<DetailPlaylist> {

                override fun onResponse(call: Call<DetailPlaylist>, response: Response<DetailPlaylist>) {
                    data.value = response.body()
                }

                override fun onFailure(call: Call<DetailPlaylist>, t: Throwable) {
                    Log.v("response_fail", t.message)
                    data.value = null
                }

            })

            return data
        }

        fun fetchVideoData(videoId: String): LiveData<DetailVideo>? {
            apiService = RetrofitClient.create()
            val data = MutableLiveData<DetailVideo>()
            apiService.getDetailVideo(apiKey, part, videoId).enqueue(object : Callback<DetailVideo> {
                override fun onFailure(call: Call<DetailVideo>, t: Throwable) {
                    data.value = null
                }

                override fun onResponse(
                    call: Call<DetailVideo>,
                    response: Response<DetailVideo>
                ) {
                    data.value = response.body()
                }

            })
            return data
        }
    }
}