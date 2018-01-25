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
			URL("https://www.instagram.com/" + sourceConfig.connectionData + "/media").openStream().use { stream ->
				val posts = try {
					JsonParser().parse(InputStreamReader(stream)).asJsonObject
				} catch (e: Exception) {
					logger.warn("Could not read from instagram")
					logger.trace("Error connecting to instagram", e)
					return listOf()
				}
				val result = posts.getAsJsonArray("items")
						.map { post ->
							val caption = post.asJsonObject.getAsJsonObject("caption")
							val img = post.asJsonObject.getAsJsonObject("images")

							val date = Date(caption.get("created_time").asLong * 1000)
							val userName = "@" + caption.getAsJsonObject("from").get("username").asString
							val userUrl = "https://www.instagram.com/" + caption.getAsJsonObject("from").get(
									"username").asString
							val userIcon = caption.getAsJsonObject("from").get("profile_picture").asString
							val url = post.asJsonObject.get("link").asString
							val picUrl = img.getAsJsonObject("standard_resolution").get("url").asString
							val text = escapeLinks(caption.get("text").asString)
							NewsPost(Color(205, 72, 107), date, userName, userUrl, userIcon, this, url,
									text, listOf(picUrl))
						}
						.sortedBy { it.date.time }
						.filter { it.date.after(lastDate) }
				if (result.isNotEmpty())
					sourceConfig.stateData = Config.dateFmt.format(result.last().date)
				return result
			}
		} catch(e: IOException) {

			logger.warn("Could not read from instagram")
			logger.trace("Error connecting to instagram", e)
			return listOf()
		}
	}
}