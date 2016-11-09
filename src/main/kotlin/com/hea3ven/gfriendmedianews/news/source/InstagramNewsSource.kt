package com.hea3ven.gfriendmedianews.news.source

import com.hea3ven.gfriendmedianews.news.source.NewsSource

class InstagramNewsSource : NewsSource() {
	override val verb: String
		get() = "posted to Instagram"

}