package com.joma.youtubeparcer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joma.youtubeparcer.api.IYouTubeApi
import com.joma.youtubeparcer.api.RetrofitClient
import com.joma.youtubeparcer.model.Playlist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainRepository {
    companion object{

        val channel = "UCYzPXprvl5Y-Sf0g4vX-m6g"
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
    }
}