package com.hea3ven.gfriendmedianews.mods.medianews.post

import com.hea3ven.gfriendmedianews.mods.medianews.source.NewsSource
import java.awt.Color
import java.util.*

class TwitterNewsPost(date: Date, userName: String, userUrl: String, userIcon: String,
		val rtUserName: String?, newsSrc: NewsSource, url: String, content: String, mediaUrls: List<String>) :
		NewsPost(Color(0, 132, 180), date, userName, userUrl, userIcon, newsSrc, url, content, mediaUrls) {

	override fun userAction(): String {
		return when (rtUserName) {
			null -> super.userAction()
			else -> "Retweeted $rtUserName's tweet"
		}
	}
}