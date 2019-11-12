package com.hea3ven.gfriendmedianews.commands

import com.hea3ven.gfriendmedianews.util.DiscordBot
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.MessageCreateListener
import io.prometheus.client.Counter
import org.slf4j.LoggerFactory

class CommandManager(val prefix: String, val bot: DiscordBot) : MessageCreateListener {
    private val logger = LoggerFactory.getLogger("com.hea3ven.gfriendmedianews.commands.CommandManager")

    private val commands: MutableMap<String, Command> = mutableMapOf()

    private val messageCreateCounter = Counter.build().name("gfmn_discord_message_create")
            .labelNames("server_id", "channel_id").help("Count of messages received.").register()
    private val messageCmdPrefixCreateCounter = Counter.build().name("gfmn_discord_message_command_prefix_create")
            .labelNames("server_id", "channel_id").help("Count of messages with the command prefix received.")
            .register()

    var permissionManager: PermissionManager = DefaultPermissionManager()

    fun registerCommand(command: Command) {
        commands[command.name] = command
    }

    override fun onMessageCreate(discord: DiscordAPI, message: Message) {
        logger.trace("Received message")
        messageCreateCounter.labels(message.channelReceiver.server.id, message.channelReceiver.id).inc()
        if (!message.content.startsWith(prefix)) {
            return
        }
        messageCmdPrefixCreateCounter.labels(message.channelReceiver.server.id, message.channelReceiver.id).inc()
        val cmdLine = message.content.substring(prefix.length)
        val (cmdName, cmdArgs) = parseCmdLine(cmdLine)
        val command = commands[cmdName] ?: return
        if (!permissionManager.hasPermission(message.author, message.channelReceiver.server, discord, command)) {
            message.reply("You don't have permissions to do this")
            return
        }
        logger.debug("Running command {}", command.name)
        command.handle(message, cmdArgs)
    }

    private fun parseCmdLine(cmdLine: String): Pair<String, String?> {
        val cmdNameEnd = cmdLine.indexOf(' ')
        return if (cmdNameEnd != -1) {
            Pair(cmdLine.substring(0, cmdNameEnd), cmdLine.substring(cmdNameEnd + 1))
        } else {
            Pair(cmdLine, null)
        }
    }
}

