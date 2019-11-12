package com.hea3ven.gfriendmedianews.util

import com.google.common.util.concurrent.FutureCallback
import com.hea3ven.gfriendmedianews.commands.CommandManager
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.persistance.Persistence
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.listener.server.ServerJoinListener
import de.btobastian.javacord.listener.server.ServerLeaveListener
import org.slf4j.LoggerFactory

abstract class DiscordBot(val persistence: Persistence, val discord: DiscordAPI, prefix: String) {

    abstract val modules: List<Module>

    var stop = false

    private val logger = LoggerFactory.getLogger(javaClass)

    val commandManager = CommandManager(prefix, this)

    fun start() {
        modules.flatMap(Module::commands).forEach(commandManager::registerCommand)
        logger.info("Loaded modules: " + modules.joinToString { it::class.java.simpleName })

        logger.info("Connecting to discord")
        discord.setAutoReconnect(true)
        discord.connect(object : FutureCallback<DiscordAPI> {
            override fun onSuccess(result: DiscordAPI?) {
                logger.info("Connection established")
                try {
                    onConnect()
                } catch (e: Throwable) {
                    logger.error("Error initializing", e)
                }
            }

            override fun onFailure(t: Throwable) {
                logger.error("Could not connect", t)
            }

        })
        while (!stop) {
            Thread.sleep(5000)
        }
    }

    fun onConnect() {
        persistence.beginTransaction().use { tx ->
            modules.forEach { m -> m.onConnect(tx) }
        }
        discord.registerListener(commandManager)
        discord.registerListener(object : ServerJoinListener, ServerLeaveListener {
            override fun onServerJoin(api: DiscordAPI, server: Server) {
                logger.info("Joined to {}", server.name)
            }

            override fun onServerLeave(api: DiscordAPI, server: Server) {
                logger.info("Leaved to {}", server.name)
            }

        })
        discord.game = "with buddies (\$help)"
    }

}