package com.hea3ven.gfriendmedianews.mods.medianews.source

import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.mods.medianews.model.TwitterNewsConfig
import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import com.hea3ven.gfriendmedianews.mods.medianews.post.TwitterNewsPost
import org.slf4j.LoggerFactory
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

class TwitterNewsSource : NewsSource() {
    private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.mods.medianews.source.TwitterNewsSource")

    override val verb: String
        get() = "Posted on twitter"

    fun fetchNews(sourceConfig: TwitterNewsConfig): List<NewsPost> {
        try {
            if (sourceConfig.lastId == null) {
                sourceConfig.lastId = twitter.timelines().getUserTimeline(sourceConfig.userName, Paging(1, 5)).last().id
            }
            logger.debug("Getting tweets from " + sourceConfig.userName + " since tweet id " + sourceConfig.lastId)
            val statuses = twitter.timelines().getUserTimeline(sourceConfig.userName,
                                                               Paging(1, 5, sourceConfig.lastId!!))
            logger.trace("Got " + statuses.size + " tweets")
            return statuses.reversed().map {
                val date = it.createdAt
                val url = if (it.retweetedStatus == null) {
                    "https://twitter.com/" + it.user.screenName + "/status/" + it.id
                } else {
                    "https://twitter.com/" + it.user.screenName + "/status/" + it.id
                }
                val text = if (it.retweetedStatus == null) {
                    it.text
                } else {
                    it.retweetedStatus.text
                }
                //						text = escapeLinks(text)
                val mediaUrls = when {
                    it.mediaEntities.isNotEmpty() -> it.mediaEntities.map { e -> e.mediaURL }
                    it.urlEntities.isNotEmpty() -> listOf(it.urlEntities.last()!!.url)
                    else -> listOf()
                }
                val userName = "@" + it.user.screenName
                var rtUserName = it.retweetedStatus?.user?.screenName
                if (rtUserName != null) rtUserName = "@$rtUserName"
                sourceConfig.lastId = it.id
                TwitterNewsPost(date, userName, "https://twitter.com/" + it.user.screenName,
                                it.user.miniProfileImageURL, rtUserName, this, url, text, mediaUrls)
            }
        } catch (e: TwitterException) {
            logger.warn("Could not read from twitter")
            logger.trace("Error connecting to twitter", e)
            return listOf()
        }
    }


    companion object {
        val twitter: Twitter = TwitterFactory.getSingleton()

        init {
            twitter.setOAuthConsumer(Config.twitterConsumerKey, Config.twitterConsumerSecret)
            twitter.oAuthAccessToken = AccessToken(Config.twitterAccessToken, Config.twitterAccessSecret)
        }
    }
}