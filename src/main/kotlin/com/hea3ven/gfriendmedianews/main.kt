package com.hea3ven.gfriendmedianews

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.google.common.util.concurrent.FutureCallback
import com.google.gson.JsonParser
import com.hea3ven.gfriendmedianews.news.post.NewsPost
import com.hea3ven.gfriendmedianews.news.post.TwitterNewsPost
import com.hea3ven.gfriendmedianews.news.source.InstagramNewsSource
import com.hea3ven.gfriendmedianews.news.source.TwitterNewsSource
import com.hea3ven.gfriendmedianews.news.source.YouTubeNewsSource
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord
import de.btobastian.javacord.listener.message.MessageCreateListener
import twitter4j.Paging
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import java.io.InputStreamReader
import java.net.URL
import java.util.*

fun main(args: Array<String>) {
	val api = Javacord.getApi(Config.discordApiToken, true)
	api.connect(object : FutureCallback<DiscordAPI> {
		override fun onSuccess(result: DiscordAPI?) {
			val twitter = TwitterFactory.getSingleton()
			twitter.setOAuthConsumer(Config.twitterConsumerKey, Config.twitterConsumerSecret)
			twitter.oAuthAccessToken = AccessToken(Config.twitterAccessToken, Config.twitterAccessSecret)

			val youtube = YouTube.Builder(NetHttpTransport(), JacksonFactory(),
					HttpRequestInitializer { }).setYouTubeRequestInitializer(
					YouTubeRequestInitializer(Config.youtubeApiKey)).setApplicationName(
					"gfriend-media-news").build()

			val channel = api.channels.find { it.name.equals("official_media") }!!

			val twtSrc = TwitterNewsSource()
			val istgSrc = InstagramNewsSource()
			val youSrc = YouTubeNewsSource()

			api.registerListener(MessageCreateListener { api, message ->
				val content = message.content
				if (content.toLowerCase() == "ping") {
					message.reply("pong")
				}
				if (content.startsWith("/slap")) {
					message.delete()
					message.reply(message.author.mentionTag + " slapped " + content.substring(6))
				} else if (content.startsWith("/init")) {
					val newsPosts = mutableListOf<NewsPost>()
					val statuses = twitter.timelines().getUserTimeline("@GFRDOfficial", Paging(1, 5))
					statuses.forEach {
						val date = it.createdAt
						val text = if (it.retweetedStatus == null) it.text else it.retweetedStatus.text
						val userName = "@" + it.user.screenName
						var rtUserName = it.retweetedStatus?.user?.screenName
						if (rtUserName != null)
							rtUserName = "@" + rtUserName
						newsPosts.add(TwitterNewsPost(date, userName, rtUserName, twtSrc, text))
					}

					URL("https://www.instagram.com/gfriendofficial/media").openStream().use { stream ->
						var posts = JsonParser().parse(InputStreamReader(stream)).asJsonObject
						posts.getAsJsonArray("items").forEach { post ->
							val caption = post.asJsonObject.getAsJsonObject("caption")
							val img = post.asJsonObject.getAsJsonObject("images")

							val date = Date(caption.get("created_time").asLong * 1000)
							val userName = "@" + caption.getAsJsonObject("from").get("username").asString
							val picUrl = img.getAsJsonObject("standard_resolution").get("url").asString
							val text = caption.get("text").asString + "\n" + picUrl
							newsPosts.add(NewsPost(date, userName, istgSrc, text))
						}
					}

					val channelsReq = youtube.channels().list("contentDetails")
					channelsReq.forUsername = "gfrdofficial"
					channelsReq.fields = "items/contentDetails"
					val channels = channelsReq.execute().items
					val uploadsId = channels.get(0).contentDetails.relatedPlaylists.uploads
					val playlistReq = youtube.playlistItems().list("snippet,contentDetails")
					playlistReq.playlistId = uploadsId
					val playlistItems = playlistReq.execute().items
					playlistItems.forEach {
						val text = "https://youtube.com/watch?v=" + it.contentDetails.videoId
						val date = Date(it.snippet.publishedAt.value)
						val userName = it.snippet.channelTitle
						newsPosts.add(NewsPost(date, userName, youSrc, text))
					}
					newsPosts.forEach {
						it.post(channel)
					}
				}
			})
		}

		override fun onFailure(t: Throwable?) {
			t?.printStackTrace()
		}
	})
	while (true)
		Thread.sleep(5000)
}

