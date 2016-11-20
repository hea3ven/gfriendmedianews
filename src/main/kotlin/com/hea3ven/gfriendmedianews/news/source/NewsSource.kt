package com.hea3ven.gfriendmedianews.news.source

import com.hea3ven.gfriendmedianews.domain.SourceConfig
import com.hea3ven.gfriendmedianews.news.post.NewsPost

abstract class NewsSource {

	abstract val verb: String

	abstract fun fetchNews(sourceConfig: SourceConfig): List<NewsPost>
}