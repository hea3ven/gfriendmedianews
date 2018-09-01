package com.hea3ven.gfriendmedianews.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscordKtTest {
    @Test
    fun testEscapeLinks() {
        val values = listOf(
                "text with no urls" to "text with no urls",
                "text with url http://test.com/test in the middle"
                        to "text with url <http://test.com/test> in the middle",
                "text with url in the end http://test.com/test"
                        to "text with url in the end <http://test.com/test>",
                "http://test.com/test text with url in the beginning"
                        to "<http://test.com/test> text with url in the beginning",
                "http://test.com/test text with two urls http://test.com/test"
                        to "<http://test.com/test> text with two urls <http://test.com/test>",
                "text with short url https://t.co/0StnagDUxM" to
                        "text with short url <https://t.co/0StnagDUxM>"
        )

        values.forEach {
            assertEquals(it.second, escapeLinks(it.first))
        }
    }
}