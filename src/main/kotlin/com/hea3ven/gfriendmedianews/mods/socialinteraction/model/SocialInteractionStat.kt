package com.hea3ven.gfriendmedianews.mods.socialinteraction.model

import org.mongodb.morphia.annotations.Entity
import java.util.Date

@Entity(noClassnameStored = true)
class SocialInteractionStat(var serverId: String, var channelId: String, var type: InteractionType,
        var sourceId: String, var targetId: String, var date: Date) {

    constructor() : this("", "", InteractionType.HUG, "", "")

    constructor(serverId: String, channelId: String, type: InteractionType, sourceId: String, targetId: String) : this(
            serverId, channelId, type, sourceId, targetId, Date())
}

enum class InteractionType(val command: String, val verb: String, val help: String) {
    SLAP("slap", "slapped", "Slap something."), HUG("hug", "hugged", "Hug something.");
}