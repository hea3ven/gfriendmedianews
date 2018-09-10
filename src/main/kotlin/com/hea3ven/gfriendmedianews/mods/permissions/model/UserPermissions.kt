package com.hea3ven.gfriendmedianews.mods.permissions.model

import de.btobastian.javacord.entities.Server
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id

@Entity(noClassnameStored = true)
class UserPermissions(@Id var id: ObjectId, var userId: String, var permissions: Set<Permission>) {

    constructor() : this("")

    constructor(userId: String) : this(ObjectId(), userId, setOf())

    fun getPermission(server: Server, key: String): Boolean {
        return permissions.firstOrNull { server.id == it.serverId && it.key == key }?.value ?: false
    }
}

class Permission(var serverId: String, var key: String, var value: Boolean) {

    constructor() : this("", "", false)
}