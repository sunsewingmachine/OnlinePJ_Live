package com.youtubevideos.model

data class VideoModel(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val thumb: String,
    val createdAt: String
)