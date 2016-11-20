package com.hea3ven.gfriendmedianews.util

private val urlRegex = """https?://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-z]{2,4}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)""".toRegex()

fun  escapeLinks(text: String): String {
	return urlRegex.replace(text,"<$0>")
}

