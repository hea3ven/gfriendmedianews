package com.hea3ven.gfriendmedianews.mods.medianews.source

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.mods.medianews.model.YouTubeNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.IOException
import java.util.*

class YouTubeNewsSource : NewsSource() {
    private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.mods.medianews.source.YouTubeNewsSource")

    override val verb: String
        get() = "posted to YouTube"

    fun fetchNews(sourceConfig: YouTubeNewsConfig): List<NewsPost> {
        val channelsReq = youtube.channels().list("contentDetails")
        channelsReq.forUsername = sourceConfig.userName
        channelsReq.fields = "items/contentDetails"
        channelsReq.maxResults = 5
        val channels = try {
            channelsReq.execute().items
        } catch (e: IOException) {
            logger.warn("Could not read from youtube")
            logger.trace("Error connecting to youtube", e)
            return listOf()
        }
        val uploadsId = channels[0].contentDetails.relatedPlaylists.uploads
        val playlistReq = youtube.playlistItems().list("snippet,contentDetails")
        playlistReq.playlistId = uploadsId
        val playlistItems = playlistReq.execute().items
        val result = playlistItems.asSequence().map {
            val url = "https://youtube.com/watch?v=" + it.contentDetails.videoId
            val thumbnailUrl = it.snippet.thumbnails.standard.url
            val text = it.snippet.title
            val date = Date(it.snippet.publishedAt.value)
            val userName = it.snippet.channelTitle
            val userUrl = "http://www.youtube.com/channel/" + it.snippet.channelId
            val userIcon = "http://i.ytimg.com/i/" + it.snippet.channelId + "/1.jpg"
            NewsPost(Color(255, 0, 0), date, userName, userUrl, userIcon, this, url, text, listOf(thumbnailUrl))
        }.sortedBy { it.date.time }.filter { it.date.after(sourceConfig.lastDate) }.toList()
        if (result.isNotEmpty()) sourceConfig.lastDate = result.last().date
        return result
    }

    companion object {
        val youtube: YouTube = YouTube.Builder(NetHttpTransport(), JacksonFactory(), HttpRequestInitializer { })
                .setYouTubeRequestInitializer(YouTubeRequestInitializer(Config.youtubeApiKey))
                .setApplicationName("gfriend-media-news").build()
    }
}