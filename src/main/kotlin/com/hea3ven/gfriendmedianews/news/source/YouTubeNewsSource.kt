package com.hea3ven.gfriendmedianews.news.source

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.domain.SourceConfig
import com.hea3ven.gfriendmedianews.news.post.NewsPost
import java.text.ParseException
import java.util.*

class YouTubeNewsSource : NewsSource() {
	override val verb: String
		get() = "posted to YouTube"

	override fun fetchNews(sourceConfig: SourceConfig): List<NewsPost> {
		var lastDate: Date
		try {
			lastDate = Config.dateFmt.parse(sourceConfig.stateData)
		} catch (e: ParseException) {
			lastDate = Date(0)
		}
		val channelsReq = youtube.channels().list("contentDetails")
		channelsReq.forUsername = sourceConfig.connectionData
//		channelsReq.forUsername = "gfrdofficial"
		channelsReq.fields = "items/contentDetails"
		channelsReq.maxResults = 5
		val channels = channelsReq.execute().items
		val uploadsId = channels[0].contentDetails.relatedPlaylists.uploads
		val playlistReq = youtube.playlistItems().list("snippet,contentDetails")
		playlistReq.playlistId = uploadsId
		val playlistItems = playlistReq.execute().items
		val result = playlistItems
				.map {
					val text = "https://youtube.com/watch?v=" + it.contentDetails.videoId
					val date = Date(it.snippet.publishedAt.value)
					val userName = it.snippet.channelTitle
					NewsPost(date, userName, this, text)
				}
				.sortedBy { it.date.time }
				.filter { it.date.after(lastDate) }
		if (result.isNotEmpty())
			sourceConfig.stateData = Config.dateFmt.format(result.last().date)
		return result
	}

	companion object {
		val youtube: YouTube = YouTube.Builder(NetHttpTransport(), JacksonFactory(),
				HttpRequestInitializer { })
				.setYouTubeRequestInitializer(YouTubeRequestInitializer(Config.youtubeApiKey))
				.setApplicationName("gfriend-media-news")
				.build()
	}
}