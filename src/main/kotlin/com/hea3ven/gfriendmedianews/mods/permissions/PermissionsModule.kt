package com.hea3ven.gfriendmedianews.mods.permissions

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.commands.ActionCommand
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.permissions.DbPermissionManager
import com.hea3ven.gfriendmedianews.mods.permissions.dao.UserPermissionsDao
import com.hea3ven.gfriendmedianews.mods.permissions.dao.UserPermissionsDaoFactory
import com.hea3ven.gfriendmedianews.mods.permissions.model.Permission
import com.hea3ven.gfriendmedianews.mods.permissions.model.UserPermissions
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import com.hea3ven.gfriendmedianews.util.parseUserId
import de.btobastian.javacord.entities.message.Message
import org.slf4j.LoggerFactory

class PermissionsModule(private val bot: ChinguBot) : Module {

    private val logger = LoggerFactory.getLogger(PermissionsModule::class.java)

    override val commands = listOf(ActionCommand("permset", " **\$permset**: Set permissions.", this::onPermSet, true))

    init {
        bot.persistence.registerDaoFactory(UserPermissionsDao::class.java, UserPermissionsDaoFactory())
    }

    override fun onConnect(tx: PersistenceTransaction) {
        super.onConnect(tx)
        bot.commandManager.permissionManager = DbPermissionManager(bot.persistence)
    }

    fun onPermSet(message: Message, args: String?) {
        val splitArgs = args?.split(" ")?.toTypedArray() ?: arrayOf()
        val userId: String? = parseUserId(splitArgs[0])
        val permissionKey: String = splitArgs[1]
        val permissionValue = splitArgs[2].toBoolean()

        if (userId == null) {
            message.reply("Invalid user")
        }

        bot.persistence.beginTransaction().use { sess ->
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
    }

}
