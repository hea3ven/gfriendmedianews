package com.hea3ven.gfriendmedianews.news.post

import com.hea3ven.gfriendmedianews.news.source.NewsSource
import com.hea3ven.gfriendmedianews.news.post.NewsPost
import java.util.*

class TwitterNewsPost(date: Date, userName: String, val rtUserName: String?, newsSrc: NewsSource, content: String) :
		NewsPost(date, userName, newsSrc, content) {

	override fun userAction(): String {
		return when (rtUserName) {
			null -> super.userAction()
			else -> "**$userName** retweeted **$rtUserName**'s tweet"
		}
	}
}