package com.hea3ven.gfriendmedianews

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Properties
import java.util.TimeZone

object Config {
    val discordApiToken: String

    var twitterConsumerKey: String

    var twitterConsumerSecret: String

    var twitterAccessToken: String

    var twitterAccessSecret: String

    val youtubeApiKey: String

    val dateFmt: DateFormat

    var mongoDbHost: String

    var mongoDbPort: Int

    init {
        val props = Properties()
        props.load(Config::class.java.getResourceAsStream("/config.properties"))
        discordApiToken = props.getProperty("discordApiToken")
        twitterConsumerKey = props.getProperty("twitterConsumerKey")
        twitterConsumerSecret = props.getProperty("twitterConsumerSecret")
        twitterAccessToken = props.getProperty("twitterAccessToken")
        twitterAccessSecret = props.getProperty("twitterAccessSecret")
        youtubeApiKey = props.getProperty("youtubeApiKey")
        mongoDbHost = props.getProperty("mongo.host")
        mongoDbPort = props.getProperty("mongo.port").toInt()

        dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFmt.timeZone = TimeZone.getTimeZone("GMT+0900")
    }

}