package com.onlinepjliveapp.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonElement
import com.onlinepjliveapp.App.Companion.context
import com.onlinepjliveapp.model.VideoModel

object Util {

    fun showSnackBar(activity: Activity?, message: String) {
        activity ?: return
        Snackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun noInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val an = cm.activeNetwork ?: return true
        val nc = cm.getNetworkCapabilities(an) ?: return true

        return when {
            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> false
            nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> false
            nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> false
            nc.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> false
            else -> true
        }
    }

    fun generateUrl(pageToken: String = ""): String {
        val base = "null"
        val key = "null"
        val channelId = "null"
        val uri = "${base}part=snippet&channelId=$channelId&maxResults=10&key=$key"
        if (pageToken.isNotEmpty())
            return "$uri&pageToken=$pageToken"
        return uri
    }

    fun parseData(data: JsonElement): MutableList<VideoModel> {

        val videos = mutableListOf<VideoModel>()
        val items = data.asJsonObject.get("items").asJsonArray
        for (a in items) {

            val info = a.asJsonObject.get("id").asJsonObject
            val type = info.get("kind").asString

            if(!info.has("videoId"))
                continue

            val id = info.get("videoId").asString

            val snippet = a.asJsonObject.get("snippet").asJsonObject
            val title = snippet.get("title").asString
            val description = snippet.get("description").asString

            val thumbnails = snippet.get("thumbnails").asJsonObject
            val thumb = thumbnails.get("high").asJsonObject.get("url").asString

            val createdAt = snippet.get("publishTime").asString

            val model = VideoModel(id, type, title, description, thumb, createdAt)
            videos.add(model)

        }

        return videos
    }

    fun parseNextPageToken(data: JsonElement): String {
        if (data.asJsonObject.has("nextPageToken"))
            return data.asJsonObject.get("nextPageToken").asString
        return ""
    }

}