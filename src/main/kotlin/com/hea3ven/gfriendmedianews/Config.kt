package com.hea3ven.gfriendmedianews

import java.util.*

object Config {
	val discordApiToken: String

	var twitterConsumerKey: String

	var twitterConsumerSecret: String

	var twitterAccessToken: String

	var twitterAccessSecret: String

	val youtubeApiKey: String

	init {
		val props = Properties()
		props.load(Config::class.java.getResourceAsStream("/config.properties"))
		discordApiToken = props.getProperty("discordApiToken")
		twitterConsumerKey = props.getProperty("twitterConsumerKey")
		twitterConsumerSecret = props.getProperty("twitterConsumerSecret")
		twitterAccessToken = props.getProperty("twitterAccessToken")
		twitterAccessSecret = props.getProperty("twitterAccessSecret")
		youtubeApiKey = props.getProperty("youtubeApiKey")
	}

}