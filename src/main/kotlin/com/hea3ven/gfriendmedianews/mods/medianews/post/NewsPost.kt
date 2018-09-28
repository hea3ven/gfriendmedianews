package com.hea3ven.gfriendmedianews.mods.medianews.post

import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.mods.medianews.source.NewsSource
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import java.awt.Color
import java.util.Date

open class NewsPost(val color: Color, val date: Date, val userName: String, val userUrl: String, val userIcon: String,
                    val newsSrc: NewsSource, val url: String, val content: String, val mediaUrls: List<String>) {

    fun post(channel: Channel) {
        val dateStr = Config.dateFmt.format(date)
        val embed = EmbedBuilder()
                .setColor(color)
                .setAuthor(userName, userUrl, userIcon)
                .setTitle(userAction())
                .setUrl(url)
                .setDescription(content)
                .addField("date", "$dateStr KST", true)
        if (mediaUrls.size == 1)
            embed.setImage(mediaUrls[0])
        channel.sendMessage(null, embed)
        if (mediaUrls.size > 1) {
            mediaUrls.forEach {
                channel.sendMessage(null, EmbedBuilder().setImage(it))
            }
        }
    }

    open fun userAction(): String = newsSrc.verb
}