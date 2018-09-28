package com.hea3ven.gfriendmedianews.commands

import de.btobastian.javacord.entities.message.Message
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.Namespace
import org.slf4j.LoggerFactory

abstract class Command(val name: String, private val helpSummary: String, val helpDetailed: String? = null,
        val requiresAdmin: Boolean = false) {

    protected val logger = LoggerFactory.getLogger(javaClass)!!

    abstract fun action(message: Message, args: Namespace)

    protected val argParser = ArgumentParsers.newFor(name).addHelp(false).build().description(helpSummary)!!

    fun getHelpSummary(mgr: CommandManager): String {
        val cmdLine = argParser.formatUsage().replace("usage: ", "").trim()
        return " **${mgr.prefix}$cmdLine**: $helpSummary"
    }

    internal fun handle(message: Message, cmdLine: String?) {
        val cmdArgs = if (cmdLine == null) emptyArray() else ArgumentTokenizer.tokenize(cmdLine).toTypedArray()
        val args = argParser.parseArgs(cmdArgs)
        action(message, args)
    }
}