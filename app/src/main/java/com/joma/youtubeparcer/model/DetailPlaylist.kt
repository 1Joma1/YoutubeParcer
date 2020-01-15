package com.joma.youtubeparcer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.joma.youtubeparcer.type_converters.PlaylistModelTypeConverter

@Entity(tableName = "detail_playlist")
@TypeConverters(PlaylistModelTypeConverter::class)
data class DetailPlaylist(
    @SerializedName("kind")
    val kind: String,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo,
    @SerializedName("etag")
    @PrimaryKey
    val etag: String,
    @SerializedName("items")
    val items: List<ItemsItem>


)