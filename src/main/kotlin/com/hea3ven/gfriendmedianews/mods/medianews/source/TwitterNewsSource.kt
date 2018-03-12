package com.hea3ven.gfriendmedianews.mods.medianews.source

import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.mods.medianews.SourceConfig
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

	override fun fetchNews(sourceConfig: SourceConfig): List<NewsPost> {
		try {
			var lastId: Long
			try {
				lastId = sourceConfig.stateData.toLong()
			} catch (e: NumberFormatException) {
				lastId = twitter.timelines().getUserTimeline(sourceConfig.connectionData,
						Paging(1, 5)).last().id
			}
			logger.debug("Getting tweets from " + sourceConfig.connectionData + " since tweet id " + lastId)
			val statuses = twitter.timelines().getUserTimeline(sourceConfig.connectionData,
					Paging(1, 5, lastId))
			logger.trace("Got " + statuses.size + " tweets")
			return statuses
					.reversed()
					.map {
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
						val mediaUrls = if (it.mediaEntities.isNotEmpty()) {
							it.mediaEntities.map { it.mediaURL }
						} else if (it.urlEntities.isNotEmpty()) {
							listOf(it.urlEntities.last()!!.url)
						} else {
							listOf()
						}
						val userName = "@" + it.user.screenName
						var rtUserName = it.retweetedStatus?.user?.screenName
						if (rtUserName != null)
							rtUserName = "@" + rtUserName
						sourceConfig.stateData = it.id.toString()
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