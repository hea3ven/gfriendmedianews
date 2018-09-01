package com.hea3ven.gfriendmedianews.mods.medianews.model

import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import com.hea3ven.gfriendmedianews.mods.medianews.source.YouTubeNewsSource
import org.apache.commons.lang3.builder.EqualsBuilder
import org.mongodb.morphia.annotations.Entity
import java.util.Date

val youTubeSource = YouTubeNewsSource()

@Entity("NewsConfig")
class YouTubeNewsConfig(serverId: String, channel: String, var userName: String,
                        var lastDate: Date = Date(0)) : NewsConfig(serverId, channel) {

    override val label = userName

    override val type = "youtube"

    constructor() : this("","","")

    override fun fetchNews(): List<NewsPost> {
        return youTubeSource.fetchNews(this)
    }

    override fun equalsData(other: NewsConfig): Boolean {
        return other is YouTubeNewsConfig && super.equalsData(other) && EqualsBuilder().append(userName,
                                                                                               other.userName).build()
    }
}