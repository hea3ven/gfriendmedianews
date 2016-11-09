package com.hea3ven.gfriendmedianews

import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.google.common.util.concurrent.FutureCallback
import com.google.gson.JsonParser
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord
import de.btobastian.javacord.listener.message.MessageCreateListener
import twitter4j.Paging
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>) {
	val api = Javacord.getApi(Config.discordApiToken, true)
	api.connect(object : FutureCallback<DiscordAPI> {
		override fun onSuccess(result: DiscordAPI?) {
			val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			dateFmt.timeZone = TimeZone.getTimeZone("GMT+0900")
			val twitter = TwitterFactory.getSingleton()
			twitter.setOAuthConsumer(Config.twitterConsumerKey, Config.twitterConsumerSecret)
			twitter.oAuthAccessToken = AccessToken(Config.twitterAccessToken, Config.twitterAccessSecret)

			val youtube = YouTube.Builder(NetHttpTransport(), JacksonFactory(),
					HttpRequestInitializer { }).setYouTubeRequestInitializer(
					YouTubeRequestInitializer(Config.youtubeApiKey)).setApplicationName(
					"gfriend-media-news").build()

			api.registerListener(MessageCreateListener { api, message ->
				val content = message.content
				if (content.toLowerCase() == "ping") {
					message.reply("pong")
				}
				if (content.startsWith("/slap")) {
					message.delete()
					message.reply(message.author.mentionTag + " slapped " + content.substring(6))
				} else if (content.startsWith("/init")) {
					val statuses = twitter.timelines().getUserTimeline("@GFRDOfficial", Paging(1, 5))
					statuses.forEach {
						val date = dateFmt.format(it.createdAt)
						if (it.retweetedStatus == null) {
							var text = it.text
							it.urlEntities.filter { url ->
								url.expandedURL.contains(it.id.toString())
							}.forEach { text = text.replace(it.url, "") }
							val title = date + " KST **@" + it.user.screenName + "** tweeted:\n"
							message.reply("==========\n\n" + title + text + "\n")
						} else {
							var text = it.retweetedStatus.text
							it.retweetedStatus.urlEntities.filter { url ->
								url.expandedURL.contains(it.retweetedStatus.id.toString())
							}.forEach { text = text.replace(it.url, "") }
							val title = date + " KST **@" + it.user.screenName + "** retweeted **@" +
									it.retweetedStatus.user.screenName + "**'s tweet:\n"
							message.reply("==========\n\n" + title + text + "\n")
						}
					}

					URL("https://www.instagram.com/gfriendofficial/media").openStream().use { stream ->
						var posts = JsonParser().parse(InputStreamReader(stream)).asJsonObject
						posts.getAsJsonArray("items").forEach { post ->
							val caption = post.asJsonObject.getAsJsonObject("caption")
							val text = caption.get("text").asString
							val date = dateFmt.format(Date(caption.get("created_time").asLong * 1000))
							val username = caption.getAsJsonObject("from").get("username").asString
							val img = post.asJsonObject.getAsJsonObject("images")
							val picUrl = img.getAsJsonObject("standard_resolution").get("url").asString

							val title = date + " KST **@" + username + "** posted to instagram:\n"
							message.reply("==========\n\n" + title + text + "\n" + picUrl + "\n")
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
						val date = dateFmt.format(Date(it.snippet.publishedAt.value))
//				val text = "*" + it.snippet.title + "*\n<https://youtube.com/watch?v=" + it.contentDetails.videoId + ">\n" + it.snippet.thumbnails.high.url
						val text = "https://youtube.com/watch?v=" + it.contentDetails.videoId
						val username = it.snippet.channelTitle
						val title = "__" + date + " KST **" + username + "** posted to youtube:__\n"
						message.reply("==========\n\n" + title + text + "\n")
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
