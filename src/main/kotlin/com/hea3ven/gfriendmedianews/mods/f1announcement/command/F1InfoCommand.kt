package com.hea3ven.gfriendmedianews.mods.f1announcement.command

import com.hea3ven.gfriendmedianews.commands.Command
import com.hea3ven.gfriendmedianews.mods.f1announcement.F1AnnouncementModule
import de.btobastian.javacord.entities.message.Message
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.property.Categories
import net.sourceforge.argparse4j.inf.Namespace
import java.text.MessageFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class F1InfoCommand(private val mod: F1AnnouncementModule) :
        Command("f1info", "Display information about next race.") {

    init {
        argParser.addArgument("timezone").setDefault("Z")
    }

    override fun action(message: Message, args: Namespace) {
        val targetZoneId = ZoneId.of(args.getString("timezone")).normalized()
        val now = ZonedDateTime.now()
        val events = mod.getNextEvents(now)
        if (events.isEmpty()) {
            message.reply("There's no events")
            return
        }
        val nextEvent = events.first()
        val currentEvents = events.filter { it.location == nextEvent.location }
        val race = currentEvents.first { "Grand Prix" == it.getProperty<Categories>(Property.CATEGORIES).value }
        var info = "Current race: **" + race.summary.value + "**\n"
        info += "\tOn: **" + race.location.value + "**\n\n"
        info += "Events:\n"
        currentEvents.forEach {
            val startTime = ZonedDateTime.ofInstant(it.startDate.date.toInstant(),
                                                    it.startDate.timeZone?.toZoneId() ?: ZoneId.of("Z"))
            val timeLeft = formatRemainingTime(now, startTime)
            val formattedStartDate = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm VV")
                    .format(startTime.withZoneSameInstant(targetZoneId))
            info += "\t- **" + it.summary.value + "** at " + formattedStartDate + " (in **" + timeLeft + "**)\n"
        }
        message.reply(info)
    }

    private fun formatRemainingTime(now: ZonedDateTime, startTime: ZonedDateTime): String {
        for (it in arrayOf(ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES)) {
            val diff = it.between(now, startTime)
            if (diff > 0) {
                return MessageFormat.format("{0} {1}", diff.toString(), it)
            }
        }
        return "Now"
    }
}