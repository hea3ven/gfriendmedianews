package com.hea3ven.gfriendmedianews.news.source

import com.hea3ven.gfriendmedianews.news.source.NewsSource

class TwitterNewsSource : NewsSource() {
	override val verb: String
		get() = "tweeted"

}