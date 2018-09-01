package com.hea3ven.gfriendmedianews.mods.medianews.model

import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import com.hea3ven.gfriendmedianews.mods.medianews.source.InstagramNewsSource
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mongodb.morphia.annotations.Entity
import java.util.Date

val instagramSource = InstagramNewsSource()

@Entity("NewsConfig")
class InstagramNewsConfig(serverId: String, channel: String, var userName: String,
                          var lastFetch: Date = Date(0)) : NewsConfig(serverId, channel) {

    override val label = userName

    override val type = "instagram"

    constructor() : this("", "", "")

    override fun fetchNews(): List<NewsPost> {
        return instagramSource.fetchNews(this)
    }

    override fun equalsData(other: NewsConfig): Boolean {
        return other is InstagramNewsConfig && super.equalsData(other) && EqualsBuilder().append(userName,
                                                                                                 other.userName).build()
    }
}