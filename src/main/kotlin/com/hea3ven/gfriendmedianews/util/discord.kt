package com.hea3ven.gfriendmedianews.util

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User

private val urlRegex = """https?://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)""".toRegex()

fun  escapeLinks(text: String): String {
	return urlRegex.replace(text,"<$0>")
}

fun Server.isAdmin(discord: DiscordAPI, user: User): Boolean {
	val botRolePos = discord.yourself.getRoles(this).filter { it.hoist }.map { it.position }.min()
	val roles = user.getRoles(this)
	return !(roles == null || roles.isEmpty() || roles.all { it.position < botRolePos!! })
}
