package com.joma.youtubeparcer.ui.detail_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joma.youtubeparcer.model.DetailPlaylist
import com.joma.youtubeparcer.repository.MainRepository
import com.joma.youtubeparcer.utils.App
import kotlinx.coroutines.launch

class DetailPlaylistViewModel : ViewModel() {

    val db = App().getInstance().getDatabase()

    fun fetchDetailPlaylistData(id: String): LiveData<DetailPlaylist>? {
        return MainRepository.fetchYoutubeDetailPlaylistData(id)
    }

    suspend fun getDetailPlaylistData(): List<DetailPlaylist>? {
        return db.ytVideoDao().getDetailPlaylist()
    }

    fun insertDetailPlaylistData(model: DetailPlaylist) {
        viewModelScope.launch {
            db.ytVideoDao().insertDetailPlaylistData(model)
        }
    }
}
