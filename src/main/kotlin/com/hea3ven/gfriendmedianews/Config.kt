package com.hea3ven.gfriendmedianews

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Config {
	val discordApiToken: String

	var twitterConsumerKey: String

	var twitterConsumerSecret: String

	var twitterAccessToken: String

	var twitterAccessSecret: String

	val youtubeApiKey: String

	val dateFmt: DateFormat

	init {
		val props = Properties()
		props.load(Config::class.java.getResourceAsStream("/config.properties"))
		discordApiToken = props.getProperty("discordApiToken")
		twitterConsumerKey = props.getProperty("twitterConsumerKey")
		twitterConsumerSecret = props.getProperty("twitterConsumerSecret")
		twitterAccessToken = props.getProperty("twitterAccessToken")
		twitterAccessSecret = props.getProperty("twitterAccessSecret")
		youtubeApiKey = props.getProperty("youtubeApiKey")

		dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		dateFmt.timeZone = TimeZone.getTimeZone("GMT+0900")
	}

}