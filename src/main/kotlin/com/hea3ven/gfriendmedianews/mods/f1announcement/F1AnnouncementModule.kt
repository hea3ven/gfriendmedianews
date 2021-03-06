package com.hea3ven.gfriendmedianews.mods.f1announcement

import com.hea3ven.gfriendmedianews.ChinguBot
import com.hea3ven.gfriendmedianews.mods.Module
import com.hea3ven.gfriendmedianews.mods.f1announcement.command.F1ConfigCommand
import com.hea3ven.gfriendmedianews.mods.f1announcement.command.F1InfoCommand
import com.hea3ven.gfriendmedianews.mods.f1announcement.dao.F1ServerConfigDao
import com.hea3ven.gfriendmedianews.mods.f1announcement.dao.F1ServerConfigDaoFactory
import com.hea3ven.gfriendmedianews.mods.f1announcement.model.F1ServerConfig
import com.hea3ven.gfriendmedianews.persistance.PersistenceTransaction
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.ParserException
import net.fortuna.ical4j.filter.Filter
import net.fortuna.ical4j.filter.HasPropertyRule
import net.fortuna.ical4j.filter.PeriodRule
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Categories
import org.apache.commons.collections4.functors.OrPredicate
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import java.io.StringReader
import java.lang.Long.min
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.concurrent.thread

//val f1CalendarUrl = URL("https://www.f1calendar.com/download/f1-calendar_p1_p2_p3_q_gp.ics")
val f1CalendarUrl = F1AnnouncementModule::class.java.classLoader.getResource("f1-calendar_p1_p2_p3_q_gp.ics")!!

class F1AnnouncementModule(val bot: ChinguBot) : Module {

    init {
        bot.persistence.registerDaoFactory(F1ServerConfigDao::class.java, F1ServerConfigDaoFactory())
    }

    private val logger = LoggerFactory.getLogger(F1AnnouncementModule::class.java)

    override val commands = listOf(F1InfoCommand(this), F1ConfigCommand(this))

    private lateinit var f1Calendar: Calendar

    internal val serverConfigs = mutableMapOf<String, F1ServerConfig>()


    override fun onConnect(tx: PersistenceTransaction) {
        super.onConnect(tx)

        try {
            logger.info("Downloading F1 calendar")
            f1CalendarUrl.openStream().use {
                try {
                    val data = InputStreamReader(it).readText()
                    logger.info("Downloaded F1 calendar")
                    f1Calendar = CalendarBuilder().build(StringReader(data))
                    logger.info("Parsed F1 calendar")
                } catch (e: ParserException) {
                    logger.error("Could not load calendar", e)
                    return
                }
            }
        } catch (e: Exception) {
            logger.error("Unknown error", e)
            return
        }
        for (server in bot.discord.servers) {
            var serverConfig = tx.getDao(F1ServerConfigDao::class.java).findByServerId(server.id)
            if (serverConfig == null) {
                serverConfig = F1ServerConfig(server.id!!)
                tx.getDao(F1ServerConfigDao::class.java).persist(serverConfig)
            }
            serverConfigs[serverConfig.serverId] = serverConfig
        }
        logger.info("Starting background thread")
        thread {
            logger.info("Started background thread")
            while (!bot.stop) {
                try {
                    val now = ZonedDateTime.now()
                    val nextEvents = getNextMainEvents(now)
                    if (nextEvents.isEmpty()) {
                        logger.trace("No events, sleeping for 6 hours")
                        Thread.sleep(6 * 60 * 60 * 1000)
                        continue
                    }
                    val nextEvent = nextEvents.first()
                    val startTime = ZonedDateTime.ofInstant(nextEvent.startDate.date.toInstant(),
                            nextEvent.startDate.timeZone?.toZoneId() ?: ZoneId.of("Z"))
                    val hours = ChronoUnit.HOURS.between(now, startTime)
                    if (hours > 5) {
                        logger.trace("Sleeping for {} hours", hours - 5)
                        Thread.sleep(min(5, (hours - 5)) * 60 * 60 * 1000)
                    } else if (hours > 2) {
                        logger.trace("Sleeping for {} hours", hours - 2)
                        Thread.sleep((hours - 2) * 60 * 60 * 1000)
                    } else {
                        val minutes = ChronoUnit.MINUTES.between(now, startTime)
                        if (minutes > 15) {
                            logger.trace("Sleeping for {} minutes", minutes - 14)
                            Thread.sleep((minutes - 14) * 60 * 1000)
                        } else {
                            logger.trace("Broadcasting the start of event {}", nextEvent.summary.value)
                            broadcastEventNotification(nextEvent)
                            Thread.sleep(20 * 60 * 60 * 1000)
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Unexpected error", e)
                }
            }
        }
    }

    private fun broadcastEventNotification(nextEvent: VEvent) {
        serverConfigs.values.filter(F1ServerConfig::enabled).forEach {
            val msg = "Get ready " + it.mentionRole + ", " + nextEvent.summary.value + " is about to start."
            bot.discord.getServerById(it.serverId).getChannelById(it.channel).sendMessage(msg)
        }
    }

    internal fun getNextEvents(now: ZonedDateTime) = Filter<VEvent>(
            arrayOf(PeriodRule(Period(DateTime(Date.from(now.toInstant())), Dur(Int.MAX_VALUE)))),
            Filter.MATCH_ALL).filter(f1Calendar.getComponents(Component.VEVENT))

    private fun getNextMainEvents(now: ZonedDateTime) = Filter<VEvent>(
            arrayOf(PeriodRule(Period(DateTime(Date.from(now.toInstant())), Dur(Int.MAX_VALUE))),
                    OrPredicate(HasPropertyRule(Categories("Qualifying Session")),
                            HasPropertyRule(Categories("Grand Prix")))), Filter.MATCH_ALL).filter(
            f1Calendar.getComponents(Component.VEVENT))
}

