package com.hea3ven.gfriendmedianews.mods.medianews.model

import com.hea3ven.gfriendmedianews.mods.medianews.post.NewsPost
import org.apache.commons.lang3.builder.EqualsBuilder
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id

@Entity("NewsConfig")
abstract class NewsConfig(@Id var id: ObjectId, var serverId: String, var channelId: String) {
    abstract val label: String

    abstract val type: String

    constructor(serverId: String, channel: String) : this(ObjectId(), serverId, channel)

    abstract fun fetchNews(): List<NewsPost>

    open fun equalsData(other: NewsConfig): Boolean {
        return EqualsBuilder().append(serverId, other.serverId).append(channelId, other.channelId).build()
    }
}

