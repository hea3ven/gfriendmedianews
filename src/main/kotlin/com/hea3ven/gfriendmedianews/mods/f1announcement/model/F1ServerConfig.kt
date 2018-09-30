package com.hea3ven.gfriendmedianews.mods.f1announcement.model

import org.mongodb.morphia.annotations.Entity

@Entity(noClassnameStored = true)
class F1ServerConfig(
        var serverId: String,
        var enabled: Boolean,
        var mentionRole: String?,
        var channel: String?) {

    constructor() : this("")

    constructor(serverId: String) : this(serverId, false, null, null)
}