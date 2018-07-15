package com.hea3ven.gfriendmedianews.mods.medianews.source

import com.google.gson.JsonParser
import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.mods.medianews.SourceConfig
import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import com.hea3ven.gfriendmedianews.util.escapeLinks
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.text.ParseException
import java.util.*
import java.util.regex.Pattern

class InstagramNewsSource : NewsSource() {
	private val logger = LoggerFactory.getLogger(
			"com.hea3ven.gfriendmedianews.mods.medianews.source.InstagramNewsSource")

	override val verb: String
		get() = "posted to Instagram"

	private var count = 0

	override fun fetchNews(sourceConfig: SourceConfig): List<NewsPost> {
		if (count++ < 5)
			return listOf()
		var lastDate: Date
		try {
			lastDate = Config.dateFmt.parse(sourceConfig.stateData)
		} catch (e: ParseException) {
			lastDate = Date(0)
		}
		try {
			URL("https://www.instagram.com/" + sourceConfig.connectionData).openStream().use { stream ->
				val posts = try {
					val pageText = InputStreamReader(stream).readText()
					val m = Pattern.compile("window\\._sharedData = ([^;]*);").matcher(pageText)
					m.find()
					JsonParser().parse(m.group(1)).asJsonObject
				} catch (e: Exception) {
					logger.warn("Could not read from instagram")
					logger.trace("Error connecting to instagram", e)
					return listOf()
				}
				val user = posts.getAsJsonObject("entry_data")
						.getAsJsonArray("ProfilePage")[0]
						.asJsonObject.getAsJsonObject("graphql")
						.getAsJsonObject("user")
				val userName = "@" + user.get("username").asString
				val userUrl = "https://www.instagram.com/" + user.get("username").asString
				val userIcon = user.get("profile_pic_url_hd").asString
				val result = user
						.getAsJsonObject("edge_owner_to_timeline_media")
						.getAsJsonArray("edges")
						.map { it.asJsonObject.getAsJsonObject("node") }
						.map { post ->
							val caption = post.asJsonObject.getAsJsonObject("caption")
//							val img = post.asJsonObject.getAsJsonObject("images")

//							val date = Date(caption.get("created_time").asLong * 1000)
							val date = Date(post.get("taken_at_timestamp").asLong * 1000)

//							val userName = "@" + caption.getAsJsonObject("from").get("username").asString
//							val userUrl = "https://www.instagram.com/" + caption.getAsJsonObject("from").get(
//									"username").asString
//							val userIcon = caption.getAsJsonObject("from").get("profile_picture").asString

//							val url = post.asJsonObject.get("link").asString
							val url = "https://www.instagram.com/p/" + post.asJsonObject.get("shortcode").asString
//							val picUrl = img.getAsJsonObject("standard_resolution").get("url").asString
							val picUrl = post.get("display_url").asString
//							val text = escapeLinks(caption.get("text").asString)
							val text = post.getAsJsonObject("edge_media_to_caption")
									.getAsJsonArray("edges")[0]
									.asJsonObject.getAsJsonObject("node")
									.get("text").asString
							NewsPost(Color(205, 72, 107), date, userName, userUrl, userIcon, this, url,
									text, listOf(picUrl))
						}
						.sortedBy { it.date.time }
						.filter { it.date.after(lastDate) }
				if (result.isNotEmpty())
					sourceConfig.stateData = Config.dateFmt.format(result.last().date)
				return result
			}
		} catch (e: IOException) {

			logger.warn("Could not read from instagram")
			logger.trace("Error connecting to instagram", e)
			return listOf()
		}
	}
}