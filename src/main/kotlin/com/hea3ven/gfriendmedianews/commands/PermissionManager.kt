package com.hea3ven.gfriendmedianews.commands

import com.hea3ven.gfriendmedianews.util.isAdmin
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User

interface PermissionManager {

    fun hasPermission(user: User, server: Server, discord: DiscordAPI, command: Command): Boolean {
        if (!command.requiresAdmin) {
            return true
        }
        if (server.isAdmin(discord, user)) {
            return true
        }
        return hasPermission(user, server, discord, "command." + command.name)
    }


    fun hasPermission(user: User, server: Server, discord: DiscordAPI, key: String): Boolean
}
