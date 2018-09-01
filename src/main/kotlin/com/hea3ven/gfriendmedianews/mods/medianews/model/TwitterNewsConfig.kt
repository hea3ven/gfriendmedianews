package com.hea3ven.gfriendmedianews.mods.medianews.model

import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import com.hea3ven.gfriendmedianews.mods.medianews.source.TwitterNewsSource
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mongodb.morphia.annotations.Entity

val twitterSource = TwitterNewsSource()

@Entity("NewsConfig")
class TwitterNewsConfig(serverId: String, channel: String, var userName: String, var lastId: Long? = null) : NewsConfig(
        serverId, channel) {

    override val label = userName

    override val type = "twitter"

    constructor() : this("", "", "")

    override fun fetchNews(): List<NewsPost> {
        return twitterSource.fetchNews(this)
    }

    override fun equalsData(other: NewsConfig): Boolean {
        return other is TwitterNewsConfig && super.equalsData(other) && EqualsBuilder().append(userName,
                                                                                               other.userName).build()
    }
}