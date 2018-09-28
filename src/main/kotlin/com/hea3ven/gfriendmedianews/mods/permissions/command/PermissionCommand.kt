package com.hea3ven.gfriendmedianews.mods.permissions.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.permissions.PermissionsModule
import com.hea3ven.gfriendmedianews.mods.permissions.dao.UserPermissionsDao
import com.hea3ven.gfriendmedianews.mods.permissions.model.Permission
import com.hea3ven.gfriendmedianews.mods.permissions.model.UserPermissions
import com.hea3ven.gfriendmedianews.util.parseUserId
import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.inf.Namespace

class PermissionCommand(private val mod: PermissionsModule) :
        Command("perm", "Change user permissions.", requiresAdmin = true) {

    init {
        argParser.addArgument("action").choices("set", "unset")
        argParser.addArgument("user")
        argParser.addArgument("key")
    }

    override fun action(message: Message, args: Namespace) {
        val permissionValue = args.getString("action") == "set"
        val userId: String? = parseUserId(args.getString("user"))
        val permissionKey: String = args.getString("key")

        if (userId == null) {
            message.reply("Invalid user")
        }

        mod.bot.persistence.beginTransaction().use { sess ->
            val userPerms = sess.getDao(UserPermissionsDao::class.java).findByUserId(message.author) ?: UserPermissions(
                    message.author.id)
            val perm = userPerms.permissions.firstOrNull { it.serverId == message.channelReceiver.server.id && it.key == permissionKey }
            if (perm == null) {
                userPerms.permissions = userPerms.permissions.plus(
                        Permission(message.channelReceiver.server.id, permissionKey, permissionValue))
            } else {
                perm.value = permissionValue
            }

            sess.getDao(UserPermissionsDao::class.java).persist(userPerms)
        }
        logger.info("Changed permission for $userId: $permissionKey = $permissionValue")
        message.reply("Permission '$permissionKey' set to $permissionValue")
    }
}