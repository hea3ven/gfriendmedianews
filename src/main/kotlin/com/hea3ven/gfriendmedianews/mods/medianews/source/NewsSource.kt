package com.hea3ven.gfriendmedianews.mods.medianews.source

import com.hea3ven.gfriendmedianews.mods.medianews.SourceConfig
import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost

abstract class NewsSource {

	abstract val verb: String

	abstract fun fetchNews(sourceConfig: SourceConfig): List<NewsPost>
}