package com.hea3ven.gfriendmedianews.mods.medianews.source

import com.google.gson.JsonParser
import com.hea3ven.gfriendmedianews.mods.medianews.model.InstagramNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import java.util.regex.Pattern

class InstagramNewsSource : NewsSource() {
    private val logger = LoggerFactory.getLogger(
            "com.hea3ven.gfriendmedianews.mods.medianews.source.InstagramNewsSource")

    override val verb: String
        get() = "posted to Instagram"

    private var count = 0

    fun fetchNews(sourceConfig: InstagramNewsConfig): List<NewsPost> {
        if (count++ < 5)
            return listOf()
        try {
            URL("https://www.instagram.com/" + sourceConfig.userName).openStream().use { stream ->
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
                            val date = Date(post.get("taken_at_timestamp").asLong * 1000)
                            val url = "https://www.instagram.com/p/" + post.asJsonObject.get("shortcode").asString
                            val picUrl = post.get("display_url").asString
                            val text = post.getAsJsonObject("edge_media_to_caption")
                                    .getAsJsonArray("edges")[0]
                                    .asJsonObject.getAsJsonObject("node")
                                    .get("text").asString
                            NewsPost(Color(205, 72, 107), date, userName, userUrl, userIcon, this, url,
                                     text, listOf(picUrl))
                        }
                        .sortedBy { it.date.time }
                        .filter { it.date.after(sourceConfig.lastFetch) }
                if (result.isNotEmpty())
                    sourceConfig.lastFetch = result.last().date
                return result
            }
        } catch (e: IOException) {

            logger.warn("Could not read from instagram")
            logger.trace("Error connecting to instagram", e)
            return listOf()
        }
    }
}