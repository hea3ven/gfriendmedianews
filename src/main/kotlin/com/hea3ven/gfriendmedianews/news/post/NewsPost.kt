package com.hea3ven.gfriendmedianews.news.post

import com.hea3ven.gfriendmedianews.Config
import com.hea3ven.gfriendmedianews.news.source.NewsSource
import de.btobastian.javacord.entities.Channel
import java.util.*

open class NewsPost(val date: Date, val userName: String, val newsSrc: NewsSource, val content: String) {

	fun post(channel: Channel) {
		val dateStr = Config.dateFmt.format(date)
		val title = "__" + dateStr + " KST " + userAction() + ":__\n"
		val msg = "==========\n" + title + "\n" + content + "\n"
		channel.sendMessage(msg)
	}

	open fun userAction(): String = "**" + userName + "** " + newsSrc.verb
}