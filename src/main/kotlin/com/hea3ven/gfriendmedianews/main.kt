package com.hea3ven.gfriendmedianews

import com.google.common.util.concurrent.FutureCallback
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord
import de.btobastian.javacord.listener.message.MessageCreateListener
import twitter4j.Paging
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
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