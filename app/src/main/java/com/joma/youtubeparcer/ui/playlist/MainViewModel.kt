package com.joma.youtubeparcer.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joma.youtubeparcer.model.Playlist
import com.joma.youtubeparcer.repository.MainRepository
import com.joma.youtubeparcer.utils.App
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val db = App().getInstance().getDatabase()
    fun getPlaylistData() : LiveData<Playlist> {
        return MainRepository.fetchYoutubePlaylistData()
    }

    fun insertPlaylistData(model: Playlist) {
        viewModelScope.launch {
            db.ytVideoDao()?.insertAllPlaylist(model)
        }
    }

    suspend fun getDataFromDB() : Playlist{
        return db.ytVideoDao().getAllPlayList()
    }
}